package bku.iot.farmapp;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.view.MainController;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SignInActivity;

public class MainActivity extends AppCompatActivity {
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