package bku.iot.farmapp.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.view.common.Utils;
import bku.iot.farmapp.view.pages.ScheduleActivity;

public class ScheduleController {
    private final String TAG = ScheduleController.class.getSimpleName();
    private final int WAIT_ACK_DURATION = 3000; // 3000ms
    private int waterRatio = 0, mixer1Ratio = 0, mixer2Ratio = 0, mixer3Ratio = 0;
    private int area1Ratio = 0, area2Ratio = 0, area3Ratio = 0;
    private int hour, minute;
    private int isDate;
    private int day, month, year;
    private List<Integer> weekday = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ScheduleActivity scheduleActivity;
    private boolean isDateTimeSetDefault = false;

    public ScheduleController(ScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;
    }

    public void openTimePickerDialog(){
        scheduleActivity.openTimePickerDialog(hour, minute);
    }

    private boolean isValidDateTime(int year, int month, int day, int hour, int minute) {
        if (isDate == 0) return true;

        Calendar currentTime = Calendar.getInstance();
        int curHour, curMinute, curDay, curMonth, curYear;

        curHour = currentTime.get(Calendar.HOUR_OF_DAY);
        curMinute = currentTime.get(Calendar.MINUTE);

        curDay = currentTime.get(Calendar.DAY_OF_MONTH);
        curMonth = currentTime.get(Calendar.MONTH) + 1;
        curYear = currentTime.get(Calendar.YEAR);


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

        return minute > curMinute;
    }

    public void setDefaultDateTime() {
        isDateTimeSetDefault = true;

        // Set date is default instead of weekdays.
        isDate = 1;

        hour = 6;
        minute = 0;

        setDefaultDateByTime(hour, minute);
        // Clear weekday value, because we set day.
        weekday.clear();
        // Update UI
        scheduleActivity.updateTimeDisplay(hour, minute);
    }

    public void setDateTimeForEditing(Schedule schedule){
        // Set time from old schedule
        String[] timeParts = schedule._time.split(":");
        hour = Integer.parseInt(timeParts[0]);
        minute = Integer.parseInt(timeParts[1]);

        scheduleActivity.updateTimeDisplay(hour, minute);

        // Set default date
        if (!schedule.date.isEmpty()) {
            String[] dateParts = schedule.date.split("/");
            isDate = 1;
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            year = Integer.parseInt(dateParts[2]);

            // Update UI
            weekday.clear();
            scheduleActivity.clearWeekdayCheck();
            scheduleActivity.updateDateDisplay(year, month, day);
        } else {
            isDate = 0;
            weekday = schedule.weekday;
            scheduleActivity.updateWeekdayDisplay("Each");
            scheduleActivity.updateCheckBox(this.weekday);
        }

        isDateTimeSetDefault = !isValidDateTime(year, month, day, hour, minute);
    }

