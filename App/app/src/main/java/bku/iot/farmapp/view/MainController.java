package bku.iot.farmapp.view;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.MainActivity;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
import bku.iot.farmapp.view.common.Navigation;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SignInActivity;

public class MainController {
    private final String TAG = MainController.class.getSimpleName();
    private MainActivity mainActivity;

    public MainController(MainActivity mainActivity){
        this.mainActivity = mainActivity;

        autoSignIn();
    }

    private void autoSignIn(){
        LocalStorage localStorage = new LocalStorage(mainActivity);
        String email = localStorage.getString("email");
        String password = localStorage.getString("password");
        Log.d(TAG, "Auto Sign In| Email: " + email + "| Password: " + password);

        if (email.isEmpty() || password.isEmpty()) {
            Navigation.startNewActivity(mainActivity, SignInActivity.class, null);
            mainActivity.finish();
            return;
        }

        MyFirebaseAuth.gI().signIn(email, password, new MyFirebaseAuth.AuthListener() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                Navigation.startNewActivity(mainActivity, HomeActivity.class, null);
                mainActivity.finish();
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                Navigation.startNewActivity(mainActivity, SignInActivity.class, null);
                mainActivity.finish();
            }
        });
    }
}
