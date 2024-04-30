package bku.iot.farmapp.view.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import bku.iot.farmapp.R;
import bku.iot.farmapp.view.common.InputText;
import bku.iot.farmapp.view.pages.viewInterface.InitActivity;

public class ChangePasswordDialog extends Dialog implements InitActivity {

    private TextInputEditText oldPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button cancelButton, okButton;
    private View.OnClickListener cancelButtonListener, okButtonListener;

    public ChangePasswordDialog(
            @NonNull Context context,
            View.OnClickListener cancelButtonListener,
            View.OnClickListener okButtonListener)
    {
        super(context);
        this.cancelButtonListener = cancelButtonListener;
        this.okButtonListener = okButtonListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password_dialog);

        setCanceledOnTouchOutside(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initViews();
        bindEvents();
        onBindView();
    }

    @Override
    public void initViews(){
        oldPasswordInput = findViewById(R.id.change_password_dialog_oldPasswordInput);
        newPasswordInput = findViewById(R.id.change_password_dialog_newPasswordInput);
        confirmPasswordInput = findViewById(R.id.change_password_dialog_confirmPasswordInput);
        cancelButton = findViewById(R.id.change_password_dialog_cancelButton);
        okButton = findViewById(R.id.change_password_dialog_okButton);
    }

    @Override
    public void bindEvents() {
        cancelButton.setOnClickListener(cancelButtonListener);
        okButton.setOnClickListener(okButtonListener);
    }

    @Override
    public void onBindView() {

    }

    public String getOldPassword(){
        return InputText.getStringFromInputEditText(oldPasswordInput);
    }

    public String getNewPassword(){
        return InputText.getStringFromInputEditText(newPasswordInput);
    }

    public String getConfirmPassword(){
        return InputText.getStringFromInputEditText(confirmPasswordInput);
    }
}
