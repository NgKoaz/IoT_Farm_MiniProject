import json
import os
import threading
import random
import datetime

from dotenv import load_dotenv
from model.mqtt.mqtt_topic import MqttTopic
from model.mqtt.schedule import Schedule, ScheduleType
from model.mqtt.sensor_data import SensorData, SensorDataType
from services.mqtt import Mqtt
from services.my_firestore import MyFirestore
from utils.time_manager import TimeManager
from scheduler.scheduler2 import Scheduler2, ScheduleTask
from scheduler.scheduler1 import Scheduler1, Task, TaskArgument


class MyMqtt:
    def __init__(self, broker, username, password):
        # Save credential
        self.broker = broker
        self.username = username
        self.password = password

        # Initializing...
        self.mqtt = Mqtt()

        # Set Callback
        self.mqtt.setCallback(onConnect=self.onConnect)

    def onConnect(self, isSuccessful):
        # After connect is successful.
        if isSuccessful:
            # Subscribe all topics we list before
            self.subscribeTopics()
        else:
            print("[ERROR] Connection failed!")

    def subscribeTopics(self):
        for topic in MqttTopic.subscriptionList:
            self.mqtt.subscribe(topic=topic)

    def reconnectToMqttBroker(self):
        self.mqtt.connect(self.broker, 1883, self.username, self.password)

    def addOnMessage(self, onMessage):
        self.mqtt.setCallback(onMessage=onMessage)

    def connect(self):
        self.mqtt.connect(self.broker, 1883, self.username, self.password)

    def publish(self, topic, payload):
        self.mqtt.publish(topic, payload)


class Main:
    def __init__(self):
        # Load environment variables
        load_dotenv()
        self.BROKER = os.getenv("BROKER")
        self.USERNAME = os.getenv("USER")
        self.PASSWORD = os.getenv("KEY")
        print(self.BROKER, self.USERNAME, self.PASSWORD)

        # Establish Firebase Connection
        self.myFirestore = MyFirestore(self.BROKER, self.USERNAME, self.PASSWORD)
        # This is for local schedule list, always update for Firebase each 5 minutes.
        self.scheduleList = []

        # Create scheduler for handling schedule from app.
        self.scheduler1 = Scheduler1()
        self.scheduler2 = Scheduler2()
        self.scheduler2.setOnTaskDone(on_task_done=self.onTaskDone)

        # Initialize scheduler2
        self.initializeScheduler2()

        # Establish MQTT services
        self.myMqtt = MyMqtt(self.BROKER, self.USERNAME, self.PASSWORD)
        self.myMqtt.addOnMessage(onMessage=self.onMessage)
        self.myMqtt.connect()

        # We need one infinity loop to maintain program
        self.loop()

    def onTaskDone(self, scheduleTask: ScheduleTask):
        scheduleTask.schedule.isOn = 0
        scheduleTask.schedule.type = ScheduleType.UPDATE
        self.myFirestore.updateSchedule(scheduleTask.schedule.scheduleId, json.loads(scheduleTask.schedule.toJsonString()))
        self.publishSchedule(scheduleTask.schedule)

    def initializeScheduler2(self):
        col = self.myFirestore.getSchedules()
        for doc in col:
            schedule = Schedule(
                scheduleId=doc.get("scheduleId"),
                volume=doc.get("volume"),
                ratio=str(doc.get("ratio")),
                date=doc.get("date"),
                weekday=doc.get("weekday"),
                time=doc.get("time"),
                isOn=doc.get("isOn")
            )
            if schedule.isOn == 1:
                self.scheduler2.SCH_AddTask(ScheduleTask(schedule))
                print("Id: " + schedule.date + " has been added!")

    def onMessage(self, topic, payload):
        topic = topic.split("/")[-1]
        print("Topic: " + topic + "| Payload: " + payload)

        if topic == "V2":
            thread = threading.Thread(target=self.handleScheduleRequest, args=(payload,))
            thread.daemon = True
            thread.start()

    def isValidAction(self):
        # TO DO
        return True

    def addScheduleRequest(self, schedule: Schedule):
        formatted_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
        schedule.setId(formatted_time)

        # Add schedule Task
        scheduleTask = ScheduleTask(schedule=schedule)
        self.scheduler2.SCH_AddTask(scheduleTask)

        self.myFirestore.putSchedule(formatted_time, json.loads(schedule.toJsonString()))
        self.publishSchedule(schedule)

    def deleteSchedule(self, schedule: Schedule):
        if schedule.scheduleId:
            if self.scheduler2.runningScheduleId == schedule.scheduleId:
                schedule.error = "This schedule is executing, you can delete until it's done"
            elif self.myFirestore.isScheduleExist(schedule.scheduleId):
                self.scheduler2.SCH_DeleteScheduleTask(schedule.scheduleId)
                self.myFirestore.deleteSchedule(schedule.scheduleId)
            else:
                schedule.error = "This schedule doesn't exist! This mean someone has already deleted before!"
            self.publishSchedule(schedule)
        else:
            print("[ERROR] deleteSchedule but scheduleId is empty!")

    def updateSchedule(self, schedule: Schedule):
        if schedule.scheduleId:
            if self.myFirestore.isScheduleExist(schedule.scheduleId):
                if schedule.isOn == 1:
                    self.scheduler2.SCH_DeleteScheduleTask(schedule.scheduleId)
                    self.scheduler2.SCH_AddTask(ScheduleTask(schedule))
                self.myFirestore.updateSchedule(schedule.scheduleId, json.loads(schedule.toJsonString()))
            else:
                schedule.error = "This schedule doesn't exist!"
            self.publishSchedule(schedule)
        else:
            print("[ERROR] updateSchedule but scheduleId is empty!")

    def handleScheduleRequest(self, payload: str):
        schedule = Schedule.importFromJsonString(payload)
        if schedule.type == ScheduleType.ADD:
            self.addScheduleRequest(schedule)
        elif schedule.type == ScheduleType.DELETE:
            self.deleteSchedule(schedule)
        elif schedule.type == ScheduleType.UPDATE:
            self.updateSchedule(schedule)

    def publishSensorData(self, sensorData: SensorData):
        self.myMqtt.publish(
            MqttTopic.sensorData,
            payload=sensorData.toStringInJsonForm()
        )

    def publishSchedule(self, schedule: Schedule):
        self.myMqtt.publish(
            MqttTopic.scheduleResponse,
            payload=schedule.toJsonString()
        )

    def publishCurrentTime(self):
        curTime = datetime.datetime.now()
        data = {
            "hour": curTime.hour,
            "minute": curTime.minute,
            "day": curTime.day,
            "month": curTime.month,
            "year": curTime.year
        }
        self.myMqtt.publish(MqttTopic.currentTime, payload=json.dumps(data))

    def loop(self):
        counter = 20
        counterTime = 5
        while True:
            counter -= 1
            if counter <= 0:
                counter = 20
                self.publishSensorData(SensorData(SensorDataType.TEMPERATURE, random.randint(0, 100)))
                self.publishSensorData(SensorData(SensorDataType.SOIL_MOISTURE, random.randint(0, 100)))

            counterTime -= 1
            if counterTime <= 0:
                counterTime = 5
                self.publishCurrentTime()
            TimeManager.millisSleep(1)


Main()




