package bku.iot.farmapp.view.pages;


import android.os.Bundle;
import android.widget.LinearLayout;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SettingController;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.widgets.appbar.AppBar;
import bku.iot.farmapp.view.widgets.dialog.ChangePasswordDialog;



public class SettingActivity extends MyActivity {

    private SettingController settingController;
    private AppBar appbar;
    private LinearLayout changePasswordEntry, signOutEntry;
    private ChangePasswordDialog changePasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingController = new SettingController(this);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initViews() {
        super.initViews();
        appbar = new AppBar(this, () -> {
            settingController.backToPreviousActivity();
        });
        appbar.setHeaderText("Setting");
        changePasswordEntry = findViewById(R.id.setting_changePasswordEntry);
        signOutEntry = findViewById(R.id.setting_signOutEntry);
    }

    @Override
    protected void setEvents() {
        changePasswordEntry.setOnClickListener(v -> {
            settingController.openChangePasswordDialog();
        });

        signOutEntry.setOnClickListener(v -> {
            settingController.signOut();
        });

        changePasswordDialog = new ChangePasswordDialog(
                this,
                v -> {
                    settingController.closeChangePasswordDialog();
                },
                v -> {
                    settingController.changePassword(
                            changePasswordDialog.getOldPassword(),
                            changePasswordDialog.getNewPassword(),
                            changePasswordDialog.getConfirmPassword()
                    );
                }
        );
    }

    public void showChangePasswordDialog(){
        changePasswordDialog.show();
    }

    public void dismissChangePasswordDialog(){
        changePasswordDialog.dismiss();
    }
}