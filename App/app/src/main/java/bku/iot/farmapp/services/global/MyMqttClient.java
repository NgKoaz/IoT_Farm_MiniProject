package bku.iot.farmapp.services.global;

import android.health.connect.datatypes.ExerciseLap;
import android.os.Handler;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyMqttClient implements MqttCallback {
    private static MyMqttClient instance;
    private static final String TAG = MyMqttClient.class.getSimpleName();
    public String broker;
    public String username;
    public String password;
    private String groupPrefix = "";
    private final byte QOS = 1;
    private final String clientId = MqttClient.generateClientId();
    private final MemoryPersistence memoryPersistence = new MemoryPersistence();
    private OnDeliveryComplete onDeliveryComplete = null;
    private MqttClient client;
    private final MqttConnectOptions opts;
    private final List<MessageObserver> onMessageObservers = new ArrayList<>();
    private List<String> subscribeTopicList;


    public MyMqttClient(){
        this.opts = new MqttConnectOptions();
    }


    public static MyMqttClient gI() {
        if (instance == null){
            instance = new MyMqttClient();
        }
        return instance;
    }

    // Method to register observers
    public void registerObserver(MessageObserver observer) {
        onMessageObservers.add(observer);
    }

    // Method to unregister observers
    public void unregisterObserver(MessageObserver observer) {
        try {
            onMessageObservers.remove(observer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Method to notify observers when a new message is received
    public void notifyMessageArrived(String topic, String payload) {
        String[] parts = topic.split("/");
        topic = parts[parts.length - 1];
        for (MessageObserver observer : onMessageObservers) {
            try {
                observer.onMessageReceived(topic, payload);
            } catch (Exception e){
                e.printStackTrace();
                unregisterObserver(observer);
            }
        }
    }

    private void saveBrokerCredential(String broker, String username, String password){
        this.broker = broker;
        this.username = username;
        this.password = password;
    }

    public void connect(String broker, String username, String password, String groupPrefix, HandleConnectionResult listener){
        try {
            this.client = new MqttClient(broker, clientId, memoryPersistence);
            this.client.setCallback(this); // Set the callback

            this.saveBrokerCredential(broker, username, password);

            // Set option
            this.opts.setCleanSession(true);
            this.opts.setUserName(username);
            this.opts.setPassword(password.toCharArray());

            this.client.connect(opts);

            this.groupPrefix = groupPrefix;

            if (listener != null)
                listener.onSuccess();
        } catch (MqttException e){
            e.printStackTrace();

            if (listener != null)
                listener.onFailure(e.getMessage());
        }
    }

    public void publish(String topic, String payload){
        if (!client.isConnected()) {
            Log.e(TAG, "Connection hasn't established yet!");
            return;
        }
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(this.QOS);
        try {
            this.client.publish(this.groupPrefix + topic, message);
        } catch (MqttException me) {
            me.printStackTrace();;
        }
    }

    public void subscribe(String topic, HandleSubscribingResult listener){
        if (!client.isConnected()) {
            Log.e(TAG, "Connection hasn't established yet!");
            if (listener != null)
                listener.onFailure(topic, "No connection!!");
            return;
        }
        try {
            this.client.subscribe(this.groupPrefix + topic);
            if (listener != null)
                listener.onSuccess(topic);
        } catch (MqttException me){
            me.printStackTrace();
            if (listener != null)
                listener.onFailure(topic, me.getMessage());
        }
    }

    public void subscribeTopics(List<String> topicList){
        this.subscribeTopicList = topicList;
        for (String topic : topicList){
            subscribe(topic, new HandleSubscribingResult() {
                @Override
                public void onSuccess(String topic) {
                    Log.d(TAG, "Subscribed: " + topic);
                }

                @Override
                public void onFailure(String topic, String errorMessage) {
                    Log.d(TAG, "Subscribe fail: " + topic + "| ErrorMessage: " + errorMessage);
                }
            });
        }
    }

    public void disconnect(HandleDisconnectionResult listener){
        if (this.client == null || !this.client.isConnected()){
            Log.d(TAG, "No connection found to disconnect!");
            this.saveBrokerCredential("", "", "");
            if (listener != null)
                listener.onSuccess();
            return;
        }
        try {
            this.client.disconnect();
            Log.d(TAG, "Disconnect to broker successfully!");
            this.saveBrokerCredential("", "", "");
            if (listener != null)
                listener.onSuccess();
        } catch (MqttException me){
            me.printStackTrace();
            if (listener != null)
                listener.onFailure(me.getMessage());
        }
    }

    private void handleConnectionLost(){
        // We can inform user about disconnect right here
        // homeController.handleConnectionLost();
        Log.d(TAG, "Reconnect MQTT Broker!!!");
        new Thread(() -> {
            connect(broker, username, password, groupPrefix, new HandleConnectionResult() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Reconnect successful!");
                    subscribeTopics(subscribeTopicList);
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Retry the connection after a delay
                    Log.d(TAG, "Reconnect failed! Reconnect again in 3 seconds.");
                    try {
                        Thread.sleep(3000); // Wait for 3 seconds before retrying
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handleConnectionLost(); // Retry the connection
                }
            });
        }).start();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Connection to MQTT broker lost. Cause: " + cause.getMessage());
        handleConnectionLost();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        Log.d(TAG, "TOPIC: " + topic);
        Log.d(TAG, "PAYLOAD: " + payload);
        notifyMessageArrived(topic, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        if (onDeliveryComplete != null) {
            onDeliveryComplete.onComplete();
        }
    }

    // Define the observer interface
    public interface MessageObserver {
        void onMessageReceived(String topic, String payload);
    }

    public interface HandleConnectionResult{
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface HandleDisconnectionResult{
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface HandleSubscribingResult{
        void onSuccess(String topic);
        void onFailure(String topic, String errorMessage);
    }

    public interface OnDeliveryComplete{
        void onComplete();
    }
}
