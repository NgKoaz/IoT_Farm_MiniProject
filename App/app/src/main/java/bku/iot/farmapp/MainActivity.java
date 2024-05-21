package bku.iot.farmapp;


import android.os.Bundle;
import android.os.Handler;

import bku.iot.farmapp.view.common.MyActivity;

public class MainActivity extends MyActivity {
    private MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mainController = new MainController(this);
        }, 1000);

    }
}