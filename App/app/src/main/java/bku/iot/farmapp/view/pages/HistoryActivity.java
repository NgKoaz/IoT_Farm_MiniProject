package bku.iot.farmapp.view.pages;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.HistoryController;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.widgets.appbar.AppBar;


public class HistoryActivity extends MyActivity {
    private HistoryController historyController;
    private AppBar appbar;
    private RecyclerView recyclerView;

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

//        MyAdapter myAdapter = new MyAdapter(this, list, false);
//        recyclerView.setAdapter(myAdapter);

    }
}