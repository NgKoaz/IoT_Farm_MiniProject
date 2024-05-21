package bku.iot.farmapp.view.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import bku.iot.farmapp.R;
import bku.iot.farmapp.view.widgets.dialog.LoadingPage;
import io.github.muddz.styleabletoast.StyleableToast;

public class MyActivity extends AppCompatActivity {
    private LoadingPage loadingPage;

    @Override
    protected void onStart() {
        super.onStart();
        initViews();
        setEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBindViews();
    }

    protected void initViews() {
        new NetworkNotify(this, this);
        loadingPage = new LoadingPage(this);
    }

    protected void setEvents() {

    }

    protected void onBindViews() {

    }

    public void showLoading(){
        loadingPage.show();
    }

    public void dismissLoading(){
        loadingPage.dismiss();
    }

    public void showToast(String message) {
        StyleableToast.makeText(this, message, Toast.LENGTH_LONG, R.style.myToast).show();
    }

    public void startNewActivity(Class<?> newActivityClass, @javax.annotation.Nullable Bundle extras) {
        Intent intent = new Intent(this, newActivityClass);
        if (extras != null) {
            intent.putExtras(extras);
            this.startActivity(intent, extras);
        } else {
            this.startActivity(intent);
        }
    }

    public void startNewActivtyForResult(
            Class<?> newActivityClass,
            ActivityResultLauncher<Intent> activityResultLauncher)
    {
        Intent intent = new Intent(this, newActivityClass);
        activityResultLauncher.launch(intent);
    }


}
