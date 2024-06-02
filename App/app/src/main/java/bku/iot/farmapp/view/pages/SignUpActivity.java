package bku.iot.farmapp.view.pages;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SignUpController;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.common.Utils;


public class SignUpActivity extends MyActivity {

    private SignUpController signUpController;
    private TextInputLayout confirmPasswordLayout;
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
        super.initViews();
        inputGmail = findViewById(R.id.signup_inputEmail);
        inputPassword = findViewById(R.id.signup_inputPassword);
        confirmPassword = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signup_btnRegister);
        signInNavText = findViewById(R.id.signup_signInNavText);
        confirmPasswordLayout = findViewById(R.id.signup_confirmPasswordLayout);
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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPassword = Utils.getStringFromInputEditText(inputPassword);
                String conPassword = s.toString();
                if (conPassword.isEmpty()) {
                    confirmPasswordLayout.setError("");
                    return;
                }
                if (!newPassword.equals(conPassword)) {
                    confirmPasswordLayout.setError("Not match!");
                } else {
                    confirmPasswordLayout.setError("");
                }
            }
        };
        inputPassword.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
    }
}