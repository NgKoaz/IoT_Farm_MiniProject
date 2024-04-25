import os
import time
import json
import random


from dotenv import load_dotenv

from data.enums.SensorDataType import SensorDataType
from data.model.mqtt.MqttTopic import MqttTopic
from data.model.mqtt.ScheduleResponsePayload import ScheduleResponsePayload
from data.model.mqtt.SensorDataPayload import SensorDataPayload
from connection.mqtt import Mqtt as MyMqtt


class Main:

    def __init__(self):
        # Load environment variables
        load_dotenv()
        self.BROKER = os.getenv("BROKER")
        self.USERNAME = os.getenv("USER")
        self.PASSWORD = os.getenv("KEY")
        print(self.BROKER, self.USERNAME, self.PASSWORD)

        # Establish MQTT connection
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
        for topic in MqttTopic.subscribedTopicList:
            self.myMqtt.subscribe(topic=topic)

    def reconnectToMqttBroker(self):
        self.myMqtt.connect(self.BROKER, 1883, self.USERNAME, self.PASSWORD)

    def onMessage(self, topic, payload):
        topic = topic.split("/")[-1]
        print("Topic: " + topic + "| Payload: " + payload)
        if topic == "V2":
            self.handleScheduleRequest(payload)

    def handleScheduleRequest(self, payload: str):
        scheduleResponsePayload = ScheduleResponsePayload.importFromJsonString(payload)
        self.publishScheduleResponse(scheduleResponsePayload)

    def publishSensorData(self, sensorDataPayload: SensorDataPayload):
        self.myMqtt.publish(
            MqttTopic.sensorDataTopic,
            payload=sensorDataPayload.toStringInJsonForm()
        )

    def publishScheduleResponse(self, scheduleResponsePayload: ScheduleResponsePayload):
        self.myMqtt.publish(
            MqttTopic.scheduleTopicResponse,
            payload=scheduleResponsePayload.toStringJson()
        )

    def loop(self):
        while True:
            self.publishSensorData(SensorDataPayload(SensorDataType.TEMPERATURE, random.randint(0, 100)))
            self.publishSensorData(SensorDataPayload(SensorDataType.SOIL_MOISTURE, random.randint(0, 100)))
            time.sleep(10)


Main()




