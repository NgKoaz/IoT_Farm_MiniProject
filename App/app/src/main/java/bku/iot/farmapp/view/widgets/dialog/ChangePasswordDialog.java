package bku.iot.farmapp.view.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import bku.iot.farmapp.R;
import bku.iot.farmapp.view.common.Utils;


public class ChangePasswordDialog extends MyDialog {

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
    }

    @Override
    protected void initViews(){
        oldPasswordInput = findViewById(R.id.change_password_dialog_oldPasswordInput);
        newPasswordInput = findViewById(R.id.change_password_dialog_newPasswordInput);
        confirmPasswordInput = findViewById(R.id.change_password_dialog_confirmPasswordInput);
        cancelButton = findViewById(R.id.change_password_dialog_cancelButton);
        okButton = findViewById(R.id.change_password_dialog_okButton);
    }

    @Override
    protected void setEvents() {
        cancelButton.setOnClickListener(cancelButtonListener);
        okButton.setOnClickListener(okButtonListener);
    }

    public String getOldPassword(){
        return Utils.getStringFromInputEditText(oldPasswordInput);
    }

    public String getNewPassword(){
        return Utils.getStringFromInputEditText(newPasswordInput);
    }

    public String getConfirmPassword(){
        return Utils.getStringFromInputEditText(confirmPasswordInput);
    }
}
