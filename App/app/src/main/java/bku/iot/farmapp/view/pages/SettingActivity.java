package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SettingController;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.dialog.ChangePasswordDialog;
import bku.iot.farmapp.view.pages.dialog.LoadingPage;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class SettingActivity extends AppCompatActivity implements InitActivity {

    private SettingController settingController;
    private AppBar appbar;
    private LinearLayout changePasswordEntry, signOutEntry;
    private ChangePasswordDialog changePasswordDialog;
    private LoadingPage loadingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingController = new SettingController(this);
        setContentView(R.layout.activity_setting);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        appbar = new AppBar(this, () -> {
            settingController.backToPreviousActivity();
        });
        appbar.setHeaderText("Setting");
        changePasswordEntry = findViewById(R.id.setting_changePasswordEntry);
        signOutEntry = findViewById(R.id.setting_signOutEntry);


    }

    @Override
    public void bindEvents() {
        changePasswordEntry.setOnClickListener(v -> {
            settingController.openChangePasswordDialog();
        });

        signOutEntry.setOnClickListener(v -> {
            settingController.signOut();
        });

        loadingPage = new LoadingPage(this);
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

    @Override
    public void onBindView() {

    }

    public void showLoadingPage(){
        loadingPage.show();
    }

    public void dismissLoadingPage(){
        loadingPage.dismiss();
    }

    public void showChangePasswordDialog(){
        changePasswordDialog.show();
    }

    public void dismissChangePasswordDialog(){
        changePasswordDialog.dismiss();
    }
}