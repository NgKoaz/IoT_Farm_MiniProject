package bku.iot.farmapp.view.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import bku.iot.farmapp.R;

public class NotifyDialog extends Dialog {
    private TextView titleText;
    private TextView descriptionText;
    private Button closeButton;
    private String title;
    private String description;

    public NotifyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_dialog);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        if (window != null){
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }

        initViews();
        setEvents();
    }

    private void initViews(){
        titleText = findViewById(R.id.schedule_input_dialog_title);
        descriptionText = findViewById(R.id.notify_dialog_description);
        closeButton = findViewById(R.id.schedule_info_input_dialog_closeButton);

        titleText.setText(title);
        descriptionText.setText(description);
    }

    public void setTitleText(String notifyText){
        title = notifyText;

    }

    public void setDescriptionText(String descriptionText){
        description = descriptionText;
    }

    private void setEvents(){
        closeButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}
