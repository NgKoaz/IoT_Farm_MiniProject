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

import org.checkerframework.checker.units.qual.Area;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.ScheduleController;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
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
    private final List<CheckBox> weekdayBoxes = new LinkedList<>();
    private TextInputEditText nameInput, waterInput;
    private TextInputEditText mixer1Input, mixer2Input, mixer3Input;
    private TextInputEditText area1Input, area2Input, area3Input;
    private Button deleteButton, saveButton, updateButton;
    private View spaceBetweenButtons;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private ScheduleInfo scheduleInfo;
    private MixerInputDialog mixerInputDialog;
    private AreaInputDialog areaInputDialog;
    private Button testBtn;

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
        waterInput = findViewById(R.id.schedule_waterInput);
        mixer1Input = findViewById(R.id.schedule_mixer1Input);
        mixer2Input = findViewById(R.id.schedule_mixer2Input);
        mixer3Input = findViewById(R.id.schedule_mixer3Input);
        area1Input = findViewById(R.id.schedule_area1Input);
        area2Input = findViewById(R.id.schedule_area2Input);
        area3Input = findViewById(R.id.schedule_area3Input);

        deleteButton = findViewById(R.id.schedule_deleteButton);
        saveButton = findViewById(R.id.schedule_saveButton);
        updateButton = findViewById(R.id.schedule_updateButton);
        spaceBetweenButtons = findViewById(R.id.schedule_spaceBetweenButtons);

        datePickerDialog = new DatePickerDialog(this);
    }

    @Override
    protected void setEvents() {
        mixerInputDialog.setOkButton(v -> {
            mixerInputDialog.dismiss();
        });
        areaInputDialog.setOkButton(v -> {
            areaInputDialog.dismiss();
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
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                scheduleController.deleteSchedule(scheduleInfo);
            });
        });
        saveButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                scheduleController.saveSchedule(
                        Utils.getStringFromInputEditText(nameInput),
                        Double.parseDouble(Utils.getStringFromInputEditText(waterInput)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer1Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer2Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer3Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area1Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area2Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area3Input))
                );
            });
        });
        updateButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                scheduleController.updateSchedule(
                        scheduleInfo,
                        Utils.getStringFromInputEditText(nameInput),
                        Double.parseDouble(Utils.getStringFromInputEditText(waterInput)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer1Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer2Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(mixer3Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area1Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area2Input)),
                        Double.parseDouble(Utils.getStringFromInputEditText(area3Input)));
            });
        });

        // Weekday checkboxes
        for (CheckBox box : weekdayBoxes) {
            box.setOnClickListener(v -> {
                if (box.isChecked()) {
                    scheduleController.setWeekday(Weekdays.MONDAY);
                }
            });
        }

        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            scheduleController.setDate(dayOfMonth, month + 1, year);
        });
        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            scheduleController.setTime(hourOfDay, minute);
        }, 6, 0, true);




        testBtn = findViewById(R.id.testBtn);
        testBtn.setOnClickListener(v -> {
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
        for (CheckBox box : weekdayBoxes) {
            box.setChecked(false);
        }
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

    public void updateCurrentTime(String time){
        currentTimeText.setText(time);
    }
}