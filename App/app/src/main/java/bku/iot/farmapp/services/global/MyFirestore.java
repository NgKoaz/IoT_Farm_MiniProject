package bku.iot.farmapp.services.global;

import android.util.Log;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;



public class MyFirestore {
    public static MyFirestore instance;
    private final static String TAG = MyFirestore.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public MyFirestore() {

    }

    public static MyFirestore gI() {
        if (instance == null) {
            instance = new MyFirestore();
        }
        return instance;
    }

    public void getScheduleList(FirebaseUser user, String broker, String username, String password, OnCompleteListener<QuerySnapshot> listener){
        if (user == null) {
            Log.e(TAG, "ERROR: `user` is null! Please re-auth!");
            return;
        }
        CollectionReference brokersCollectionRef =
                db.collection("brokers")
                        .document(broker + "-" + username + "-" + password)
                        .collection("schedule");
        brokersCollectionRef.get().addOnCompleteListener(listener);
    }

    public void getHistoryScheduleList(FirebaseUser user, String broker, String username, String password, OnCompleteListener<QuerySnapshot> listener){
        if (user == null) {
            Log.e(TAG, "ERROR: `user` is null! Please re-auth!");
            return;
        }
        CollectionReference brokersCollectionRef =
                db.collection("brokers")
                        .document(broker + "-" + username + "-" + password)
                        .collection("history");
        brokersCollectionRef.get().addOnCompleteListener(listener);
    }

    public void setBrokerServerInfo(FirebaseUser user, String broker, String username, String password) {
        if (user == null) {
            Log.e(TAG, "Error: `user` is null!");
            return;
        }

        String userId = user.getUid();
        CollectionReference brokersCollectionRef = db.collection("users").document(userId)
                .collection("brokers");

        // Construct a query to check if the broker info already exists
        Query query = brokersCollectionRef.whereEqualTo("broker", broker)
                .whereEqualTo("username", username);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Log.d(TAG, "Broker info already exists for this user");
                } else {
                    // Save the broker info since it doesn't exist yet.
                    DocumentReference newDocumentRef = brokersCollectionRef.document();
                    Map<String, Object> info = new HashMap<>();
                    info.put("broker", broker);
                    info.put("username", username);
                    info.put("password", password);
                    newDocumentRef.set(info)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Broker info saved successfully!!"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error saving broker info: " + e.getMessage()));
                }
            } else {
                Log.e(TAG, "Error checking for existing broker info: ", task.getException());
            }
        });
    }

    public void setProfile(FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: `user` is null!");
            return;
        }
//        String userId = user.getUid();
//        DocumentReference documentReference = db.collection("users").document(userId);
//        Map<String, Object> info = new HashMap<>();
//        info.put("brokerDefault", "");
//        documentReference.set(info);
    }

//    public void getFeeds(@NonNull OnGetFeedsComplete listener){
//        if (publishList != null && subscribeList != null){
//            listener.onComplete(subscribeList);
//            return;
//        }
//
//        DocumentReference documentReference = db.collection("feeds").document("feeds");
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()){
//                        publishList = (ArrayList<String>) document.getData().get("publish");
//                        subscribeList = (ArrayList<String>) document.getData().get("subscribe");
//                        listener.onComplete(subscribeList);
//                    }
//                } else {
//                    // Query failed, handle the error
//                    Exception exception = task.getException();
//                    if (exception != null) {
//                        Log.e(TAG, "Error getting feeds: " + exception.getMessage());
//                    }
//                }
//            }
//        });
//    }

//    public void getBrokerServers(FirebaseUser user, @NonNull OnGetBrokersComplete listener) {
//        // Already gotten
//        if (brokerList != null) return;
//        if (user == null) {
//            Log.e(TAG, "Error: `user` is null!");
//            return;
//        }
//        String userId = user.getUid();
//        CollectionReference collectionReference = db.collection("users")
//                .document(userId).collection("brokers");
//
//        collectionReference.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot == null || querySnapshot.isEmpty()) {
//                    Log.d(TAG, "Query Snapshot is null!");
//                    return;
//                }
//
//                brokerList = new ArrayList<>();
//                for (QueryDocumentSnapshot document : querySnapshot) {
//                    // Extract data from each document
//                    try {
//                        BrokerInfo info = new BrokerInfo(
//                                document.get("broker").toString(),
//                                document.get("username").toString(),
//                                document.get("password").toString()
//                        );
//                        brokerList.add(info);
//                    } catch (NullPointerException npe){
//                        npe.printStackTrace();
//                    }
//                }
//                listener.onComplete(brokerList);
//            } else {
//                // Query failed, handle the error
//                Exception exception = task.getException();
//                if (exception != null) {
//                    Log.e(TAG, "Error getting broker list: " + exception.getMessage());
//                }
//            }
//        });
//    }

//    public void getStatisticalData(@NonNull BrokerInfo brokerInfo, StatisticalDataType dataType, OnGetDataComplete listener){
//        if (brokerInfo.getBroker().isEmpty() || brokerInfo.getUsername().isEmpty()){
//            Log.e(TAG, "BrokerServer or Username is empty!!");
//            return;
//        }
//
//        // Eleminate "ssl://" or "tcp://"
//        String[] parts = brokerInfo.getBroker().split("/");
//        String brokerServer = parts[parts.length - 1];
//
//        String brokerId = brokerServer + "-" + brokerInfo.getUsername() + "-" + brokerInfo.getPassword();
//        CollectionReference colRef;
//        switch (dataType) {
//            case TEMP:
//                colRef = db.collection("brokers").document(brokerId).collection("temp");
//                break;
//            case HUMI:
//                colRef = db.collection("brokers").document(brokerId).collection("humi");
//                break;
//            case LIGHT:
//                colRef = db.collection("brokers").document(brokerId).collection("light");
//                break;
//            case LOG:
//                colRef = db.collection("brokers").document(brokerId).collection("log");
//                break;
//            default:
//                Log.e(TAG, "Wrong DataType!");
//                return;
//        }
//
//        colRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot == null || querySnapshot.isEmpty()) {
//                    Log.d(TAG, "Query Snapshot is null!");
//                    return;
//                }
//
//                listener.onComplete(querySnapshot);
//            } else {
//                // Query failed, handle the error
//                Exception exception = task.getException();
//                if (exception != null) {
//                    Log.e(TAG, "Error getting data: " + exception.getMessage());
//                }
//            }
//        });
//    }

//    public interface OnGetDataComplete{
//        void onComplete(QuerySnapshot querySnapshot);
//    }
//
//    public interface OnGetFeedsComplete{
//        void onComplete(ArrayList<String> subscribe);
//    }
//
//    public interface OnGetBrokersComplete{
//        void onComplete(ArrayList<BrokerInfo> brokerList);
//    }
}