package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HistoryController;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.recyclerView.MyAdapter;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class HistoryActivity extends AppCompatActivity implements InitActivity {

    private HistoryController historyController;
    private AppBar appbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyController = new HistoryController(this);
        setContentView(R.layout.activity_history);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        appbar = new AppBar(this, () -> {
            historyController.backToPreviousActivity();
        });
        appbar.setHeaderText("History");

        recyclerView = findViewById(R.id.history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        MyAdapter myAdapter = new MyAdapter(this, list, false);
//        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void bindEvents() {

    }

    @Override
    public void onBindView() {

    }
}