package bku.iot.farmapp.view.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import bku.iot.farmapp.R;


public class LoadingPage extends MyDialog {

    public LoadingPage(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);
    }
}
