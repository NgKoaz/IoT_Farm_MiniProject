package bku.iot.farmapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.SensorType;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyFirestore;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.ScheduleActivity;
import bku.iot.farmapp.view.pages.HistoryActivity;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SettingActivity;
import bku.iot.farmapp.view.pages.SignInActivity;


public class HomeController implements MyMqttClient.MessageObserver {

    private final String TAG = HomeController.class.getSimpleName();
    private final HomeActivity homeActivity;
    private final ActivityResultLauncher<Intent> activityResultLauncher;
    private final Handler mHandler;
    private List<ScheduleInfo> scheduleInfoList = new ArrayList<>();


    public HomeController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        mHandler = new Handler(Looper.getMainLooper());

        // Get user and display
        mHandler.post(this::updateHelloUserText);
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

        refreshScheduleList();
    }

    private void updateHelloUserText(){
        FirebaseUser user = MyFirebaseAuth.gI().getCurrentUser();
        if (user == null) return;
        homeActivity.updateHelloUserText(user.getEmail());
    }

    private void refreshScheduleList(){
        String[] brokerParts = MyMqttClient.gI().broker.split("/");
        String broker = brokerParts[brokerParts.length - 1];
        String username = MyMqttClient.gI().username;
        String password = MyMqttClient.gI().password;

        MyFirestore.gI().getScheduleList(
                MyFirebaseAuth.gI().getCurrentUser(),
                broker,
                username,
                password,
                task -> {
                    if (!task.isSuccessful()) return;
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot == null || querySnapshot.isEmpty()){
                        Log.d(TAG, "No schedule has been set!");
                        return;
                    }
                    for (DocumentSnapshot doc : querySnapshot){
                        // Get all keys and values
                        Map<String, Object> docData = doc.getData();
                        if (docData == null) continue;

                        ScheduleInfo scheduleInfo = new ScheduleInfo();
                        scheduleInfo.assignAttribute("scheduleId", doc.getId());

                        // Iterate over the entries to print keys and values
                        for (Map.Entry<String, Object> entry : docData.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue().toString();
                            scheduleInfo.assignAttribute(key, value);
                        }

                        scheduleInfoList.add(scheduleInfo);
                    }
                    homeActivity.updateScheduleList(scheduleInfoList);
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
        topicList.add("V4");
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
                mHandler.post(() -> homeActivity.updateTempText(value));
            } else if (type.equals(SensorType.MOIS)){
                mHandler.post(() -> homeActivity.updateMoisText(value));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateScheduleList(ScheduleInfo newSchedule){
        for (ScheduleInfo oldSchedule : scheduleInfoList) {
            if (oldSchedule.scheduleId.equals(newSchedule.scheduleId)) {
                scheduleInfoList.remove(oldSchedule);
                scheduleInfoList.add(newSchedule);
            }
        }
    }

    private void deleteScheduleList(ScheduleInfo newSchedule){
        scheduleInfoList.removeIf(oldSchedule -> oldSchedule.scheduleId.equals(newSchedule.scheduleId));
    }

    private void handleScheduleResponseMessage(JSONObject jsonObject){
        try {
            ScheduleInfo scheduleInfo = new ScheduleInfo(jsonObject);
            if (scheduleInfo.isError == 1) return;

            if (scheduleInfo.type.equals("add")){
                scheduleInfoList.add(scheduleInfo);
                mHandler.post(() -> homeActivity.updateScheduleList(scheduleInfoList));
            } else if (scheduleInfo.type.equals("update")){
                updateScheduleList(scheduleInfo);
                mHandler.post(() -> homeActivity.updateScheduleList(scheduleInfoList));
            } else if (scheduleInfo.type.equals("delete")){
                deleteScheduleList(scheduleInfo);
                mHandler.post(() -> homeActivity.updateScheduleList(scheduleInfoList));
            }
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
        Navigation.startNewActivity(homeActivity, ScheduleActivity.class, extras);
    }

    public void updateTimeDisplay(JSONObject jsonObject){
        try {
            String hour = String.valueOf(jsonObject.get("hour"));
            String minute = String.valueOf(jsonObject.get("minute"));
            String day = String.valueOf(jsonObject.get("day"));
            String month = String.valueOf(jsonObject.get("month"));
            String year = String.valueOf(jsonObject.get("year"));
            mHandler.post(() -> homeActivity.updateTimeText(String.format("%s:%s %s/%s/%s", hour, minute, day, month, year)));
        } catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG, "updateTimeDisplay() is not in correct form");
        }
    }

    @Override
    public void onMessageReceived(String topic, String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            if (topic.equals(MqttTopic.sensorData)){
                executor.submit(() -> {
                    updateSensorDataUI(jsonObject);
                });
            } else if (topic.equals(MqttTopic.scheduleResponse)){
                executor.submit(() -> {
                    handleScheduleResponseMessage(jsonObject);
                });
            } else if (topic.equals(MqttTopic.currentTime)){
                executor.submit(() -> {
                    updateTimeDisplay(jsonObject);
                });
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
