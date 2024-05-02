package bku.iot.farmapp.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.view.common.ToastManager;
import bku.iot.farmapp.view.pages.ScheduleActivity;

public class ScheduleController implements MyMqttClient.MessageObserver {
    private final String TAG = ScheduleController.class.getSimpleName();
    private int isDate;
    private String date;
    private String weekday;
    private String time;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean hasCurrentTime = false;     // Got current time from gateway yet?
    private int curHour, curMinute, curDay, curMonth, curYear;


    private final ScheduleActivity scheduleActivity;

    public ScheduleController(ScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;

        MyMqttClient.gI().registerObserver(this);
    }

    public void openTimePickerDialog(){
        scheduleActivity.openTimePickerDialog();
    }

    private boolean isValidDateTime() {
        if (isDate == 0) return true;
        String[] timeParts = time.split(":");
        String[] dateParts = date.split("/");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        // Compare year
        if (year < curYear)
            return false;
        else if (year > curYear)
            return true;

        // If the years are the same, compare month
        if (month < curMonth)
            return false;
        else if (month > curMonth)
            return true;

        // If the months are the same, compare day
        if (day < curDay)
            return false;
        else if (day > curDay)
            return true;

        if (hour < curHour)
            return false;
        else if (hour > curHour)
            return true;

        if (minute <= curMinute)
            return false;
        return true;
    }

