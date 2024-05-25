package bku.iot.farmapp.view.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import bku.iot.farmapp.R;


public class NotifyDialog extends MyDialog {
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
    }

    @Override
    protected void initViews(){
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

    protected void setEvents(){
        closeButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}
