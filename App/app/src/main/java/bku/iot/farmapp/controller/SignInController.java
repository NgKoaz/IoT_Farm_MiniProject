package bku.iot.farmapp.controller;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
import bku.iot.farmapp.view.common.Navigation;
import bku.iot.farmapp.view.common.ToastManager;
import bku.iot.farmapp.view.pages.HomeActivity;
import bku.iot.farmapp.view.pages.SignInActivity;
import bku.iot.farmapp.view.pages.SignUpActivity;

public class SignInController {
    private final String TAG = SignInActivity.class.getSimpleName();
    private final SignInActivity signInActivity;
    private final LocalStorage localStorage;

    public SignInController(SignInActivity signInActivity){
        this.signInActivity = signInActivity;
        localStorage = new LocalStorage(signInActivity);
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

    private void saveCredentialIntoLocalStorage(String email, String password){
        localStorage.putString("email", email);
        localStorage.putString("password", password);
    }

    private void clearLocalStorage(){
        localStorage.clear();
    }

    public void signIn(String email, String password, boolean isRememberMe){
        signInActivity.showLoading();

        if (!checkCredentialForSignIn(email, password)) {
            signInActivity.dimissLoading();
            return;
        }
        MyFirebaseAuth.gI().signIn(email, password, new MyFirebaseAuth.AuthListener() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                if (isRememberMe) {
                    saveCredentialIntoLocalStorage(email, password);
                } else {
                    clearLocalStorage();
                }
                signInActivity.dimissLoading();
                navigateToHomePage();
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                signInActivity.dimissLoading();
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
