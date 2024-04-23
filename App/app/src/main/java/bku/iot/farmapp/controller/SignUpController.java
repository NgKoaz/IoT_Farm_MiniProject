package bku.iot.farmapp.controller;

import com.google.firebase.auth.FirebaseUser;

import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.utils.ToastManager;
import bku.iot.farmapp.view.pages.SignInActivity;
import bku.iot.farmapp.view.pages.SignUpActivity;

public class SignUpController {
    private final SignUpActivity signUpActivity;

    public SignUpController(SignUpActivity signUpActivity){
        this.signUpActivity = signUpActivity;
    }

    private boolean checkCredentialForSignUp(String email, String password, String confirmPassword){
        boolean res = true;
        String message = "";
        if (email.isEmpty()) {
            res = false;
            message = "Enter your email!";
        } else if (password.isEmpty()){
            res = false;
            message = "Enter your password!";
        } else if (confirmPassword.isEmpty()){
            res = false;
            message = "Enter confirm password field!";
        } else if (!password.equals(confirmPassword)) {
            res = false;
            message = "Confirm password not match!";
        }

        if (!res) ToastManager.showToast(signUpActivity, message);
        return res;
    }

    public void signUp(String email, String password, String confirmPassword){
        signUpActivity.showLoading();
        if (!checkCredentialForSignUp(email, password, confirmPassword)) {
            signUpActivity.hideLoading();
            return;
        }

        MyFirebaseAuth.gI().signUp(email, password, new MyFirebaseAuth.AuthListener() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                signUpActivity.hideLoading();
                ToastManager.showToast(signUpActivity, "Sign up successfully!");
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                signUpActivity.hideLoading();
                ToastManager.showToast(signUpActivity, errorMessage);
            }
        });
    }

    public void navigateToSignInPage(){
        Navigation.startNewActivity(signUpActivity, SignInActivity.class, null);
        signUpActivity.finish();
    }
}