    private void setDefaultDateByTime(int hour, int minute) {
        isDateTimeSetDefault = true;
        boolean isMoveOneDay = false;
        Calendar currentTime = Calendar.getInstance();

        int curHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int curMinute = currentTime.get(Calendar.MINUTE);

        if (hour < curHour || (hour == curHour && minute <= curMinute)) {
            isMoveOneDay = true;
        }

        if (isMoveOneDay) {
            currentTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        isDate = 1;
        year = currentTime.get(Calendar.YEAR);
        month = currentTime.get(Calendar.MONTH) + 1;
        day = currentTime.get(Calendar.DAY_OF_MONTH);

        // Clear weekday
        weekday.clear();

        // Update UI
        scheduleActivity.clearWeekdayCheck();
        scheduleActivity.updateDateDisplay(year, month, day);
    }

    public void setTime(int hour, int minute){
        if (!isDateTimeSetDefault && !isValidDateTime(year, month, day, hour, minute)) {
            mHandler.post(() -> scheduleActivity.showToast("The time set is in the past!"));
            return;
        }

        this.hour = hour;
        this.minute = minute;
        scheduleActivity.updateTimeDisplay(hour, minute);
        if (isDateTimeSetDefault) {
            setDefaultDateByTime(hour, minute);
        }
    }

    public void setDate(int year, int month, int day){
        if (!isValidDateTime(year, month, day, hour, minute)) {
            mHandler.post(() -> scheduleActivity.showToast("The time set is in the past!"));
            return;
        }

        isDateTimeSetDefault = false;

        isDate = 1;
        this.year = year;
        this.month = month;
        this.day = day;

        // Clear weekday
        weekday.clear();

        // Update UI
        scheduleActivity.clearWeekdayCheck();
        scheduleActivity.updateDateDisplay(year, month, day);
    }

    public void setWeekday(boolean isChecked, int numWeekday){
        isDateTimeSetDefault = false;

        isDate = 0;
        if (isChecked) {
            if (!weekday.contains(numWeekday)) {
                weekday.add(numWeekday);
            }
            weekday.sort((o1, o2) -> {
                int o11 = o1;
                int o22 = o2;
                return o11 - o22;
            });
        } else {
            if (weekday.contains(numWeekday)) {
                weekday.remove(Integer.valueOf(numWeekday));
            }
        }
        scheduleActivity.updateWeekdayDisplay("Each");
    }

    private String getUserEmail(){
        if (MyFirebaseAuth.gI().getCurrentUser() == null) {
            return "";
        }
        return MyFirebaseAuth.gI().getCurrentUser().getEmail();
    }

    private String getTimeIsSetInStringType() {
        return String.format(hour < 10 ? "0%s" : "%s", hour) + ":" +
                String.format(minute < 10 ? "0%s" : "%s", minute);
    }

    private String getDateIsSetInStringType() {
        return String.format(String.format(day < 10 ? "0%s" : "%s", day)) + "/" +
                String.format(month < 10 ? "0%s" : "%s", month) + "/" +
                year;
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
        if (!isValidDateTime(year, month, day, hour, minute)) {
            mHandler.post(() -> scheduleActivity.showToast("The time you set is in the past!"));
            return false;
        }
        String userEmail = getUserEmail();
        if (userEmail.equals("")) {
            mHandler.post(() -> scheduleActivity.showToast("Not found your email, please re-sign in"));
            return false;
        }
        if (isDate == 0 && weekday.isEmpty()) {
            mHandler.post(() -> scheduleActivity.showToast("Please set date or weekday!"));
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

        Schedule schedule = new Schedule(
                getUserEmail(),
                "add",
                name,
                Integer.parseInt(volume),
                ratio,
                getDateIsSetInStringType(),
                this.weekday,
                getTimeIsSetInStringType()
        );
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
        schedule.date = getDateIsSetInStringType();
        schedule.weekday = weekday;
        schedule._time = getTimeIsSetInStringType();
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
                            String scheduleId = jsonObject.getString("scheduleId");
                            String payloadEmail = jsonObject.getString("email");
                            String payloadType = jsonObject.getString("type");

                            if (schedule.email.equals(payloadEmail) && schedule.type.equals(payloadType)) {
                                if (!schedule.type.equals("update") || schedule.scheduleId.equals(scheduleId)) {
                                    isAck.set(true);
                                    MyMqttClient.gI().unregisterObserver(this);
                                    listener.onComplete(isAck.get(), jsonObject.getString("error"));
                                }
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
        scheduleActivity.openDatePickerDialog(year, month, day);
    }

    public void backToPreviousActivity(){
        scheduleActivity.finish();
    }

    public void loadEditSchedulePage(Schedule schedule) {
        waterRatio = schedule.ratio.get(0);
        mixer1Ratio = schedule.ratio.get(1);
        mixer2Ratio = schedule.ratio.get(2);
        mixer3Ratio = schedule.ratio.get(3);
        area1Ratio = schedule.ratio.get(4);
        area2Ratio = schedule.ratio.get(5);
        area3Ratio = schedule.ratio.get(6);

        // Update UI
        scheduleActivity.updateNameText(schedule._name);
        scheduleActivity.updateVolumeText(String.valueOf(schedule.volume));
        scheduleActivity.updateMixerRatioText(
                String.valueOf(waterRatio),
                String.valueOf(mixer1Ratio),
                String.valueOf(mixer2Ratio),
                String.valueOf(mixer3Ratio)
        );
        scheduleActivity.updateAreaRatioText(
                String.valueOf(area1Ratio),
                String.valueOf(area2Ratio),
                String.valueOf(area3Ratio)
        );
    }

    public void startThreadUpdateCurrentTime() {
        Thread thread = new Thread(() -> {
            while (true) {
                updateCurrentTimeDisplay();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    private void updateCurrentTimeDisplay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault());
        String currentTime = dateFormat.format(new Date());
        mHandler.post(() -> scheduleActivity.updateCurrentTime(currentTime));
    }

    private interface OnWaitAck{
        void onComplete(boolean isAck, String error);
    }
}
