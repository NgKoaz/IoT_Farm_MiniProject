package bku.iot.farmapp.controller;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
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

    private boolean checkCredentialForSignIn(String email, String password){
        boolean res = true;
        String message = "";
        if (email.isEmpty()){
            res = false;
            message = "Enter your email!";
        } else if (password.isEmpty()){
            res = false;
            message = "Enter your password!";
        }
        if (!res) ToastManager.showToast(signInActivity, message);
        return res;
    }

    private void saveSignInInfoIntoLocalStorage(String email, String password){
        LocalStorage.gI().putString("email", email);
        LocalStorage.gI().putString("password", password);
    }

    private void clearLocalStorage(){
        LocalStorage.gI().clear();
    }

    public void signIn(String email, String password, boolean isRememberMe){
        signInActivity.showLoading();

        if (!checkCredentialForSignIn(email, password)) {
            signInActivity.hideLoading();
            return;
        }
        MyFirebaseAuth.gI().signIn(email, password, new MyFirebaseAuth.AuthListener() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                if (isRememberMe) {
                    saveSignInInfoIntoLocalStorage(email, password);
                } else {
                    clearLocalStorage();
                }
                signInActivity.hideLoading();
                navigateToHomePage();
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                signInActivity.hideLoading();
                ToastManager.showToast(signInActivity, errorMessage);
            }
        });
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
