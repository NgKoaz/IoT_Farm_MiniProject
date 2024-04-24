package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SignUpController;
import bku.iot.farmapp.utils.InputText;
import bku.iot.farmapp.view.pages.dialog.LoadingPage;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class SignUpActivity extends AppCompatActivity implements InitActivity {

    private SignUpController signUpController;
    private EditText inputGmail, inputPassword, confirmPassword;
    private Button signUpButton;
    private TextView signInNavText;
    private LoadingPage loadingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signUpController = new SignUpController(this);
        setContentView(R.layout.activity_sign_up);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        inputGmail = findViewById(R.id.signup_inputEmail);
        inputPassword = findViewById(R.id.signup_inputPassword);
        confirmPassword = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signup_btnRegister);
        signInNavText = findViewById(R.id.signup_signInNavText);

        loadingPage = new LoadingPage(this);
    }

    @Override
    public void bindEvents() {
        signUpButton.setOnClickListener(v -> {
            signUpController.signUp(
                    InputText.getStringFromInputEditText(inputGmail),
                    InputText.getStringFromInputEditText(inputPassword),
                    InputText.getStringFromInputEditText(confirmPassword)
            );
        });

        signInNavText.setOnClickListener(v -> {
            signUpController.navigateToSignInPage();
        });
    }

    @Override
    public void onBindView() {

    }

    public void showLoading(){
        loadingPage.show();
    }

    public void hideLoading(){
        loadingPage.dismiss();
    }
}