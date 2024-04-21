package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SettingController;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class SettingActivity extends AppCompatActivity implements InitActivity {

    private SettingController settingController;
    private AppBar appbar;
    private LinearLayout changePasswordEntry, signOutEntry;

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
            settingController.changePassword();
        });

        signOutEntry.setOnClickListener(v -> {
            settingController.signOut();
        });
    }
}