package bku.iot.farmapp.view.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class MyDialog extends Dialog {

    public MyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setCanceledOnTouchOutside(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initViews();
        setEvents();
    }

    protected void initViews() {

    }

    protected void setEvents() {

    }
}
