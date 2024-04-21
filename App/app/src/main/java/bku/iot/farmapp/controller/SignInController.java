package bku.iot.farmapp.controller;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SignInActivity;
import bku.iot.farmapp.view.pages.SignUpActivity;

public class SignInController {
    private final String TAG = SignInActivity.class.getSimpleName();
    private final SignInActivity signInActivity;

    public SignInController(SignInActivity signInActivity){
        this.signInActivity = signInActivity;
    }

    public void signIn(String email, String password, boolean isRememberMe){
        if (email == null || email.isEmpty()){
            ToastManager.showToast(signInActivity, "Please enter your email!");
            return;
        }
        if (password == null || password.isEmpty()){
            ToastManager.showToast(signInActivity, "Please enter your email!");
            return;
        }
        navigateToHomePage();

//        MyFirebaseAuth.gI().signIn(email, password, new MyFirebaseAuth.AuthListener() {
//            @Override
//            public void onAuthSuccess(FirebaseUser user) {
//                if (isRememberMe){
////                    contextLocalStorage.putString("email", email);
////                    contextLocalStorage.putString("password", password);
//
//                    Log.d(TAG, "Save Email: " + email);
//                    Log.d(TAG, "Save Password: " + password);
//                } else {
////                    contextLocalStorage.clear();
//                }
//                Navigation.startNewActivity(signInActivity, HomeActivity.class, null);
//                signInActivity.finish();
//            }

//            @Override
//            public void onAuthFailure(String errorMessage) {
////                contextLocalStorage.clear();
////                signInActivity.showToast("Email and password may wrong!");
//            }
//        });
    }

    public void navigateToHomePage(){
        Navigation.startNewActivity(signInActivity, HomeActivity.class, null);
        signInActivity.finish();
    }

    public void navigateToSignUpPage(){
        Navigation.startNewActivity(signInActivity, SignUpActivity.class, null);
        signInActivity.finish();
    }
}
