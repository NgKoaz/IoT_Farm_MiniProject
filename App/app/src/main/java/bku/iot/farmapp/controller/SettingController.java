package bku.iot.farmapp.controller;

import android.util.Log;

import java.util.Set;

import bku.iot.farmapp.view.pages.SettingActivity;

public class SettingController {

    private final String TAG = SettingController.class.getSimpleName();
    private SettingActivity settingActivity;

    public SettingController(SettingActivity settingActivity){
        this.settingActivity = settingActivity;
    }

    public void backToPreviousActivity(){
        settingActivity.finish();
    }

    public void changePassword(){
        Log.d(TAG, "I NEED TO CHANGE MY PASSS");
    }

    public void signOut(){
        Log.d(TAG, "SIGN OUT PLEAAAAAAAAAAASEEE");
    }

}
