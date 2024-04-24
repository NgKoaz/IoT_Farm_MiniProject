package bku.iot.farmapp.controller;

import android.util.Log;

import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;

public class ScheduleController {

    private final String TAG = ScheduleController.class.getSimpleName();
    private byte isDate;


    private final AddOrEditScheduleActivity scheduleActivity;

    public ScheduleController(AddOrEditScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;
    }

    public void openTimePickerDialog(){
        scheduleActivity.openTimePickerDialog();

    }

    public void setTime(int hour, int minute){
        scheduleActivity.updateTimeDisplay(String.format("%d", hour), String.format("%d", minute));
    }

    public void setDate(int date, int month, int year){
        isDate = 1;
        scheduleActivity.clearWeekdayCheck();
        scheduleActivity.updateDateDisplay(String.format("Date: %d - %d - %d", date, month, year));
    }

    // weekday params has been taken from Weekdays class
    public void setWeekday(String weekday){
        isDate = 0;
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
            boolean isNewAdding,
            String name,
            double water,
            double mixer1, double mixer2, double mixer3,
            double area1, double area2, double area3,
            byte isDate, String date, String weekday,
            String time
    ) {
        String userEmail = getUserEmail();
        if (userEmail.equals("")) {
            ToastManager.showToast(scheduleActivity, "Not found your email, please re-sign in");
            return;
        }

        ScheduleInfo scheduleInfo = new ScheduleInfo(
                userEmail,
                "",
                name,
                water,
                mixer1, mixer2, mixer3,
                area1, area2, area3,
                isDate, date, weekday,
                time
        );
        if (isNewAdding){
            Log.d(TAG, "SAVE SCHEDULEEEEEEEEEEEEEEEEEE");
        } else {
            Log.d(TAG, "EDIT SCHEDULE!!!!!!!!!!!");
        }
    }

    public void openDatePickerDialog(){
        scheduleActivity.openDatePickerDialog();
    }

    public void doActionWithSchedule(){

    }

    public void updateSchedule(){

    }

    public void deleteSchedule(){
        Log.d(TAG, "CHOOSE THE DATE!!!!!!!!!!");
    }

    public void backToPreviousActivity(){
        scheduleActivity.finish();
    }
}
