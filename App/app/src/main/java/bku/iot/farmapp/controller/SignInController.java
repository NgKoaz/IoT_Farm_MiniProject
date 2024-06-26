package bku.iot.farmapp.controller;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.local.LocalStorage;
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
        if (!res) signInActivity.showToast(message);
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
            signInActivity.dismissLoading();
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
                signInActivity.dismissLoading();
                navigateToHomePage();
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                signInActivity.dismissLoading();
                signInActivity.showToast(errorMessage);
            }
        });
    }

    public void navigateToHomePage(){
        signInActivity.startNewActivity(HomeActivity.class, null);
        signInActivity.finish();
    }

    public void navigateToSignUpPage(){
        signInActivity.startNewActivity(SignUpActivity.class, null);
        signInActivity.finish();
    }
}
