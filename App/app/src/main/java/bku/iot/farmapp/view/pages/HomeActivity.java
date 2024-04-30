package bku.iot.farmapp.view.pages;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HomeController;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.view.common.NetworkNotify;
import bku.iot.farmapp.view.widgets.recyclerView.MyAdapter;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class HomeActivity extends AppCompatActivity implements InitActivity {

    private HomeController homeController;
    private ImageView settingButton;
    private TextView helloUserText;
    private TextView timeText;
    private TextView tempValueText, moisValueText;
    private ImageView historyTaskButton, addScheduleButton;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private NetworkNotify networkNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        initViews();
        bindEvents();
        onBindView();

        homeController = new HomeController(this);
    }

    @Override
    public void initViews() {
        helloUserText = findViewById(R.id.home_helloUserText);
        settingButton = findViewById(R.id.home_settingButton);
        timeText = findViewById(R.id.home_timeText);
        tempValueText = findViewById(R.id.home_tempValueText);
        moisValueText = findViewById(R.id.home_moisValueText);
        historyTaskButton = findViewById(R.id.home_historyTaskButton);
        addScheduleButton = findViewById(R.id.home_addScheduleButton);
        recyclerView = findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        networkNotify = new NetworkNotify(this, this);
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