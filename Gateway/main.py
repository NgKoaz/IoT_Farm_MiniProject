import os
import time

from dotenv import load_dotenv

from data.enums.SensorDataType import SensorDataType
from data.model.mqttPayloadModel.SensorDataPayload import SensorDataPayload
from mqtt import Mqtt as MyMqtt


class Utils:
    pass


class Main:
    sensorDataTopic = "V1"
    responseScheduleTopic = "V3"
    subscribedTopicList = ["V2"]

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

    def onMessage(self, topic, payload):
        print("Topic: " + topic + "| Payload: " + payload)
        # if topic

    def subscribeTopics(self):
        for topic in self.subscribedTopicList:
            self.myMqtt.subscribe(topic=topic)

    def reconnectToMqttBroker(self):
        self.myMqtt.connect(self.BROKER, 1883, self.USERNAME, self.PASSWORD)

    def loop(self):
        while True:
            self.publishSensorData(SensorDataPayload(SensorDataType.TEMPERATURE, 24))
            time.sleep(3)

    def publishSensorData(self, sensorDataPayload: SensorDataPayload):
        self.myMqtt.publish("V1", payload=sensorDataPayload.toStringInJsonForm())

    def publishScheduleResponse(self):
        pass




Main()




