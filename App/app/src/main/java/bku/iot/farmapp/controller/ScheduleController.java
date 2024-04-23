package bku.iot.farmapp.controller;

import android.util.Log;

import bku.iot.farmapp.R;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;

public class ScheduleController {

    private final String TAG = ScheduleController.class.getSimpleName();
    private final int MONDAY = R.id.schedule_monButton;
    private final int TUESDAY = R.id.schedule_tueButton;
    private final int WEDNESDAY = R.id.schedule_wedButton;
    private final int THURSDAY = R.id.schedule_thurButton;
    private final int FRIDAY = R.id.schedule_friButton;
    private final int SATURDAY = R.id.schedule_satButton;
    private final int SUNDAY = R.id.schedule_sunButton;


    private final AddOrEditScheduleActivity scheduleActivity;

    public ScheduleController(AddOrEditScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;
    }

    public void changeTime(){
        Log.d(TAG, "CHANGE TIME PLS");
        scheduleActivity.updateTimeDisplay("06", "00");
    }

    public void chooseDate(){
        Log.d(TAG, "CHOOSE THE DATE!!!!!!!!!!");
        scheduleActivity.updateDateDisplay("Each Sunday,");
    }

    public void changeWeekday(int id){
        Log.d(TAG, String.format("%s: %d", "WEEKDAY", id));
    }

    public void saveSchedule(boolean isNewAdding){
        if (isNewAdding){
            Log.d(TAG, "SAVE SCHEDULEEEEEEEEEEEEEEEEEE");
        } else {
            Log.d(TAG, "EDIT SCHEDULE!!!!!!!!!!!");
        }
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
