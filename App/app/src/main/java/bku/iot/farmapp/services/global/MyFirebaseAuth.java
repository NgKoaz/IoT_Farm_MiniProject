package bku.iot.farmapp.services.global;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class MyFirebaseAuth {

    private final String TAG = "_MyFirebaseAuth";
    private static MyFirebaseAuth instance;
    private final FirebaseAuth mAuth;

    public static synchronized MyFirebaseAuth gI(){
        if (instance == null){
            instance = new MyFirebaseAuth();
        }
        return instance;
    }

    public MyFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public void signUp(String email, String password, AuthListener listener){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = getCurrentUser();
                            listener.onAuthSuccess(user);
                        } else {
                            listener.onAuthFailure(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    public void signIn(String email, String password, AuthListener listener){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                listener.onAuthSuccess(user);
            } else {
                listener.onAuthFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void changePassword(String newPassword, OnChangePasswordCompleteListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task ->
                        listener.onComplete(task.isSuccessful())
                    );
        }
    }

    public void signOut(){
        if (mAuth.getCurrentUser() == null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public interface OnChangePasswordCompleteListener {
        void onComplete(boolean isSuccessful);
    }

    public interface AuthListener{
        void onAuthSuccess(FirebaseUser user);
        void onAuthFailure(String errorMessage);
    }
}
