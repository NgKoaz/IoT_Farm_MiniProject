import threading
import subprocess

import paho.mqtt.client as mqtt
from Adafruit_IO.errors import MQTTError
from paho.mqtt.enums import CallbackAPIVersion, MQTTErrorCode


class Mqtt:
    server = ""
    port = None
    QOS = 1
    onConnect = None
    onMessage = None
    username = None
    password = None
    isConnected = False

    def __init__(self):
        self.client = mqtt.Client(callback_api_version=CallbackAPIVersion.VERSION1)

    def on_connect(self, client, userdata, flags, rc):
        if self.onConnect:
            self.onConnect(rc == 0)
            self.isConnected = True
        else:
            if rc == 0:
                print(f"Connected to MQTT broker! [{self.server}:{self.port}]")
            else:
                print("Failed to connect to MQTT broker, return code: ", rc)

    # Callback function when a message is received from the broker
    def on_message(self, client, userdata, msg):
        if self.onMessage:
            self.onMessage(msg.topic, msg.payload.decode("utf-8"))
        else:
            print("Received message: ", str(msg.payload.decode("utf-8")) + " |Topic: " + str(msg.topic))

    def on_subscribe(self, client, userdata, mid, granted_qos):
        print("Subscribed to topic!")

    def on_publish(client, userdata, mid, granted_qos):
        print("Publish: OK!")

    def on_disconnect(self, client, userdata, rc):
        print(f"Disconnected with result code: {rc}")
        self.isConnected = False

    def subscribe(self, topic):
        try:
            self.client.subscribe(topic=self.username + "/feeds/" + topic, qos=self.QOS)
        except MQTTErrorCode as e:
            print(e)

    def publish(self, topic, payload):
        try:
            self.client.publish(topic=self.username + "/feeds/" + topic, payload=payload, qos=self.QOS)
        except mqtt.MQTTMessageInfo as e:
            print(e)

    def setCallback(self, onConnect=None, onMessage=None):
        if onConnect:
            self.onConnect = onConnect
        if onMessage:
            self.onMessage = onMessage

    def loop(self):
        try:
            self.client.loop_forever()
        except Exception as e:
            print("MQTT Error: Perhaps the connection has been interrupted!")

    def connect(self, server, port, username, password):
        self.client.username_pw_set(username, password)

        # Set up callback functions
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_subscribe = self.on_subscribe
        self.client.on_publish = self.on_publish
        self.client.on_disconnect = self.on_disconnect

        # Connect to the MQTT broker
        self.server = server
        self.port = port
        self.username = username
        self.password = password
        self.client.connect(server, port)

        # Loop to maintain the connection and process incoming messages
        thread = threading.Thread(target=self.loop)
        thread.daemon = True
        thread.start()

