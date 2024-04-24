package bku.iot.farmapp.controller;

import android.util.Log;

import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.SettingActivity;

public class SettingController {

    private final String TAG = SettingController.class.getSimpleName();
    private final SettingActivity settingActivity;

    public SettingController(SettingActivity settingActivity){
        this.settingActivity = settingActivity;
    }

    private boolean checkChangePasswordInput(String oldPassword, String newPassword, String confirmPassword){
        boolean res = true;
        String message = "";
        if (oldPassword.isEmpty()){
            res = false;
            message = "Old password field is empty";
        } else if (newPassword.isEmpty()) {
            res = false;
            message = "New password field is empty";
        } else if (confirmPassword.isEmpty()){
            res = false;
            message = "Confirm password field is empty";
        } else if (!newPassword.equals(confirmPassword)) {
            res = false;
            message = "Confirm password is not match!";
        } else if (newPassword.length() < 6) {
            res = false;
            message = "New password is too short, must have length > 6";
        }
        if (!res) ToastManager.showToast(settingActivity, message);
        return res;
    }

    public void backToPreviousActivity(){
        settingActivity.setResult(ActivityResultCode.DO_NOTHING);
        settingActivity.finish();
    }

    public void openChangePasswordDialog(){
        settingActivity.showChangePasswordDialog();
    }

    public void closeChangePasswordDialog(){
        settingActivity.dismissChangePasswordDialog();
    }

    public void changePassword(String oldPassword, String newPassword, String confirmPassword){
        settingActivity.showLoadingPage();
        if (!checkChangePasswordInput(oldPassword, newPassword, confirmPassword)){
            return;
        }
        MyFirebaseAuth.gI().changePassword(newPassword, isSuccessful -> {
            if (isSuccessful) {
                settingActivity.dismissChangePasswordDialog();
                settingActivity.dismissLoadingPage();
                ToastManager.showToast(settingActivity, "Change password successfully!");
            } else {
                settingActivity.dismissLoadingPage();
                ToastManager.showToast(settingActivity, "Non-expected error!");
            }
        });
    }

    public void signOut(){
        LocalStorage localStorage = new LocalStorage(settingActivity);
        localStorage.clear();
        settingActivity.setResult(ActivityResultCode.SIGN_OUT);
        settingActivity.finish();
    }

}
