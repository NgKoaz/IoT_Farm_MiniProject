package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.utils.InputText;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.dialog.LoadingPage;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class AddOrEditScheduleActivity extends AppCompatActivity implements InitActivity {
    private ScheduleController scheduleController;
    private AppBar appbar;
    private String typePage;
    private TextView hourText, minuteText;
    private TextView dateInfoText;
    private ImageView choosingDateButton;
    private RadioGroup weekDaysRadioGroup;
    private RadioButton monButton, tueButton, wedButton, thurButton, friButton, satButton, sunButton;
    private TextInputEditText nameInput, waterInput;
    private TextInputEditText mixer1Input, mixer2Input, mixer3Input;
    private TextInputEditText area1Input, area2Input, area3Input;
    private Button deleteButton, saveButton;
    private View spaceBetweenButtons;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private LoadingPage loadingPage;
    private ScheduleInfo scheduleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1 is ADD and 0 is Edit
        typePage = "ADD";
        if (getIntent().getExtras() != null)
            typePage = getIntent().getExtras().getString("page");

        loadingPage = new LoadingPage(this);
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

        monButton = findViewById(R.id.schedule_monButton);
        tueButton = findViewById(R.id.schedule_tueButton);
        wedButton = findViewById(R.id.schedule_wedButton);
        thurButton = findViewById(R.id.schedule_thurButton);
        friButton = findViewById(R.id.schedule_friButton);
        satButton = findViewById(R.id.schedule_satButton);
        sunButton = findViewById(R.id.schedule_sunButton);

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

        datePickerDialog = new DatePickerDialog(this);
    }

    @Override
    public void bindEvents() {
        hourText.setOnClickListener(v -> {
            scheduleController.openTimePickerDialog();
        });
        minuteText.setOnClickListener(v -> {
            scheduleController.openTimePickerDialog();
        });
        choosingDateButton.setOnClickListener(v -> {
            scheduleController.openDatePickerDialog();
        });
        deleteButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                scheduleController.deleteSchedule(scheduleInfo);
            });
        });
        saveButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                scheduleController.saveSchedule(
                        typePage.equals("ADD"),
                        InputText.getStringFromInputEditText(nameInput),
                        Double.parseDouble(InputText.getStringFromInputEditText(waterInput)),
                        Double.parseDouble(InputText.getStringFromInputEditText(mixer1Input)),
                        Double.parseDouble(InputText.getStringFromInputEditText(mixer2Input)),
                        Double.parseDouble(InputText.getStringFromInputEditText(mixer3Input)),
                        Double.parseDouble(InputText.getStringFromInputEditText(area1Input)),
                        Double.parseDouble(InputText.getStringFromInputEditText(area2Input)),
                        Double.parseDouble(InputText.getStringFromInputEditText(area3Input))
                );
            });
        });

        // RADIO GROUP
        monButton.setOnClickListener(v -> {
            if (monButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.MONDAY);
            }
        });
        tueButton.setOnClickListener(v -> {
            if (tueButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.TUESDAY);
            }
        });
        wedButton.setOnClickListener(v -> {
            if (wedButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.WEDNESDAY);
            }
        });
        thurButton.setOnClickListener(v -> {
            if (thurButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.THURSDAY);
            }
        });
        friButton.setOnClickListener(v -> {
            if (friButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.FRIDAY);
            }
        });
        satButton.setOnClickListener(v -> {
            if (satButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.SATURDAY);
            }
        });
        sunButton.setOnClickListener(v -> {
            if (friButton.isChecked()) {
                scheduleController.setWeekday(Weekdays.SUNDAY);
            }
        });
        // END --- RADIO GROUP


        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            scheduleController.setDate(dayOfMonth, month + 1, year);
        });
        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            scheduleController.setTime(hourOfDay, minute);
        }, 6, 0, true);
    }

    @Override
    public void onBindView() {
        // This is a edit page or add page?
        if (typePage.equals("ADD")){
            loadAddSchedulePage();
        } else {
            loadEditSchedulePage();
        }
    }

    private void loadAddSchedulePage(){
        // Set default time, date picker
        scheduleController.setDefaultDateTime();

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

        scheduleController.setDateTimeForEditting(scheduleInfo);
    }

    public void openTimePickerDialog(){
        timePickerDialog.show();
    }

    public void openDatePickerDialog(){
        datePickerDialog.show();
    }

    public void clearWeekdayCheck(){
        weekDaysRadioGroup.clearCheck();
    }

    public void showLoading(){
        loadingPage.show();
    }

    public void dismissLoading(){
        loadingPage.dismiss();
    }

    public void updateTimeDisplay(String hour, String minute){
        if (hour.length() == 1) hour = "0" + hour;
        if (minute.length() == 1) minute = "0" + minute;
        hourText.setText(hour);
        minuteText.setText(minute);
    }

    public void updateDateDisplay(String dateString){
        dateInfoText.setText(dateString);
    }
}