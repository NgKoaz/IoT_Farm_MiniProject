package bku.iot.farmapp.services.global;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MyMqttClient implements MqttCallback {
    private static MyMqttClient instance;
    private static final String TAG = MyMqttClient.class.getSimpleName();
    private String broker;
    private String username;
    private String password;
    private String groupPrefix = "";
    private final byte QOS = 1;
    private final String clientId = MqttClient.generateClientId();
    private final MemoryPersistence memoryPersistence = new MemoryPersistence();
    private OnMessageArrived onMessageArrived = null;
    private OnDeliveryComplete onDeliveryComplete = null;
    private MqttClient client;
    MqttConnectOptions opts;

    public MyMqttClient(){
        this.opts = new MqttConnectOptions();
    }

    public static MyMqttClient gI() {
        if (instance == null){
            instance = new MyMqttClient();
        }
        return instance;
    }

    private void saveCurrentBrokerInfo(String broker, String username, String password){
        this.broker = broker;
        this.username = username;
        this.password = password;
    }

    public void connect(String broker, String username, String password, String groupPrefix, HandleConnectionResult listener){
        try {
            this.client = new MqttClient(broker, clientId, memoryPersistence);
            this.client.setCallback(this); // Set the callback

            this.saveCurrentBrokerInfo(broker, username, password);

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

    public void disconnect(HandleDisconnectionResult listener){
        if (this.client == null || !this.client.isConnected()){
            Log.d(TAG, "No connection found to disconnect!");
            this.saveCurrentBrokerInfo("", "", "");
            if (listener != null)
                listener.onSuccess();
            return;
        }
        try {
            this.client.disconnect();
            Log.d(TAG, "Disconnect to broker successfully!");
            this.saveCurrentBrokerInfo("", "", "");
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
        try {
            this.client.connect(opts);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }

    public boolean hasOnMessageArrived(){
        return (onMessageArrived != null);
    }

    public void setOnMessageArrived(OnMessageArrived onMessageArrived){
        this.onMessageArrived = onMessageArrived;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Connection to MQTT broker lost. Cause: " + cause.getMessage());
        handleConnectionLost();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (onMessageArrived != null) {
            // Process the message.
            String payload = new String(message.getPayload());
            Log.d(TAG, "TOPIC: " + topic);
            Log.d(TAG, "PAYLOAD: " + payload);
            onMessageArrived.onArrived(topic, payload);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        if (onDeliveryComplete != null) {
            onDeliveryComplete.onComplete();
        }
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

    public interface OnMessageArrived{
        void onArrived(String topic, String payload);
    }

    public interface OnDeliveryComplete{
        void onComplete();
    }
}
