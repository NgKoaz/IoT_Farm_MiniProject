import json
import os
import threading
import time
import random
import datetime


from dotenv import load_dotenv

from model.mqtt.mqtt_topic import MqttTopic
from model.mqtt.schedule import Schedule, ScheduleType
from model.mqtt.sensor_data import SensorData, SensorDataType
from services.mqtt import Mqtt as MyMqtt
from services.my_firestore import MyFirestore


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
        # This is for local schedule list, always update each 5 minutes.
        self.scheduleList = []

        # Establish MQTT services
        self.myMqtt = MyMqtt()
        # Set Callback
        self.myMqtt.setCallback(onConnect=self.onConnect, onMessage=self.onMessage)
        # Connect
        self.myMqtt.connect(self.BROKER, 1883, self.USERNAME, self.PASSWORD)

        # We need one infinity loop to maintain program
        self.loop()

    def onConnect(self, isSuccessful):
        # After connect is successful.
        if isSuccessful:
            # Subscribe all topics we list before
            self.subscribeTopics()
        else:
            print("[ERROR] Connection failed!")

    def subscribeTopics(self):
        for topic in MqttTopic.subscriptionList:
            self.myMqtt.subscribe(topic=topic)

    def reconnectToMqttBroker(self):
        self.myMqtt.connect(self.BROKER, 1883, self.USERNAME, self.PASSWORD)

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

    def addSchedule(self, schedule: Schedule):
        formatted_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
        schedule.setId(formatted_time)

        # Lack checking

        self.scheduleList.append(schedule)
        self.myFirestore.putSchedule(formatted_time, json.loads(schedule.toJsonString()))

        self.publishSchedule(schedule)

    def deleteSchedule(self, schedule: Schedule):
        self.myFirestore.deleteSchedule(schedule.scheduleId)

    def updateSchedule(self, schedule: Schedule):
        self.myFirestore.updateSchedule(schedule.scheduleId, json.loads(schedule.toJsonString()))

    def handleScheduleRequest(self, payload: str):
        schedule = Schedule.importFromJsonString(payload)

        if schedule.type == ScheduleType.ADD:
            self.addSchedule(schedule)
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

    def loop(self):
        while True:
            self.publishSensorData(SensorData(SensorDataType.TEMPERATURE, random.randint(0, 100)))
            self.publishSensorData(SensorData(SensorDataType.SOIL_MOISTURE, random.randint(0, 100)))
            time.sleep(10)


Main()




