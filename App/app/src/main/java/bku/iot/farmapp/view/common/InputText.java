package bku.iot.farmapp.view.common;

import android.text.Editable;
import android.widget.EditText;

public class InputText {
    public static String getStringFromInputEditText(EditText input) {
        Editable editable = input.getText();
        if (editable != null) return input.getText().toString();
        return "";
    }
}
