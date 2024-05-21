package bku.iot.farmapp.view.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import bku.iot.farmapp.R;
import bku.iot.farmapp.view.common.Utils;


public class MixerInputDialog extends MyDialog {
    private TextInputEditText waterInput, mixer1Input, mixer2Input, mixer3Input;
    private Button closeButton, okButton;
    private View.OnClickListener okListener;

    public MixerInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mixer_input);
    }

    @Override
    protected void initViews(){
        waterInput = findViewById(R.id.schedule_info_input_dialog_water);
        mixer1Input = findViewById(R.id.schedule_info_input_dialog_mixer1);
        mixer2Input = findViewById(R.id.schedule_info_input_dialog_mixer2);
        mixer3Input = findViewById(R.id.schedule_info_input_dialog_mixer3);
        closeButton = findViewById(R.id.schedule_info_input_dialog_closeButton);
        okButton = findViewById(R.id.schedule_info_input_dialog_okButton);
    }

    @Override
    protected void setEvents() {
        closeButton.setOnClickListener(v -> {
            dismiss();
        });
        if (okListener != null) {
            okButton.setOnClickListener(okListener);
        }
    }

    public void setOkButton(View.OnClickListener okListener) {
        this.okListener = okListener;
    }

    public String getWater() {
        return Utils.getStringFromInputEditText(waterInput);
    }

    public String getMixer1() {
        return Utils.getStringFromInputEditText(mixer1Input);
    }

    public String getMixer2() {
        return Utils.getStringFromInputEditText(mixer2Input);
    }

    public String getMixer3() {
        return Utils.getStringFromInputEditText(mixer3Input);
    }

}
