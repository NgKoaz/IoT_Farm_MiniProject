package bku.iot.farmapp.view.pages;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HomeController;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.widgets.recyclerView.ScheduleListAdapter;


public class HomeActivity extends MyActivity {
    private HomeController homeController;
    private ImageView settingButton;
    private TextView helloUserText;
    private TextView timeText;
    private TextView tempValueText, moisValueText;
    private ImageView historyTaskButton, addScheduleButton;
    private RecyclerView recyclerView;
    private ScheduleListAdapter scheduleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeController = new HomeController(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        helloUserText = findViewById(R.id.home_helloUserText);
        settingButton = findViewById(R.id.home_settingButton);
        timeText = findViewById(R.id.home_timeText);
        tempValueText = findViewById(R.id.home_tempValueText);
        moisValueText = findViewById(R.id.home_moisValueText);
        historyTaskButton = findViewById(R.id.home_historyTaskButton);
        addScheduleButton = findViewById(R.id.home_addScheduleButton);
        recyclerView = findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void setEvents() {
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
    protected void onBindViews() {
        super.onBindViews();
        homeController.refreshRecyclerView();
        homeController.startThreadUpdateClock();
    }

    public void updateScheduleList(List<Schedule> scheduleList){
        if (scheduleListAdapter == null){
            scheduleListAdapter = new ScheduleListAdapter(this, scheduleList, (buttonView, isCheck, schedule, position) -> {
                homeController.handleSwitch(buttonView, isCheck, schedule, position);
            });
            recyclerView.setAdapter(scheduleListAdapter);
        } else {
            scheduleListAdapter.notifyDataSetChanged();
        }
    }

    public void updateAdapterByPosition(int position) {
        scheduleListAdapter.notifyItemChanged(position);
    }

    public void updateTimeText(String time){
        timeText.setText(time);
    }

    public void updateTempText(String value){
        String text = value + "°C";
        tempValueText.setText(text);
    }

    public void updateMoisText(String value){
        String text = value + "%";
        moisValueText.setText(text);
    }

    public void updateHelloUserText(String user){
        StringBuilder displayText = new StringBuilder("Hello, ");
        displayText.append(user);
        helloUserText.setText(displayText.toString());
    }
}