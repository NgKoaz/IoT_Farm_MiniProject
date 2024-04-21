package bku.iot.farmapp.controller;

import android.util.Log;

import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;

public class ScheduleController {

    private final String TAG = ScheduleController.class.getSimpleName();
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

    public void deleteSchedule(){
        Log.d(TAG, "CHOOSE THE DATE!!!!!!!!!!");
    }

    public void saveSchedule(boolean isNewAdding){
        if (isNewAdding){
            Log.d(TAG, "SAVE SCHEDULEEEEEEEEEEEEEEEEEE");
        } else {
            Log.d(TAG, "EDIT SCHEDULE!!!!!!!!!!!");
        }
    }

    public void backToPreviousActivity(){
        scheduleActivity.finish();
    }
}
