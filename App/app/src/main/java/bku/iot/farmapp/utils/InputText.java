package bku.iot.farmapp.utils;

import android.text.Editable;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class InputText {
    public static String getStringFromInputEditText(EditText input) {
        Editable editable = input.getText();
        if (editable != null) return input.getText().toString();
        return "";
    }
}
