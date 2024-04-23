package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class AddOrEditScheduleActivity extends AppCompatActivity implements InitActivity {
    private ScheduleController scheduleController;
    private AppBar appbar;
    private String typePage;
    private TextView hourText, minuteText;
    private TextView dateInfoText;
    private ImageView choosingDateButton;
    private RadioGroup weekDaysRadioGroup;
    private TextInputEditText nameInput, waterInput;
    private TextInputEditText mixer1Input, mixer2Input, mixer3Input;
    private TextInputEditText area1Input, area2Input, area3Input;
    private Button deleteButton, saveButton;
    private View spaceBetweenButtons;
    private ScheduleInfo scheduleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1 is ADD and 0 is Edit
        typePage = "ADD";
        if (getIntent().getExtras() != null)
            typePage = getIntent().getExtras().getString("page");

        scheduleController = new ScheduleController(this);
        setContentView(R.layout.activity_add_or_edit_schedule);

        initViews();
        bindEvents();
        onBindView();
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
        weekDaysRadioGroup = findViewById(R.id.schedule_weekdaysGroup);

        nameInput = findViewById(R.id.schedule_nameInput);
        waterInput = findViewById(R.id.schedule_waterInput);
        mixer1Input = findViewById(R.id.schedule_mixer1Input);
        mixer2Input = findViewById(R.id.schedule_mixer2Input);
        mixer3Input = findViewById(R.id.schedule_mixer3Input);
        area1Input = findViewById(R.id.schedule_area1Input);
        area2Input = findViewById(R.id.schedule_area2Input);
        area3Input = findViewById(R.id.schedule_area3Input);

        deleteButton = findViewById(R.id.schedule_deleteButton);
        saveButton = findViewById(R.id.schedule_saveButton);
        spaceBetweenButtons = findViewById(R.id.schedule_spaceBetweenButtons);
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
            scheduleController.saveSchedule(typePage.equals("ADD"));
        });

        weekDaysRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            scheduleController.changeWeekday(checkedId);
        });
    }

    @Override
    public void onBindView() {
        if (typePage.equals("ADD")){
            loadAddSchedulePage();
        } else {
            loadEditSchedulePage();
        }
    }

    private void loadAddSchedulePage(){
        appbar.setHeaderText("Add Schedule");
        deleteButton.setVisibility(View.GONE);
        spaceBetweenButtons.setVisibility(View.GONE);
    }

    private void loadEditSchedulePage(){
        appbar.setHeaderText("Edit Schedule");
        scheduleInfo = getIntent().getParcelableExtra("scheduleInfo");

        assert scheduleInfo != null;
        nameInput.setText(scheduleInfo.name);
        waterInput.setText(String.valueOf(scheduleInfo.water));
        mixer1Input.setText(String.valueOf(scheduleInfo.mixer1));
        mixer2Input.setText(String.valueOf(scheduleInfo.mixer2));
        mixer3Input.setText(String.valueOf(scheduleInfo.mixer3));
        area1Input.setText(String.valueOf(scheduleInfo.area1));
        area2Input.setText(String.valueOf(scheduleInfo.area2));
        area3Input.setText(String.valueOf(scheduleInfo.area3));
    }

    public void updateTimeDisplay(String hour, String minute){
        hourText.setText(hour);
        minuteText.setText(minute);
    }

    public void updateDateDisplay(String dateString){
        dateInfoText.setText(dateString);
    }
}