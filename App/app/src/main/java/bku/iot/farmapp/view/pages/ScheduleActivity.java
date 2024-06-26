package bku.iot.farmapp.view.pages;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.common.Utils;
import bku.iot.farmapp.view.widgets.appbar.AppBar;
import bku.iot.farmapp.view.widgets.dialog.AreaInputDialog;
import bku.iot.farmapp.view.widgets.dialog.MixerInputDialog;


public class ScheduleActivity extends MyActivity {
    private ScheduleController scheduleController;
    private AppBar appbar;
    private String typePage;
    private TextView currentTimeText;
    private TextView hourText, minuteText;
    private TextView dateInfoText;
    private ImageView choosingDateButton;
    private final List<CheckBox> weekdayBoxes = new ArrayList<>();
    private TextInputEditText nameInput, volumeInput;
    private Schedule schedule;
    private Button deleteButton, saveButton, updateButton;
    private View spaceBetweenButtons;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private MixerInputDialog mixerInputDialog;
    private AreaInputDialog areaInputDialog;
    private ImageView mixerChangeButton, areaChangeButton;
    private TextView waterText, mixer1Text, mixer2Text, mixer3Text;
    private TextView area1Text, area2Text, area3Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1 is ADD and 0 is Edit
        typePage = "ADD";
        if (getIntent().getExtras() != null)
            typePage = getIntent().getExtras().getString("page");


