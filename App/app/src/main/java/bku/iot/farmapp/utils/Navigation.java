package bku.iot.farmapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Navigation {
    public static void startNewActivity(Context context, Class<?> newActivityClass, Bundle extras){
        Intent intent = new Intent(context, newActivityClass);
        if (extras != null) intent.putExtras(extras);
        context.startActivity(intent);
    }
}
