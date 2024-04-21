package bku.iot.farmapp.utils;

import android.text.Editable;

import com.google.android.material.textfield.TextInputEditText;

public class InputText {
    public static String getStringFromInputEditText(TextInputEditText input) {
        Editable editable = input.getText();
        if (editable != null) return input.getText().toString();
        return "";
    }
}
