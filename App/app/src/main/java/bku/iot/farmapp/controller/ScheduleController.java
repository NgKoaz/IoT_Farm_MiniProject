package bku.iot.farmapp.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;

public class ScheduleController implements MyMqttClient.MessageObserver {

    private final String TAG = ScheduleController.class.getSimpleName();
    private int isDate;
    private String date;
    private String weekday;
    private String time;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    private final AddOrEditScheduleActivity scheduleActivity;

    public ScheduleController(AddOrEditScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;
    }

    public void openTimePickerDialog(){
        scheduleActivity.openTimePickerDialog();
    }

//    private boolean isValidDateTime(int minute, int hour, int day, int month, int year) {
//        Calendar currentTime = Calendar.getInstance();
//
//        int curYear = currentTime.get(Calendar.YEAR);
//        int curMonth = currentTime.get(Calendar.MONTH) + 1;
//        int curDay = currentTime.get(Calendar.DAY_OF_MONTH);
//        int curHour = currentTime.get(Calendar.HOUR);
//        int curMinute = currentTime.get(Calendar.MINUTE);
//
//        Log.d(TAG, String.format("%d:%d %d/%d/%d", hour, minute, day, month, year));
//        Log.d(TAG, String.format("%d:%d %d/%d/%d", curHour, curMinute, curDay, curMonth, curYear));
//
//        // Compare year
//        if (year < curYear)
//            return false;
//        else if (year > curYear)
//            return true;
//
//        // If the years are the same, compare month
//        if (month < curMonth)
//            return false;
//        else if (month > curMonth)
//            return true;
//
//        // If the months are the same, compare day
//        if (day < curDay)
//            return false;
//        else if (day > curDay)
//            return true;
//
//        if (hour < curHour)
//            return false;
//        else if (hour > curHour)
//            return true;
//
//        if (minute <= curMinute)
//            return false;
//        return true;
//    }

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
        this.time = String.format("%d:%d", hour, minute);
        scheduleActivity.updateTimeDisplay(String.format("%d", hour), String.format("%d", minute));

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
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        scheduleActivity.updateTimeDisplay(String.format("%d", hour), String.format("%d", minute));

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
//        String[] dateParts = this.date.split("/");
//        int day = Integer.parseInt(dateParts[0]);
//        int month = Integer.parseInt(dateParts[1]);
//        int year = Integer.parseInt(dateParts[2]);
//
//        if (!isValidDateTime(minute, hour, day, month, year)){
//            ToastManager.showToast(scheduleActivity, "Cannot select time in the past");
//            return;
//        }

        this.time = String.format("%d:%d", hour, minute);
        scheduleActivity.updateTimeDisplay(String.format("%d", hour), String.format("%d", minute));
    }

    public void setDate(int day, int month, int year){
//        String[] timeParts = this.time.split(":");
//        int hour = Integer.parseInt(timeParts[0]);
//        int minute = Integer.parseInt(timeParts[1]);
//
//        if (!isValidDateTime(minute, hour, day, month, year)){
//            ToastManager.showToast(scheduleActivity, "Cannot select date in the past");
//            return;
//        }

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

    private void publishScheduleRequest(ScheduleInfo scheduleInfo){
        try {
            MyMqttClient.gI().publish(MqttTopic.scheduleRequest, scheduleInfo.toJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            ToastManager.showToast(scheduleActivity, "Non-expected error! Let try again!");
        }
    }

    public void saveSchedule(
            boolean isNewAdding,
            String name,
            double water,
            double mixer1, double mixer2, double mixer3,
            double area1, double area2, double area3
    ) {
        mHandler.post(scheduleActivity::showLoading);

        Log.d(TAG, "Save schedule!!!!!!!");
        String userEmail = getUserEmail();
        if (userEmail.equals("")) {
            ToastManager.showToast(scheduleActivity, "Not found your email, please re-sign in");
            mHandler.post(scheduleActivity::dismissLoading);
            return;
        }

        String type;
        if (isNewAdding)
            type = "add";
        else
            type = "update";

        ScheduleInfo scheduleInfo = new ScheduleInfo(
                userEmail,
                type,
                name, water,
                mixer1, mixer2, mixer3,
                area1, area2, area3,
                this.isDate, this.date,
                this.weekday, this.time
        );

//        if (this.isDate == 1){
//            String[] timeParts = this.time.split(":");
//            int hour = Integer.parseInt(timeParts[0]);
//            int minute = Integer.parseInt(timeParts[1]);
//
//            String[] dateParts = this.date.split("/");
//            int day = Integer.parseInt(dateParts[0]);
//            int month = Integer.parseInt(dateParts[1]);
//            int year = Integer.parseInt(dateParts[2]);
//
//            if (!isValidDateTime(minute, hour, day, month, year)){
//                ToastManager.showToast(scheduleActivity, "Cannot select date in the past");
//                return;
//            }
//        }
        publishAndWaitAck(scheduleInfo);

        mHandler.post(scheduleActivity::dismissLoading);
    }

    private void publishAndWaitAck(ScheduleInfo scheduleInfo){
        try {
            AtomicBoolean isAck = new AtomicBoolean(false);
            MyMqttClient.MessageObserver messageObserver = (topic, payload) -> {
                if (topic.equals(MqttTopic.scheduleResponse)) {
                    try {
                        JSONObject jsonObject = new JSONObject(payload);
                        String payloadEmail = jsonObject.getString("email");
                        String payloadType = jsonObject.getString("type");
                        String payloadName = jsonObject.getString("name");
                        if (scheduleInfo.email.equals(payloadEmail) &&
                                scheduleInfo.type.equals(payloadType) &&
                                scheduleInfo.name.equals(payloadName))
                        {
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
            if (isAck.get()) {
                mHandler.post(this::backToPreviousActivity);
            } else {
                mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Request time out!"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.post(() -> ToastManager.showToast(scheduleActivity, "Non-expected error! Let try again!"));
        }
    }

    public void openDatePickerDialog(){
        scheduleActivity.openDatePickerDialog();
    }

    public void deleteSchedule(){
        Log.d(TAG, "CHOOSE THE DATE!!!!!!!!!!");
    }

    public void backToPreviousActivity(){
        scheduleActivity.finish();
    }

    @Override
    public void onMessageReceived(String topic, String payload) {
        if (topic.equals(MqttTopic.scheduleResponse)){

        }

    }
}
