package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class AddOrEditScheduleActivity extends AppCompatActivity implements InitActivity {

    private ScheduleController scheduleController;
    private AppBar appbar;
    private boolean isAddPage;
    private TextView hourText, minuteText;
    private TextView dateInfoText;
    private ImageView choosingDateButton;
    private RadioGroup weekDays;
    private Button deleteButton, saveButton;
    private View spaceBetweenButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1 is ADD and 0 is Edit
        isAddPage = true;
        if (getIntent().getExtras() != null)
            isAddPage = getIntent().getExtras().getBoolean("ADD");

        scheduleController = new ScheduleController(this);
        setContentView(R.layout.activity_add_or_edit_schedule);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        appbar = new AppBar(this, () -> {
            scheduleController.backToPreviousActivity();
        });
        hourText = findViewById(R.id.schedule_hourText);
        minuteText = findViewById(R.id.schedule_minuteText);
        dateInfoText = findViewById(R.id.schedule_dateInfoText);
        choosingDateButton = findViewById(R.id.schedule_choosingDateButton);
        weekDays = findViewById(R.id.schedule_weekdaysGroup);
        deleteButton = findViewById(R.id.schedule_deleteButton);
        saveButton = findViewById(R.id.schedule_saveButton);
        spaceBetweenButtons = findViewById(R.id.schedule_spaceBetweenButtons);

        if (isAddPage){
            appbar.setHeaderText("Add Schedule");
            deleteButton.setVisibility(View.GONE);
            spaceBetweenButtons.setVisibility(View.GONE);
        } else {
            appbar.setHeaderText("Edit Schedule");
        }
    }

    @Override
    public void bindEvents() {
        hourText.setOnClickListener(v -> {
            scheduleController.changeTime();
        });
        minuteText.setOnClickListener(v -> {
            scheduleController.changeTime();
        });
        choosingDateButton.setOnClickListener(v -> {
            scheduleController.chooseDate();
        });
        deleteButton.setOnClickListener(v -> {
            scheduleController.deleteSchedule();
        });
        saveButton.setOnClickListener(v -> {
            scheduleController.saveSchedule(isAddPage);
        });
    }

    public void updateTimeDisplay(String hour, String minute){
        hourText.setText(hour);
        minuteText.setText(minute);
    }

    public void updateDateDisplay(String dateString){
        dateInfoText.setText(dateString);
    }
}