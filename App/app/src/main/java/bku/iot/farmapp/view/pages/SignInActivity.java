package bku.iot.farmapp.view.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import bku.iot.farmapp.R;
import bku.iot.farmapp.controller.SignInController;
import bku.iot.farmapp.utils.InputText;
import bku.iot.farmapp.utils.Navigation;
import bku.iot.farmapp.view.pages.loadingPage.LoadingPage;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class SignInActivity extends AppCompatActivity implements InitActivity {
    private SignInController signInController;
    private TextInputEditText emailInputText, passwordInputText;
    private CheckBox rememberMe;
    private Button signInButton;
    private TextView signUpNavText;
    private LoadingPage loadingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signInController = new SignInController(this);
        setContentView(R.layout.activity_sign_in);

        initViews();
        bindEvents();
    }

    @Override
    public void initViews() {
        emailInputText = findViewById(R.id.signin_emailInputText);
        passwordInputText = findViewById(R.id.signin_passwordInputText);
        rememberMe = findViewById(R.id.signin_rememberMe);
        signInButton = findViewById(R.id.signin_signInButton);
        signUpNavText = findViewById(R.id.signin_signUpNavText);

        loadingPage = new LoadingPage(this);
    }

    @Override
    public void bindEvents() {
        signInButton.setOnClickListener(v -> {
            signInController.signIn(
                    InputText.getStringFromInputEditText(emailInputText),
                    InputText.getStringFromInputEditText(passwordInputText),
                    rememberMe.isChecked()
            );
        });

        signUpNavText.setOnClickListener(v -> {
            signInController.navigateToSignUpPage();
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