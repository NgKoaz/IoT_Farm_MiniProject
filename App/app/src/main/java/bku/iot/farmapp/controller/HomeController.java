package bku.iot.farmapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;
import bku.iot.farmapp.view.pages.HistoryActivity;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SettingActivity;
import bku.iot.farmapp.view.pages.SignInActivity;

public class HomeController {

    private final String TAG = HomeController.class.getSimpleName();
    private final HomeActivity homeActivity;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public HomeController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;

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
        MyMqttClient.gI().setOnMessageArrived(this::onMessageArrived);
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

    private void onMessageArrived(String topic, String payload){


    }

    public void startToSettingPage(){
        Navigation.startNewActivtyForResult(homeActivity, SettingActivity.class, activityResultLauncher);
    }

    public void navigateToSignInPage(){
        Navigation.startNewActivity(homeActivity, SignInActivity.class, null);
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
}
