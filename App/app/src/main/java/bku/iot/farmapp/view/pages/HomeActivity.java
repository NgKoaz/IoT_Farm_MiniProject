package bku.iot.farmapp.view.pages;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HomeController;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.data.enums.ActivityResultCode;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.view.pages.recyclerView.MyAdapter;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class HomeActivity extends AppCompatActivity implements InitActivity {

    private HomeController homeController;
    private ImageView settingButton;
    private TextView tempValueText, moisValueText;
    private ImageView historyTaskButton, addScheduleButton;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

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
        recyclerView = findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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

    @Override
    public void onBindView() {

    }

    public void updateScheduleList(List<ScheduleInfo> scheduleInfoList){
        if (myAdapter == null){
            myAdapter = new MyAdapter(this, scheduleInfoList, true);
            recyclerView.setAdapter(myAdapter);
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }

    public void updateTempText(String value){
        String text = value + "°C";
        tempValueText.setText(text);
    }

    public void updateMoisText(String value){
        String text = value + "%";
        moisValueText.setText(text);
    }
}