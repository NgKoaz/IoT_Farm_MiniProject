import os
import time

from dotenv import load_dotenv
from mqtt import Mqtt as MyMqtt


class Utils:
    pass


class Main:
    subscribedTopicList = ["V2"]

    def __init__(self):
        # Load environment variables
        load_dotenv()
        self.BROKER = os.getenv("BROKER")
        self.USERNAME = os.getenv("USERNAME")
        self.PASSWORD = os.getenv("PASSWORD")

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

    def subscribeTopics(self):
        for topic in self.subscribedTopicList:
            self.myMqtt.subscribe(topic=topic)

    def reconnectToMqttBroker(self):
        self.myMqtt.connect(self.BROKER, 1883, self.USERNAME, self.PASSWORD)

    def loop(self):
        while True:
            time.sleep(1)


Main()




