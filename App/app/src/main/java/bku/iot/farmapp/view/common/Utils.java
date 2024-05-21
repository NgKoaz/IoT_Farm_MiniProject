package bku.iot.farmapp.view.common;

import android.text.Editable;
import android.widget.EditText;

public class Utils {
    public static String getStringFromInputEditText(EditText input) {
        Editable editable = input.getText();
        return (editable != null) ?  editable.toString() : "";
    }
}
