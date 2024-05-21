package bku.iot.farmapp.view.pages;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SignUpController;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.common.Utils;


public class SignUpActivity extends MyActivity {

    private SignUpController signUpController;
    private EditText inputGmail, inputPassword, confirmPassword;
    private Button signUpButton;
    private TextView signInNavText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signUpController = new SignUpController(this);
        setContentView(R.layout.activity_sign_up);

    }

    @Override
    protected void initViews() {
        inputGmail = findViewById(R.id.signup_inputEmail);
        inputPassword = findViewById(R.id.signup_inputPassword);
        confirmPassword = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signup_btnRegister);
        signInNavText = findViewById(R.id.signup_signInNavText);
    }

    @Override
    protected void setEvents() {
        signUpButton.setOnClickListener(v -> {
            signUpController.signUp(
                    Utils.getStringFromInputEditText(inputGmail),
                    Utils.getStringFromInputEditText(inputPassword),
                    Utils.getStringFromInputEditText(confirmPassword)
            );
        });

        signInNavText.setOnClickListener(v -> {
            signUpController.navigateToSignInPage();
        });
    }
}