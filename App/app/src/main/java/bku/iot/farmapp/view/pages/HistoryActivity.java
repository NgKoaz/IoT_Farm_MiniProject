package bku.iot.farmapp.view.pages;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.List;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HistoryController;
import bku.iot.farmapp.data.model.HistorySchedule;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.widgets.appbar.AppBar;
import bku.iot.farmapp.view.widgets.recyclerView.HistoryListAdapter;


public class HistoryActivity extends MyActivity {
    private HistoryController historyController;
    private AppBar appbar;
    private RecyclerView recyclerView;
    private HistoryListAdapter historyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyController = new HistoryController(this);
        setContentView(R.layout.activity_history);
    }

    @Override
    public void initViews() {
        super.initViews();
        appbar = new AppBar(this, () -> {
            historyController.backToPreviousActivity();
        });
        appbar.setHeaderText("History");

        recyclerView = findViewById(R.id.history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onBindViews() {
        super.onBindViews();
        historyController.loadHistoryView();
    }

    public void updateHistoryView(List<HistorySchedule> historyScheduleList) {
        if (historyListAdapter == null) {
            historyListAdapter = new HistoryListAdapter(this, historyScheduleList);
            recyclerView.setAdapter(historyListAdapter);
        } else {
            historyListAdapter.notifyDataSetChanged();
        }
    }
}