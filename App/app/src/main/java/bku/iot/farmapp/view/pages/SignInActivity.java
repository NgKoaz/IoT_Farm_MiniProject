package bku.iot.farmapp.view.pages;


import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SignInController;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.common.Utils;
import bku.iot.farmapp.view.widgets.dialog.LoadingPage;


public class SignInActivity extends MyActivity {
    private SignInController signInController;
    private TextInputEditText emailInputText, passwordInputText;
    private CheckBox rememberMe;
    private Button signInButton;
    private TextView signUpNavText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signInController = new SignInController(this);
        setContentView(R.layout.activity_sign_in);
    }

    @Override
    protected void initViews() {
        emailInputText = findViewById(R.id.signin_emailInputText);
        passwordInputText = findViewById(R.id.signin_passwordInputText);
        rememberMe = findViewById(R.id.signin_rememberMe);
        signInButton = findViewById(R.id.signin_signInButton);
        signUpNavText = findViewById(R.id.signin_signUpNavText);
    }

    @Override
    protected void setEvents() {
        signInButton.setOnClickListener(v -> {
            signInController.signIn(
                    Utils.getStringFromInputEditText(emailInputText),
                    Utils.getStringFromInputEditText(passwordInputText),
                    rememberMe.isChecked()
            );
        });

        signUpNavText.setOnClickListener(v -> {
            signInController.navigateToSignUpPage();
        });
    }
}