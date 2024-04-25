package bku.iot.farmapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.SensorType;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;
import bku.iot.farmapp.view.pages.HistoryActivity;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SettingActivity;
import bku.iot.farmapp.view.pages.SignInActivity;


public class HomeController implements MyMqttClient.MessageObserver {

    private final String TAG = HomeController.class.getSimpleName();
    private final HomeActivity homeActivity;
    private final ActivityResultLauncher<Intent> activityResultLauncher;
    private final Handler mHandler;
    private final List<ScheduleInfo> scheduleInfoList = new ArrayList<>();


    public HomeController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        mHandler = new Handler(Looper.getMainLooper());
        // Before connect to mqtt, we set all event handler first.
        setHandlerForMqtt();
        // Connect to default MQTT Broker
        connectToMqttBroker(
                "tcp://mqtt.ohstem.vn",
                "FarmApp_IOT",
                ""
        );

        // This is for startActivityForResult function
        activityResultLauncher = homeActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == ActivityResultCode.SIGN_OUT) {
                        navigateToSignInPage();
                    }
                }
        );
    }

    private void setHandlerForMqtt(){
        MyMqttClient.gI().registerObserver(this);
    }

    private void connectToMqttBroker(String brokerServer, String username, String password){
        MyMqttClient.gI().connect(
                brokerServer,
                username,
                password,
                username + "/feeds/",
                new MyMqttClient.HandleConnectionResult() {
                    @Override
                    public void onSuccess() {
                        subscribeTopic();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        ToastManager.showToast(homeActivity, "Connect to MQTT Broker fail! Reconnecting in 3 seconds...");
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            connectToMqttBroker(brokerServer, username, password);
                        }, 3000);
                    }
        });
    }

    private void subscribeTopic(){
        List<String> topicList = new LinkedList<>();
        topicList.add("V1");
        topicList.add("V3");
        for (String topic : topicList){
            MyMqttClient.gI().subscribe(topic, new MyMqttClient.HandleSubscribingResult() {
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

    private void updateSensorDataUI(JSONObject jsonObject){
        try {
            String type = (String) jsonObject.get("type");
            String value = String.valueOf(jsonObject.get("value"));
            if (type.equals(SensorType.TEMP)) {
                homeActivity.updateTempText(value);
            } else if (type.equals(SensorType.MOIS)){
                homeActivity.updateMoisText(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleScheduleResponseMessage(JSONObject jsonObject){
        try {
            ScheduleInfo scheduleInfo = new ScheduleInfo(jsonObject);
            Log.e(TAG, scheduleInfo.toJsonString());
            scheduleInfoList.add(scheduleInfo);
            homeActivity.updateScheduleList(scheduleInfoList);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void startToSettingPage(){
        Navigation.startNewActivtyForResult(homeActivity, SettingActivity.class, activityResultLauncher);
    }

    public void navigateToSignInPage(){
        Navigation.startNewActivity(homeActivity, SignInActivity.class, null);
        MyMqttClient.gI().unregisterObserver(this);
        homeActivity.finish();
    }

    public void startToHistoryPage(){
        Navigation.startNewActivity(homeActivity, HistoryActivity.class, null);
    }

    public void startToAddSchedulePage(){
        Bundle extras = new Bundle();
        extras.putString("page", "ADD");
        Navigation.startNewActivity(homeActivity, AddOrEditScheduleActivity.class, extras);
    }

    @Override
    public void onMessageReceived(String topic, String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            if (topic.equals(MqttTopic.sensorData)){
                mHandler.post(() -> {
                    updateSensorDataUI(jsonObject);
                });
            } else if (topic.equals(MqttTopic.scheduleResponse)){
                Log.d(TAG, "SCHEDULE");
                mHandler.post(() -> {
                    handleScheduleResponseMessage(jsonObject);
                });
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
