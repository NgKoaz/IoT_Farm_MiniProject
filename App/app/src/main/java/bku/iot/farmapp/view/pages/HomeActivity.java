package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HomeController;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class HomeActivity extends AppCompatActivity implements InitActivity {

    private HomeController homeController;
    private ImageView settingButton;
    private TextView tempValueText, moisValueText;
    private ImageView historyTaskButton, addScheduleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeController = new HomeController(this);
        setContentView(R.layout.activity_home);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        settingButton = findViewById(R.id.home_settingButton);
        tempValueText = findViewById(R.id.home_tempValueText);
        moisValueText = findViewById(R.id.home_moisValueText);
        historyTaskButton = findViewById(R.id.home_historyTaskButton);
        addScheduleButton = findViewById(R.id.home_addScheduleButton);
    }

    @Override
    public void bindEvents() {
        settingButton.setOnClickListener(v -> {
            homeController.startToSettingPage();
        });

        historyTaskButton.setOnClickListener(v -> {
            homeController.startToHistoryPage();
        });

        addScheduleButton.setOnClickListener(v -> {
            homeController.startToAddSchedulePage();
        });
    }
}