    public void setDefaultDateTime(){
        // Get current time
        Calendar currentTime = Calendar.getInstance();
        int curHour = currentTime.get(Calendar.HOUR_OF_DAY);

        // Default hour
        int hour = 6;
        int minute = 0;

        // If time is in the past, add one day.
        if (curHour >= hour) {
            currentTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Set default time
        String hourDisplay = String.format((hour < 10) ? "0%d" : "%d", hour);
        String minuteDisplay = String.format((minute < 10) ? "0%d" : "%d", minute);

        this.time = String.format("%s:%s", hourDisplay, minuteDisplay);
        scheduleActivity.updateTimeDisplay(hourDisplay, minuteDisplay);

        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int year = currentTime.get(Calendar.YEAR);

        // Set default date
        isDate = 1;
        this.date = String.format("%d/%d/%d", day, month, year);
        scheduleActivity.clearWeekdayCheck();
        scheduleActivity.updateDateDisplay(this.date);
    }

    public void setDateTimeForEditting(ScheduleInfo scheduleInfo){
        // Set default time
        this.time = scheduleInfo.time;
        String[] timeParts = this.time.split(":");
        String hour = timeParts[0];
        String minute = timeParts[1];

        scheduleActivity.updateTimeDisplay(hour, minute);

        // Set default date
        this.isDate = scheduleInfo.isDate;
        if (isDate == 1) {
            this.date = scheduleInfo.date;
            scheduleActivity.clearWeekdayCheck();
            scheduleActivity.updateDateDisplay(this.date);
        } else {
            this.weekday = scheduleInfo.weekday;
            setWeekday(scheduleInfo.weekday);
        }
    }

    public void setTime(int hour, int minute){
        // Set default time
        String hourDisplay = String.format((hour < 10) ? "0%d" : "%d", hour);
        String minuteDisplay = String.format((minute < 10) ? "0%d" : "%d", minute);

        this.time = String.format("%s:%s", hourDisplay, minuteDisplay);
        scheduleActivity.updateTimeDisplay(hourDisplay, minuteDisplay);
    }

    public void setDate(int day, int month, int year){
        isDate = 1;
        this.date = String.format("%d/%d/%d", day, month, year);
        scheduleActivity.clearWeekdayCheck();
        scheduleActivity.updateDateDisplay(this.date);
    }

    // weekday params has been taken from Weekdays class
    public void setWeekday(String weekday){
        isDate = 0;
        this.weekday = weekday;
        date = "";
        String text = "Monday";
        switch(weekday){
            case Weekdays.MONDAY:
                text = "Monday";
                break;
            case Weekdays.TUESDAY:
                text = "Tuesday";
                break;
            case Weekdays.WEDNESDAY:
                text = "Wednesday";
                break;
            case Weekdays.THURSDAY:
                text = "Thursday";
                break;
            case Weekdays.FRIDAY:
                text = "Friday";
                break;
            case Weekdays.SATURDAY:
                text = "Saturday";
                break;
            case Weekdays.SUNDAY:
                text = "Sunday";
                break;
            default:
        }
        scheduleActivity.updateDateDisplay(String.format("%s %s,", "Each", text));
    }

    private String getUserEmail(){
        if (MyFirebaseAuth.gI().getCurrentUser() == null) {
            return "";
        }
        return MyFirebaseAuth.gI().getCurrentUser().getEmail();
    }

    public void saveSchedule(
            String name,
            double water,
            double mixer1,
            double mixer2,
            double mixer3,
            double area1,
            double area2,
            double area3
    ) {
        mHandler.post(scheduleActivity::showLoading);
        Log.d(TAG, "Save schedule!!!!!!!");
        if (!hasCurrentTime) {
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Wait gateway send current time!"));
            mHandler.post(scheduleActivity::dismissLoading);
            return;
        }
        if (isValidDateTime()){
            String userEmail = getUserEmail();
            if (!userEmail.equals("")) {
                ScheduleInfo scheduleInfo = new ScheduleInfo(
                        userEmail, "add", name, water,
                        mixer1, mixer2, mixer3,
                        area1, area2, area3,
                        this.isDate, this.date,
                        this.weekday, this.time
                );
                // Send
                publishAndWaitAck(scheduleInfo, (isAck, isError, error) -> {
                    if (isAck) {
                        if (isError){
                            mHandler.post(() -> ToastManager.showToast(scheduleActivity, error));
                        } else {
                            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Add schedule successful!"));
                            mHandler.post(this::backToPreviousActivity);
                        }
                    } else {
                        mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Request time out!"));
                    }
                });
            } else {
                mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Not found your email, please re-sign in"));
            }
        } else {
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "The time you set is in the past!"));
        }
        mHandler.post(scheduleActivity::dismissLoading);
    }

    public void updateSchedule(
            ScheduleInfo scheduleInfo,
            String name,
            double water,
            double mixer1,
            double mixer2,
            double mixer3,
            double area1,
            double area2,
            double area3
    ){
        mHandler.post(scheduleActivity::showLoading);
        Log.d(TAG, "Update Schedule!!!!!!");

        if (!hasCurrentTime) {
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Wait gateway send current time!"));
            mHandler.post(scheduleActivity::dismissLoading);
            return;
        }

        if (isValidDateTime()){
            if (scheduleInfo != null) {
                // Set attributes
                scheduleInfo.type = "update";
                scheduleInfo.name = name;
                scheduleInfo.water = water;
                scheduleInfo.mixer1 = mixer1;
                scheduleInfo.mixer2 = mixer2;
                scheduleInfo.mixer3 = mixer3;
                scheduleInfo.area1 = area1;
                scheduleInfo.area2 = area2;
                scheduleInfo.area3 = area3;
                scheduleInfo.isDate = this.isDate;
                scheduleInfo.date = this.date;
                scheduleInfo.weekday = this.weekday;
                scheduleInfo.time = this.time;

                publishAndWaitAck(scheduleInfo, (isAck, isError, error) -> {
                    if (isAck) {
                        if (isError){
                            mHandler.post(() -> ToastManager.showToast(scheduleActivity, error));
                        } else {
                            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Update schedule successful!"));
                            mHandler.post(this::backToPreviousActivity);
                        }
                    } else {
                        mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Request time out!"));
                    }
                });
            } else {
                mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Non-expected error occur: `scheduleInfo` is null"));
            }
        } else {
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "The time you set is in the past!"));
        }
        mHandler.post(scheduleActivity::dismissLoading);
    }

    public void deleteSchedule(ScheduleInfo scheduleInfo){
        mHandler.post(scheduleActivity::showLoading);
        Log.d(TAG, "Delete Schedule!!!!!!");

        if (scheduleInfo != null){
            scheduleInfo.type = "delete";
            publishAndWaitAck(scheduleInfo, (isAck, isError, error) -> {
                if (isAck) {
                    if (isError){
                        mHandler.post(() -> ToastManager.showToast(scheduleActivity, error));
                    } else {
                        mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Delete schedule successful!"));
                        mHandler.post(this::backToPreviousActivity);
                    }
                } else {
                    mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Request time out!"));
                }
            });
        } else {
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Non-expected error occur: `scheduleInfo` is null"));
        }
        mHandler.post(scheduleActivity::dismissLoading);
    }

    private void publishAndWaitAck(ScheduleInfo scheduleInfo, OnWaitAck listener){
        try {
            AtomicBoolean isAck = new AtomicBoolean(false);
            AtomicBoolean isError = new AtomicBoolean(false);
            AtomicReference<String> error = new AtomicReference<>("");
            MyMqttClient.MessageObserver messageObserver = (topic, payload) -> {
                if (topic.equals(MqttTopic.scheduleResponse)) {
                    try {
                        JSONObject jsonObject = new JSONObject(payload);
                        String payloadEmail = jsonObject.getString("email");
                        String payloadType = jsonObject.getString("type");
                        String payloadScheduleId = jsonObject.getString("scheduleId");
                        if (scheduleInfo.email.equals(payloadEmail) && scheduleInfo.type.equals(payloadType) &&
                                (scheduleInfo.scheduleId.equals(payloadScheduleId) || scheduleInfo.type.equals("add"))
                        ){
                            if (scheduleInfo.isError == 1) {
                                isError.set(true);
                                error.set(scheduleInfo.error);
                            }
                            isAck.set(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            MyMqttClient.gI().registerObserver(messageObserver);
            MyMqttClient.gI().publish(MqttTopic.scheduleRequest, scheduleInfo.toJsonString());

            int i = 0;
            while (i < 30) {
                if (isAck.get()) break;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
            MyMqttClient.gI().unregisterObserver(messageObserver);
            listener.onComplete(isAck.get(), isError.get(), error.get());
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Non-expected error! Let try again!"));
        }
    }

    public void openDatePickerDialog(){
        scheduleActivity.openDatePickerDialog();
    }

    public void backToPreviousActivity(){
        MyMqttClient.gI().unregisterObserver(this);
        scheduleActivity.finish();
    }

    public void updateCurrentTimeDisplay(){
        mHandler.post(() -> {
            String curHourDisplay = String.format((curHour < 10) ? "0%d" : "%d", curHour);
            String curMinuteDisplay = String.format((curMinute < 10) ? "0%d" : "%d", curMinute);
            String curDayDisplay = String.format((curDay < 10) ? "0%d" : "%d", curDay);
            String curMonthDisplay = String.format((curMonth < 10) ? "0%d" : "%d", curMonth);
            String curYearDisplay = String.format((curYear < 10) ? "0%d" : "%d", curYear);

            scheduleActivity.updateCurrentTime(
                String.format("%s:%s %s/%s/%s",
                        curHourDisplay,
                        curMinuteDisplay,
                        curDayDisplay,
                        curMonthDisplay,
                        curYearDisplay)
            );
        });
    }

    private void handleCurrentTimeMessage(JSONObject jsonObject){
        try {
            curHour = jsonObject.getInt("hour");
            curMinute = jsonObject.getInt("minute");
            curDay = jsonObject.getInt("day");
            curMonth = jsonObject.getInt("month");
            curYear = jsonObject.getInt("year");
            hasCurrentTime = true;
            updateCurrentTimeDisplay();
        } catch (JSONException e) {
            Log.e(TAG, "handleCurrentTimeMessage get data error!");
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String topic, String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            if (topic.equals(MqttTopic.currentTime)){
                executor.submit(() -> {
                    handleCurrentTimeMessage(jsonObject);
                });
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private interface OnWaitAck{
        void onComplete(boolean isAck, boolean isError, String error);
    }
}