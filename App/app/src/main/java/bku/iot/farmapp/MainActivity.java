package bku.iot.farmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import bku.iot.farmapp.services.local.LocalStorage;
import bku.iot.farmapp.view.pages.SignInActivity;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For LocalStorage instance
        new LocalStorage(getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_DELAY);
    }
}