package bku.iot.farmapp.view.common;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;


import bku.iot.farmapp.services.local.NetworkManager;
import bku.iot.farmapp.view.widgets.dialog.NotifyDialog;

public class NetworkNotify {
    private final NetworkManager networkManager;
    private final NotifyDialog notifyDialog;

    
    public NetworkNotify(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner){
        networkManager = new NetworkManager(context);
        notifyDialog = new NotifyDialog(context);
        notifyDialog.setTitleText("Network Error");
        notifyDialog.setDescriptionText("No connection!");

        networkManager.observe(lifecycleOwner, aBoolean -> {
            if (aBoolean) {
                notifyDialog.dismiss();
            } else {
                notifyDialog.show();
            }
        });
    }
}
