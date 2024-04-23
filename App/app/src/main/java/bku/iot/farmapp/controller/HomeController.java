package bku.iot.farmapp.controller;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;
import java.util.List;

import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;
import bku.iot.farmapp.view.pages.HistoryActivity;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SettingActivity;
import bku.iot.farmapp.view.pages.recyclerView.MyAdapter;

public class HomeController {

    private final String TAG = HomeController.class.getSimpleName();
    private final HomeActivity homeActivity;

    public HomeController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        connectToMqttBroker(
                "tcp://mqtt.ohstem.vn",
                "FarmApp_IOT",
                ""
        );
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
        List<String> topicList = new ArrayList<>();
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

    public void startToSettingPage(){
        Navigation.startNewActivity(homeActivity, SettingActivity.class, null);
    }

    public void startToHistoryPage(){
        Navigation.startNewActivity(homeActivity, HistoryActivity.class, null);
    }

    public void startToAddSchedulePage(){
        Bundle extras = new Bundle();
        extras.putString("page", "ADD");
        Navigation.startNewActivity(homeActivity, AddOrEditScheduleActivity.class, extras);
    }
}
