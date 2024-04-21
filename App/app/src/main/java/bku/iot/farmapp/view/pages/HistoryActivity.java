package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HistoryController;
import bku.iot.farmapp.view.pages.appbar.AppBar;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class HistoryActivity extends AppCompatActivity implements InitActivity {

    private HistoryController historyController;
    private AppBar appbar;

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

    }

    @Override
    public void bindEvents() {

    }
}