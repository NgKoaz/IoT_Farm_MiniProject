package bku.iot.farmapp.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.view.common.Utils;
import bku.iot.farmapp.view.pages.ScheduleActivity;

public class ScheduleController implements MyMqttClient.MessageObserver {
    private final String TAG = ScheduleController.class.getSimpleName();
    private final int WAIT_ACK_DURATION = 5000; // 3000ms
    private int waterRatio = 0, mixer1Ratio = 0, mixer2Ratio = 0, mixer3Ratio = 0;
    private int area1Ratio = 0, area2Ratio = 0, area3Ratio = 0;
    private int isDate;
    private String date;
    private List<Integer> weekday = new ArrayList<>();
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

    public void setDateTimeForEditting(Schedule schedule){
        // Set default time
        this.time = schedule._time;
        String[] timeParts = this.time.split(":");
        String hour = timeParts[0];
        String minute = timeParts[1];

        scheduleActivity.updateTimeDisplay(hour, minute);

        // Set default date
        if (!schedule.date.isEmpty()) {
            this.date = schedule.date;
            scheduleActivity.clearWeekdayCheck();
            scheduleActivity.updateDateDisplay(this.date);
        } else {
            this.weekday = schedule.weekday;
            this.date = "";
            scheduleActivity.updateDateDisplay("Each");
            scheduleActivity.updateCheckBox(this.weekday);
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
    public void setWeekday(boolean isChecked, int numWeekday){
        if (isChecked) {
            this.weekday.add(numWeekday);
            this.weekday.sort((o1, o2) -> {
                int o11 = o1;
                int o22 = o2;
                return o11 - o22;
            });
        }
        date = "";
        scheduleActivity.updateDateDisplay("Each");
    }

    private String getUserEmail(){
        if (MyFirebaseAuth.gI().getCurrentUser() == null) {
            return "";
        }
        return MyFirebaseAuth.gI().getCurrentUser().getEmail();
    }

    public void setWaterAndMixerRatio(String water, String mixer1, String mixer2, String mixer3) {
        if (water.isEmpty() || mixer1.isEmpty() || mixer2.isEmpty() || mixer3.isEmpty()) {
            scheduleActivity.showToast("Please fill in all input field! Set 0 if you don't want to use that params.");
            return;
        }
        try {
            waterRatio = Integer.parseInt(water);
            mixer1Ratio = Integer.parseInt(mixer1);
            mixer2Ratio = Integer.parseInt(mixer2);
            mixer3Ratio = Integer.parseInt(mixer3);
        } catch (NumberFormatException e) {
            scheduleActivity.showToast("No expected error! Non-integer type.");
            waterRatio = 0;
            mixer1Ratio = 0;
            mixer2Ratio = 0;
            mixer3Ratio = 0;
            return;
        }
        scheduleActivity.updateMixerRatioText(water, mixer1, mixer2, mixer3);
        scheduleActivity.dismissMixerInputDialog();
    }

    public void setAreaRatio(String area1, String area2, String area3) {
        if (area1.isEmpty() || area2.isEmpty() || area3.isEmpty()) {
            scheduleActivity.showToast("Please fill in all input field! Set 0 if you don't want to use that params.");
            return;
        }
        try {
            area1Ratio = Integer.parseInt(area1);
            area2Ratio = Integer.parseInt(area2);
            area3Ratio = Integer.parseInt(area3);
        } catch (NumberFormatException e) {
            scheduleActivity.showToast("No expected error! Non-integer type.");
            area1Ratio = 0;
            area2Ratio = 0;
            area3Ratio = 0;
            return;
        }
        scheduleActivity.updateAreaRatioText(area1, area2, area3);
        scheduleActivity.dismissAreaInputDialog();
    }

    private boolean checkSchedule() {
        if (!hasCurrentTime) {
            mHandler.post(() -> scheduleActivity.showToast("Wait gateway send current time at Gateway's location!"));
            return false;
        }
        if (!this.date.isEmpty() && !isValidDateTime()) {
            mHandler.post(() -> scheduleActivity.showToast("The time you set is in the past!"));
            return false;
        }
        String userEmail = getUserEmail();
        if (userEmail.equals("")) {
            mHandler.post(() -> scheduleActivity.showToast("Not found your email, please re-sign in"));
            return false;
        }
        return true;
    }

    public void addSchedule(String name, String volume) {
        // Check before send.
        if (name.isEmpty()) {
            mHandler.post(() -> scheduleActivity.showToast("Put a schedule name!"));
            return;
        }
        if (!Utils.isInteger(volume)) {
            mHandler.post(() -> scheduleActivity.showToast("Volume is not integer!"));
            return;
        }
        if (!checkSchedule()) return;

        // Prepare before send
        mHandler.post(scheduleActivity::showLoading);

        List<Integer> ratio = new ArrayList<>();
        ratio.add(waterRatio);
        ratio.add(mixer1Ratio);
        ratio.add(mixer2Ratio);
        ratio.add(mixer3Ratio);
        ratio.add(area1Ratio);
        ratio.add(area2Ratio);
        ratio.add(area3Ratio);

        Schedule schedule = new Schedule(getUserEmail(), "add", name, Integer.parseInt(volume), ratio, this.date, this.weekday, this.time);
        schedule.isOn = 1;

        publishAndWaitAck(schedule, (isAck, error) -> {
            if (!isAck) {
                mHandler.post(() -> {
                    scheduleActivity.showToast("Time out message!");
                });
                mHandler.post(scheduleActivity::dismissLoading);
            } else if (!error.isEmpty()) {
                mHandler.post(() -> {
                    scheduleActivity.showToast(error);
                    scheduleActivity.dismissLoading();
                });
            } else {
                mHandler.post(() -> {
                    scheduleActivity.showToast("Successful!");
                    scheduleActivity.dismissLoading();
                    backToPreviousActivity();
                });
            }
        });
    }

    public void updateSchedule(Schedule schedule, String name, String volume){
        // Check before send.
        if (name.isEmpty()) {
            mHandler.post(() -> scheduleActivity.showToast("Put a schedule name!"));
            return;
        }
        if (!Utils.isInteger(volume)) {
            mHandler.post(() -> scheduleActivity.showToast("Volume is not integer!"));
            return;
        }
        if (!checkSchedule()) return;

        // Prepare before send
        mHandler.post(scheduleActivity::showLoading);
        schedule.email = getUserEmail();
        schedule.type = "update";
        schedule._name = name;
        schedule.volume = Integer.parseInt(volume);
        List<Integer> ratio = new ArrayList<>();
        ratio.add(waterRatio);
        ratio.add(mixer1Ratio);
        ratio.add(mixer2Ratio);
        ratio.add(mixer3Ratio);
        ratio.add(area1Ratio);
        ratio.add(area2Ratio);
        ratio.add(area3Ratio);
        schedule.ratio = ratio;
        schedule.date = date;
        schedule.weekday = weekday;
        schedule._time = time;
        schedule.isOn = 1;

        publishAndWaitAck(schedule, (isAck, error) -> {
            if (!isAck) {
                mHandler.post(() -> {
                    scheduleActivity.showToast("Time out message!");
                });
                mHandler.post(scheduleActivity::dismissLoading);
            } else if (!error.isEmpty()) {
                mHandler.post(() -> {
                    scheduleActivity.showToast(error);
                    scheduleActivity.dismissLoading();
                });
            } else {
                mHandler.post(() -> {
                    scheduleActivity.showToast("Successfully!");
                    scheduleActivity.dismissLoading();
                    backToPreviousActivity();
                });
            }
        });
    }

    public void deleteSchedule(Schedule schedule){
        mHandler.post(scheduleActivity::showLoading);

        if (schedule != null){
            schedule.type = "delete";
            publishAndWaitAck(schedule, (isAck, error) -> {
                if (!isAck) {
                    mHandler.post(() -> {
                        scheduleActivity.showToast("Time out message!");
                        scheduleActivity.dismissLoading();
                    });
                } else if (!error.isEmpty()) {
                    mHandler.post(() -> {
                        scheduleActivity.showToast(error);
                        scheduleActivity.dismissLoading();
                    });
                } else {
                    mHandler.post(() -> {
                        scheduleActivity.showToast("Delete successfully!");
                        scheduleActivity.dismissLoading();
                        backToPreviousActivity();
                    });
                }
            });
        } else {
            mHandler.post(() -> scheduleActivity.showToast("Non-expected error occur: `schedule` is null"));
        }
    }

    private void publishAndWaitAck(Schedule schedule, OnWaitAck listener){
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
                    Thread.sleep(WAIT_ACK_DURATION);
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
            scheduleActivity.showToast("Non-expected error! Let try again!");
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
        void onComplete(boolean isAck, String error);
    }
}
