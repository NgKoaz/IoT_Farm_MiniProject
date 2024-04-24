package bku.iot.farmapp.services.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


// This is SingleTon class but you have to run constructor first before you use gI().
public class LocalStorage {
    private static final String TAG = LocalStorage.class.getSimpleName();
    private static final String PREF_NAME = "SecureStorage";
    private static final String SECRET_KEY = "rQQ2ZzvGS04q1gzs";
    private final Context context;
    private final SharedPreferences sharedPreferences;


    // You have to call this function first, before you use gI().
    public LocalStorage(@NonNull Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Store encrypted string
    public void putString(String key, String value) {
        Log.d("HELLO", "Key: " + key + "| Value: " + value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedValue = cipher.doFinal(value.getBytes());
            String encryptedBase64 = Base64.encodeToString(encryptedValue, Base64.DEFAULT);
            editor.putString(key, encryptedBase64);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Encryption error: " + e.getMessage());
        }
    }

    // Retrieve and decrypt string
    public String getString(String key) {
        String encryptedValueBase64 = sharedPreferences.getString(key, null);
        if (encryptedValueBase64 != null) {
            try {
                SecretKey secretKey = generateSecretKey();
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] encryptedValue = Base64.decode(encryptedValueBase64, Base64.DEFAULT);
                byte[] decryptedBytes = cipher.doFinal(encryptedValue);
                return new String(decryptedBytes);
            } catch (Exception e) {
                Log.e(TAG, "Decryption error: " + e.getMessage());
            }
        }
        return "";
    }

    public void clear(){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Generate secret key from the provided key
    private static SecretKey generateSecretKey() {
        byte[] keyData = SECRET_KEY.getBytes();
        return new SecretKeySpec(keyData, "AES");
    }
}
