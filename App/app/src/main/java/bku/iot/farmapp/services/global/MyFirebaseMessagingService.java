package bku.iot.farmapp.services.global;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import bku.iot.farmapp.MainActivity;
import bku.iot.farmapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private final String CHANNEL_ID = "FarmAppIoT_K21_HCMUT";
    private final String CHANNEL_NAME = "bku.iot.farmapp";

    @Override
    public void onNewToken(@NonNull String token)
    {
        Log.e(TAG, "Refreshed token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("notify").addOnCompleteListener(task -> {
            String msg = "Subscribed to news";
            if (!task.isSuccessful()) {
                msg = "Subscription failed";
            }
            Log.d(TAG, msg);
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        RemoteMessage.Notification notification = message.getNotification();
        if (notification == null) return;

        String title = notification.getTitle();
        String body = notification.getBody();

        showNotification(this, title, body);
    }

    public void showNotification(Context context, String title, String body) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_24px)
                        .setAutoCancel(true)
                        .setVibrate(new long[] {1000, 1000, 1000})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body));

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(2207, builder.build());
    }
}
