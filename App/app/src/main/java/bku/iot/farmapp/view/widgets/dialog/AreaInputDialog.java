package bku.iot.farmapp.view.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;

import bku.iot.farmapp.R;
import bku.iot.farmapp.view.common.Utils;


public class AreaInputDialog extends MyDialog {
    private TextInputEditText area1Input, area2Input, area3Input;
    private Button closeButton, okButton;
    private View.OnClickListener okListener;

    public AreaInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_area_input);
    }

    @Override
    protected void initViews(){
        area1Input = findViewById(R.id.area_input_dialog_area1);
        area2Input = findViewById(R.id.area_input_dialog_area2);
        area3Input = findViewById(R.id.area_input_dialog_area3);
        okButton = findViewById(R.id.area_input_dialog_okButton);
        closeButton = findViewById(R.id.area_input_dialog_closeButton);
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

    public String getArea1() {
        return Utils.getStringFromInputEditText(area1Input);
    }

    public String getArea2() {
        return Utils.getStringFromInputEditText(area2Input);
    }

    public String getArea3() {
        return Utils.getStringFromInputEditText(area3Input);
    }

}
