package bku.iot.farmapp.controller;

import android.os.Bundle;

import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.view.pages.AddOrEditScheduleActivity;
import bku.iot.farmapp.view.pages.HistoryActivity;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SettingActivity;

public class HomeController {
    private final HomeActivity homeActivity;

    public HomeController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    public void startToSettingPage(){
        Navigation.startNewActivity(homeActivity, SettingActivity.class, null);
    }

    public void startToHistoryPage(){
        Navigation.startNewActivity(homeActivity, HistoryActivity.class, null);
    }

    public void startToAddSchedulePage(){
        Bundle extras = new Bundle();
        extras.putBoolean("ADD", true);
        Navigation.startNewActivity(homeActivity, AddOrEditScheduleActivity.class, extras);
    }

    public void startToEditSchedulePage(){
        Bundle extras = new Bundle();
        extras.putBoolean("ADD", false);
        Navigation.startNewActivity(homeActivity, AddOrEditScheduleActivity.class, extras);
    }

}
