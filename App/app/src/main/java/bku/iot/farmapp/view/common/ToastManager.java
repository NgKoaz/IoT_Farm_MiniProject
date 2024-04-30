package bku.iot.farmapp.view.common;

import android.content.Context;
import android.widget.Toast;

import bku.iot.farmapp.R;
import io.github.muddz.styleabletoast.StyleableToast;

public class ToastManager {
    public static void showToast(Context context, String message){
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.myToast).show();
    }
}
