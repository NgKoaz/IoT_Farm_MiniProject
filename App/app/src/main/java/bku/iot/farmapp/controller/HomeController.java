package bku.iot.farmapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.CompoundButton;

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
import java.util.concurrent.atomic.AtomicBoolean;

import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.SensorType;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyFirestore;
import bku.iot.farmapp.services.global.MyMqttClient;
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
    private final List<Schedule> scheduleList = new ArrayList<>();
    private boolean debounce = false;

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

                        Schedule schedule = new Schedule();
                        schedule.assignAttribute("scheduleId", doc.getId());

                        // Iterate over the entries to print keys and values
                        for (Map.Entry<String, Object> entry : docData.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue().toString();
                            schedule.assignAttribute(key, value);
                        }
                        scheduleList.add(schedule);
                    }
                    sortScheduleList();
                    homeActivity.updateScheduleList(scheduleList);
                }
        );
    }

    public void refreshRecyclerView(){
        homeActivity.updateScheduleList(scheduleList);
    }

    private void sortScheduleList(){
        scheduleList.sort((o1, o2) -> {
            String[] o1_parts = o1._time.split(":");
            String[] o2_parts = o2._time.split(":");

            short o1_hour = Short.parseShort(o1_parts[0]);
            short o1_minute = Short.parseShort(o1_parts[1]);

            short o2_hour = Short.parseShort(o2_parts[0]);
            short o2_minute = Short.parseShort(o2_parts[1]);

            if (o1_hour != o2_hour) {
                return Short.compare(o1_hour, o2_hour);
            } else {
                return Short.compare(o1_minute, o2_minute);
            }
        });
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
                        homeActivity.showToast("Connect to MQTT Broker fail! Reconnecting in 3 seconds...");
                        Thread thread = new Thread(() -> {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            connectToMqttBroker(brokerServer, username, password);
                        });
                        thread.start();
                    }
        });
    }

    private void subscribeTopic(){
        List<String> topicList = new LinkedList<>();
        topicList.add("V1");
        topicList.add("V3");
        topicList.add("V4");
        MyMqttClient.gI().subscribeTopics(topicList);
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

    private void addScheduleInList(Schedule schedule) {
        scheduleList.add(schedule);
        sortScheduleList();
    }

    private void updateScheduleInList(Schedule newSchedule){
        for (Schedule oldSchedule : scheduleList) {
            if (oldSchedule.scheduleId.equals(newSchedule.scheduleId)) {
                if (oldSchedule.isOn != newSchedule.isOn) {
                    debounce = true;
                }
                oldSchedule.shallowCopy(newSchedule);
                break;
            }
        }
        sortScheduleList();
    }

    private void deleteScheduleInList(Schedule deleteSchedule){
        scheduleList.removeIf(schedule -> schedule.scheduleId.equals(deleteSchedule.scheduleId));
        sortScheduleList();
    }

    private void handleScheduleResponseMessage(JSONObject jsonObject){
        try {
            Schedule schedule = new Schedule(jsonObject);

            if (!schedule.error.isEmpty()) return;

            switch (schedule.type) {
                case "add":
                    addScheduleInList(schedule);
                    mHandler.post(() -> homeActivity.updateScheduleList(scheduleList));
                    break;
                case "update":
                    updateScheduleInList(schedule);
                    mHandler.post(() -> homeActivity.updateScheduleList(scheduleList));
                    break;
                case "delete":
                    deleteScheduleInList(schedule);
                    mHandler.post(() -> homeActivity.updateScheduleList(scheduleList));
                    break;
                default:
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void handleSwitch(CompoundButton buttonView, boolean isCheck, Schedule schedule, int position) {
        if (debounce) {
            debounce = false;
            return;
        }
        homeActivity.showLoading();
        schedule.type = "update";
        schedule.setIsOn(isCheck ? 1 : 0);
        publishAndWaitAck(schedule, (isAck, error) -> {
            if (!isAck) {
                mHandler.post(() -> {
                    homeActivity.showToast("Time out!");
                    schedule.setIsOn(!isCheck ? 1 : 0);
                    debounce = true;
                    buttonView.setChecked(!isCheck);
                    homeActivity.dismissLoading();
                });
            } else if (!error.isEmpty()) {
                mHandler.post(() -> {
                    homeActivity.showToast(error);
                    schedule.setIsOn(!isCheck ? 1 : 0);
                    debounce = true;
                    buttonView.setChecked(!isCheck);
                    homeActivity.dismissLoading();
                });
            } else {
                mHandler.post(homeActivity::dismissLoading);
            }
        });
    }

    private void publishAndWaitAck(Schedule schedule, HomeController.OnWaitAck listener){
        try {
            AtomicBoolean isAck = new AtomicBoolean(false);
            MyMqttClient.MessageObserver messageObserver = new MyMqttClient.MessageObserver() {
                @Override
                public void onMessageReceived(String topic, String payload) {
                    if (topic.equals(MqttTopic.scheduleResponse)) {
                        try {
                            JSONObject jsonObject = new JSONObject(payload);
                            String payloadEmail = jsonObject.getString("email");
                            String payloadType = jsonObject.getString("type");

                            if (schedule.email.equals(payloadEmail) && schedule.type.equals(payloadType)) {
                                isAck.set(true);
                                MyMqttClient.gI().unregisterObserver(this);
                                listener.onComplete(isAck.get(), jsonObject.getString("error"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyMqttClient.gI().unregisterObserver(this);
                        }
                    }
                }
            };
            MyMqttClient.gI().registerObserver(messageObserver);
            Thread thread = new Thread(() -> {
                Log.d(TAG, "Start sleep!");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "End sleep!");

                if (!isAck.get()) {
                    MyMqttClient.gI().unregisterObserver(messageObserver);
                    listener.onComplete(isAck.get(), "");
                }
            });
            MyMqttClient.gI().publish(MqttTopic.scheduleRequest, schedule.toJsonString());
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
            homeActivity.showToast("Non-expected error! Let try again!");
        }
    }

    public void startToSettingPage(){
        homeActivity.startNewActivtyForResult(SettingActivity.class, activityResultLauncher);
    }

    public void navigateToSignInPage(){
        homeActivity.startNewActivity(SignInActivity.class, null);
        MyMqttClient.gI().unregisterObserver(this);
        homeActivity.finish();
    }

    public void startToHistoryPage(){
        homeActivity.startNewActivity(HistoryActivity.class, null);
    }

    public void startToAddSchedulePage(){
        Bundle extras = new Bundle();
        extras.putString("page", "ADD");
        homeActivity.startNewActivity(ScheduleActivity.class, extras);
    }

    public void updateTimeDisplay(JSONObject jsonObject){
        try {
            String tempHour = String.valueOf(jsonObject.get("hour"));
            String hour = (tempHour.length() <= 1) ? "0" + tempHour : tempHour;

            String tempMinute = String.valueOf(jsonObject.get("minute"));
            String minute = (tempMinute.length() <= 1) ? "0" + tempMinute : tempMinute;

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

            switch (topic) {
                case MqttTopic.sensorData:
                    updateSensorDataUI(jsonObject);
                    break;
                case MqttTopic.scheduleResponse:
                    handleScheduleResponseMessage(jsonObject);
                    break;
                case MqttTopic.currentTime:
                    updateTimeDisplay(jsonObject);
                    break;
                default:
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private interface OnWaitAck{
        void onComplete(boolean isAck, String error);
    }
}
