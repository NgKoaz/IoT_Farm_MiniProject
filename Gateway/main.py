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
from services.uart import Uart


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
        self.scheduleIdRunning = ""

        # Create scheduler for handling schedule from app.
        self.scheduler1 = Scheduler1()
        # Initialize Uart
        self.uart = Uart(self.scheduler1)

        # Add 2 tasks for reading sensor
        self.scheduler1.SCH_AddTask(Task(pTask=self.uart.readTemperature, delay=0, period=3))
        self.scheduler1.SCH_AddTask(Task(pTask=self.uart.readMoisture, delay=0.02, period=3))

        self.scheduler2 = Scheduler2()
        self.scheduler2.setOnTaskDone(on_task_done=self.onTaskDone)
        self.scheduler2.setOnSaveHistory(on_save_history=self.onSaveHistory)

        # Initialize scheduler2
        self.setOffAllScheduleInDatabase()

        # Establish MQTT services
        self.myMqtt = MyMqtt(self.BROKER, self.USERNAME, self.PASSWORD)
        self.myMqtt.addOnMessage(onMessage=self.onMessage)
        self.myMqtt.connect()

        # We need one infinity loop to maintain program
        self.loop()

    def onTaskDone(self, scheduleTask: ScheduleTask):
        scheduleTask.schedule.isOn = 0
        scheduleTask.schedule.type = ScheduleType.UPDATE
        js = json.loads(scheduleTask.schedule.toJsonString())
        del js["email"]
        del js["type"]
        del js["error"]
        self.myFirestore.updateSchedule(scheduleTask.schedule.scheduleId, js)
        self.publishSchedule(scheduleTask.schedule)

    def onSaveHistory(self, scheduleTask: ScheduleTask):
        js = json.loads(scheduleTask.schedule.toJsonString())
        del js["email"]
        del js["type"]
        del js["isOn"]
        del js["error"]
        self.myFirestore.putHistory(js)

    def setOffAllScheduleInDatabase(self):
        col = self.myFirestore.getSchedules()
        for doc in col:
            schedule = Schedule(
                scheduleId=doc.get("scheduleId"),
                volume=doc.get("volume"),
                ratio=str(doc.get("ratio")),
                date=doc.get("date"),
                weekday=str(doc.get("weekday")),
                time=doc.get("time"),
                isOn=doc.get("isOn")
            )
            if schedule.isOn == 1:
                schedule.isOn = 0
                self.myFirestore.updateSchedule(schedule.scheduleId, json.loads(schedule.toJsonString()))

    def taskScheduler2(self, schedule):
        self.scheduleIdRunning = schedule.scheduleId
        print(schedule.scheduleId)
        # Start adding task
        # TO DO
        # Schedule:
        # schedule.volume: Total will be pumped
        # schedule.ratio[0]: ratio of water in total.
        # schedule.ratio[1]: ratio of mixer1 in total.
        # schedule.ratio[2]: ratio of mixer2 in total.
        # schedule.ratio[3]: ratio of mixer3 in total.
        # schedule.ratio[4]: ratio of area1 in total.
        # schedule.ratio[5]: ratio of area2 in total.
        # schedule.ratio[6]: ratio of area3 in total.

        #

        # self.scheduler1.SCH_AddTask(Task(pTask=self.uart.setMixer1, args=TaskArgument(state=1), delay=1, period=0))

        # TO DO
        # End adding task
        pass

    def onMessage(self, topic, payload):
        topic = topic.split("/")[-1]
        print("Topic: " + topic + "| Payload: " + payload)

        if topic == "V2":
            thread = threading.Thread(target=self.handleScheduleRequest, args=(payload,))
            thread.daemon = True
            thread.start()

    def addScheduleRequest(self, schedule: Schedule):
        formatted_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
        schedule.setId(formatted_time)

        # Add schedule Task here
        scheduleTask = ScheduleTask(pTask=self.taskScheduler2, schedule=schedule)
        self.scheduler2.SCH_AddTask(scheduleTask)

        js = json.loads(schedule.toJsonString())
        del js["email"]
        del js["type"]
        del js["error"]
        self.myFirestore.putSchedule(formatted_time, js)

        self.publishSchedule(schedule)

    def deleteSchedule(self, schedule: Schedule):
        if schedule.scheduleId:
            if self.scheduler1.numTaskRunning > 2 and self.scheduleIdRunning == schedule.scheduleId:
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
            if self.scheduler1.numTaskRunning > 2 and self.scheduleIdRunning == schedule.scheduleId:
                schedule.error = "This schedule is executing, you can delete until it's done"
            elif self.myFirestore.isScheduleExist(schedule.scheduleId):
                if schedule.isOn == 1:
                    self.scheduler2.SCH_DeleteScheduleTask(schedule.scheduleId)
                    self.scheduler2.SCH_AddTask(ScheduleTask(pTask=self.taskScheduler2, schedule=schedule))

                js = json.loads(schedule.toJsonString())
                del js["email"]
                del js["type"]
                del js["error"]
                self.myFirestore.updateSchedule(schedule.scheduleId, js)
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
        counter = 5
        counterTime = 5
        while True:
            counter -= 1
            if counter <= 0:
                counter = 5
                # self.publishSensorData(SensorData(SensorDataType.TEMPERATURE, self.uart.tempValue))
                # self.publishSensorData(SensorData(SensorDataType.SOIL_MOISTURE, self.uart.moisValue))
                print("Temp: " + str(self.uart.tempValue))
                print("Mois: " + str(self.uart.moisValue))

            counterTime -= 1
            if counterTime <= 0:
                counterTime = 5
                self.publishCurrentTime()
            TimeManager.sleep(1)


Main()




