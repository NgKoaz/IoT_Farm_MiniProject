package bku.iot.farmapp.view.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;

import javax.annotation.Nullable;


public class Navigation {
    public static void startNewActivity(Context context, Class<?> newActivityClass, @Nullable Bundle extras){
        Intent intent = new Intent(context, newActivityClass);
        if (extras != null) {
            intent.putExtras(extras);
            context.startActivity(intent, extras);
        } else {
            context.startActivity(intent);
        }
    }

    public static void startNewActivtyForResult(
            Context context,
            Class<?> newActivityClass,
            ActivityResultLauncher<Intent> activityResultLauncher)
    {
        Intent intent = new Intent(context, newActivityClass);
        activityResultLauncher.launch(intent);
    }
}