        scheduleController = new ScheduleController(this);
        setContentView(R.layout.activity_schedule);
    }

    @Override
    protected void initViews() {
        super.initViews();
        appbar = new AppBar(this, () -> {
            scheduleController.backToPreviousActivity();
        });
        currentTimeText = findViewById(R.id.schedule_currentTimeText);

        mixerInputDialog = new MixerInputDialog(this);
        areaInputDialog = new AreaInputDialog(this);

        hourText = findViewById(R.id.schedule_hourText);
        minuteText = findViewById(R.id.schedule_minuteText);
        dateInfoText = findViewById(R.id.schedule_dateInfoText);
        choosingDateButton = findViewById(R.id.schedule_choosingDateButton);

        weekdayBoxes.add(findViewById(R.id.schedule_mondayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_tuesdayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_wednesdayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_thursdayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_fridayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_saturdayBox));
        weekdayBoxes.add(findViewById(R.id.schedule_sundayBox));

        nameInput = findViewById(R.id.schedule_nameInput);
        volumeInput = findViewById(R.id.schedule_volumeInput);
        waterText = findViewById(R.id.schedule_waterRatio);
        mixer1Text = findViewById(R.id.schedule_mixer1Ratio);
        mixer2Text = findViewById(R.id.schedule_mixer2Ratio);
        mixer3Text = findViewById(R.id.schedule_mixer3Ratio);
        area1Text = findViewById(R.id.schedule_area1Ratio);
        area2Text = findViewById(R.id.schedule_area2Ratio);
        area3Text = findViewById(R.id.schedule_area3Ratio);

        mixerChangeButton = findViewById(R.id.schedule_mixerChangeButton);
        areaChangeButton = findViewById(R.id.schedule_areaChangeButton);

        deleteButton = findViewById(R.id.schedule_deleteButton);
        saveButton = findViewById(R.id.schedule_saveButton);
        updateButton = findViewById(R.id.schedule_updateButton);
        spaceBetweenButtons = findViewById(R.id.schedule_spaceBetweenButtons);

        datePickerDialog = new DatePickerDialog(this);
    }

    @Override
    protected void setEvents() {
        mixerInputDialog.setOkButton(v -> {
            scheduleController.setWaterAndMixerRatio(
                    mixerInputDialog.getWater(),
                    mixerInputDialog.getMixer1(),
                    mixerInputDialog.getMixer2(),
                    mixerInputDialog.getMixer3()
            );
        });
        areaInputDialog.setOkButton(v -> {
            scheduleController.setAreaRatio(
                    areaInputDialog.getArea1(),
                    areaInputDialog.getArea2(),
                    areaInputDialog.getArea3()
            );
        });

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
                scheduleController.deleteSchedule(schedule);
        });
        saveButton.setOnClickListener(v -> {
            scheduleController.addSchedule(
                    Utils.getStringFromInputEditText(nameInput),
                    Utils.getStringFromInputEditText(volumeInput)
            );
        });

        updateButton.setOnClickListener(v -> {
                scheduleController.updateSchedule(
                        schedule,
                        Utils.getStringFromInputEditText(nameInput),
                        Utils.getStringFromInputEditText(volumeInput)
                );
        });

        // Weekday checkboxes
        for (int i = 0; i < 7; i++) {
            CheckBox box = weekdayBoxes.get(i);
            final int finalI = i;
            box.setOnClickListener(v -> {
                scheduleController.setWeekday(box.isChecked(), finalI);
            });
        }

        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            scheduleController.setDate(year, month + 1, dayOfMonth);
        });
        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            scheduleController.setTime(hourOfDay, minute);
        }, 6, 0, true);

        mixerChangeButton.setOnClickListener(v -> {
            mixerInputDialog.show();
        });
        areaChangeButton.setOnClickListener(v -> {
            areaInputDialog.show();
        });
    }

    @Override
    protected void onBindViews() {
        // This is a edit page or add page?
        if (typePage.equals("ADD")){
            loadAddSchedulePage();
        } else {
            loadEditSchedulePage();
        }

        scheduleController.startThreadUpdateCurrentTime();
    }

    private void loadAddSchedulePage(){
        // Set default time, date picker
        scheduleController.setDefaultDateTime();

        appbar.setHeaderText("Add Schedule");

        saveButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        spaceBetweenButtons.setVisibility(View.GONE);
    }

    private void loadEditSchedulePage(){
        appbar.setHeaderText("Edit Schedule");

        saveButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        spaceBetweenButtons.setVisibility(View.VISIBLE);

        schedule = getIntent().getParcelableExtra("schedule");
        scheduleController.loadEditSchedulePage(schedule);

        scheduleController.setDateTimeForEditing(schedule);
    }

    public void updateNameText(String name) {
        nameInput.setText(name);
    }

    public void updateVolumeText(String volume) {
        volumeInput.setText(volume);
    }

    public void updateCheckBox(List<Integer> weekday){
        for (Integer wd : weekday) {
            weekdayBoxes.get(wd).setChecked(true);
        }
    }

    public void updateAreaRatioText(String area1, String area2, String area3) {
        area1Text.setText(area1);
        area2Text.setText(area2);
        area3Text.setText(area3);
    }

    public void updateMixerRatioText(String water, String mixer1, String mixer2, String mixer3) {
        waterText.setText(water);
        mixer1Text.setText(mixer1);
        mixer2Text.setText(mixer2);
        mixer3Text.setText(mixer3);
    }

    public void dismissAreaInputDialog() {
        areaInputDialog.dismiss();
    }

    public void dismissMixerInputDialog() {
        mixerInputDialog.dismiss();
    }

    public void openTimePickerDialog(int hourOfDay, int minute){
        timePickerDialog.updateTime(hourOfDay, minute);
        timePickerDialog.show();
    }

    public void openDatePickerDialog(int year, int month, int dayOfMonth){
        if (dayOfMonth > 0 && year > 2023 || month > 0) {
            datePickerDialog.updateDate(year, month - 1, dayOfMonth);
        }
        datePickerDialog.show();
    }

    public void clearWeekdayCheck(){
        for (CheckBox box : weekdayBoxes) {
            box.setChecked(false);
        }
    }

    public void updateTimeDisplay(int hour, int minute){
        hourText.setText(String.format(hour < 10 ? "0%s" : "%s", hour));
        minuteText.setText(String.format(minute < 10 ? "0%s" : "%s", minute));
    }

    public void updateDateDisplay(int year, int month, int day){
        Calendar currentTime = Calendar.getInstance();

        int curDay = currentTime.get(Calendar.DAY_OF_MONTH);
        int curMonth = currentTime.get(Calendar.MONTH) + 1;
        int curYear = currentTime.get(Calendar.YEAR);
        boolean isToday = curDay == day && curMonth == month && curYear == year;

        currentTime.add(Calendar.DAY_OF_YEAR, 1);

        int tomorrowDay = currentTime.get(Calendar.DAY_OF_MONTH);
        int tomorrowMonth = currentTime.get(Calendar.MONTH) + 1;
        int tomorrowYear = currentTime.get(Calendar.YEAR);
        boolean isTomorrow = tomorrowDay == day && tomorrowMonth == month && tomorrowYear == year;

        String displayText = String.format(String.format(day < 10 ? "0%s" : "%s", day)) + "/" +
                String.format(month < 10 ? "0%s" : "%s", month) + "/" +
                year + ((isToday) ? " (Today)" : (isTomorrow) ? " (Tomorrow)" : "");
        dateInfoText.setText(displayText);
    }

    public void updateWeekdayDisplay(String text){
        dateInfoText.setText(text);
    }

    public void updateCurrentTime(String time){
        currentTimeText.setText(time);
    }

}