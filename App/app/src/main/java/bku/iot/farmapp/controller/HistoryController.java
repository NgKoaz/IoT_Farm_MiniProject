package bku.iot.farmapp.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import bku.iot.farmapp.data.enums.MqttTopic;
import bku.iot.farmapp.data.model.HistorySchedule;
import bku.iot.farmapp.services.global.MyFirebaseAuth;
import bku.iot.farmapp.services.global.MyFirestore;
import bku.iot.farmapp.services.global.MyMqttClient;
import bku.iot.farmapp.view.pages.HistoryActivity;

public class HistoryController {
    private final HistoryActivity historyActivity;
    private final String TAG = HistoryController.class.getSimpleName();
    private final List<HistorySchedule> historyScheduleList = new ArrayList<>();

    public HistoryController(HistoryActivity historyActivity){
        this.historyActivity = historyActivity;
    }

    public void loadHistoryView() {
        String[] parts = MyMqttClient.gI().broker.split("/");
        MyFirestore.gI().getHistoryScheduleList(
                MyFirebaseAuth.gI().getCurrentUser(),
                parts[parts.length - 1],
                MyMqttClient.gI().username,
                MyMqttClient.gI().password,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            for (DocumentSnapshot doc : query) {
                                HistorySchedule historySchedule = new HistorySchedule(
                                        doc.get("name").toString(),
                                        doc.get("volume").toString(),
                                        doc.get("ratio").toString(),
                                        doc.get("start_time").toString(),
                                        doc.get("end_time").toString()
                                );
                                historyScheduleList.add(0, historySchedule);
                            }
                            historyActivity.updateHistoryView(historyScheduleList);
                        } else {
                            Log.e(TAG, "Get history schedule error!");
                        }
                    }
                }
        );
    }



    public void backToPreviousActivity(){
        historyActivity.finish();
    }
}
