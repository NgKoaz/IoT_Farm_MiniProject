package bku.iot.farmapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class ScheduleInfo implements Parcelable {
    public String email;
    public String type;
    public String name;
    public double water;
    public double mixer1, mixer2, mixer3;
    public double area1, area2, area3;
    public int isDate;
    public String date;
    public String weekday;
    public String time;
    public int isError;
    public String error;


    protected ScheduleInfo(Parcel in) {
        email = in.readString();
        type = in.readString();
        name = in.readString();
        water = in.readDouble();
        mixer1 = in.readDouble();
        mixer2 = in.readDouble();
        mixer3 = in.readDouble();
        area1 = in.readDouble();
        area2 = in.readDouble();
        area3 = in.readDouble();
        isDate = in.readInt();
        date = in.readString();
        weekday = in.readString();
        time = in.readString();
        isError = in.readInt();
        error = in.readString();
    }

    public ScheduleInfo(JSONObject jsonObject) throws JSONException {
        try {
            // Retrieving all key-value pairs using keys()
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                assignAttribute(key, value);
            }
        } catch (JSONException e){
            e.printStackTrace();
            throw new JSONException(e.getMessage());
        }
    }

    private void assignAttribute(String key, Object value){
        switch (key){
            case "email":
                email = (String) value;
                break;
            case "type":
                type = (String) value;
                break;
            case "name":
                name = (String) value;
                break;
            case "water":
                water = Double.parseDouble(value.toString());
                break;
            case "mixer1":
                mixer1 = Double.parseDouble(value.toString());
                break;
            case "mixer2":
                mixer2 = Double.parseDouble(value.toString());
                break;
            case "mixer3":
                mixer3 = Double.parseDouble(value.toString());
                break;
            case "area1":
                area1 = Double.parseDouble(value.toString());
                break;
            case "area2":
                area2 = Double.parseDouble(value.toString());
                break;
            case "area3":
                area3 = Double.parseDouble(value.toString());
                break;
            case "isDate":
                isDate = (int) value;
                break;
            case "date":
                date = (String) value;
                break;
            case "weekday":
                weekday = (String) value;
                break;
            case "time":
                time = (String) value;
                break;
            case "isError":
                isError = (int) value;
                break;
            case "error":
                error = (String) value;
                break;
            default:
        }
    }

    public static final Creator<ScheduleInfo> CREATOR = new Creator<ScheduleInfo>() {
        @Override
        public ScheduleInfo createFromParcel(Parcel in) {
            return new ScheduleInfo(in);
        }

        @Override
        public ScheduleInfo[] newArray(int size) {
            return new ScheduleInfo[size];
        }
    };

    public ScheduleInfo(String email, String type, String name, double water,
                        double mixer1, double mixer2, double mixer3,
                        double area1, double area2, double area3,
                        int isDate, String date, String weekday, String time) {
        this.email = email;
        this.type = type;
        this.name = name;
        this.water = water;
        this.mixer1 = mixer1;
        this.mixer2 = mixer2;
        this.mixer3 = mixer3;
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
        this.isDate = isDate;
        this.date = date;
        this.weekday = weekday;
        this.time = time;
        this.isError = 0;
        this.error = "";
    }

    public String toJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("type", type);
            jsonObject.put("name", name);
            jsonObject.put("water", water);
            jsonObject.put("mixer1", mixer1);
            jsonObject.put("mixer2", mixer2);
            jsonObject.put("mixer3", mixer3);
            jsonObject.put("area1", area1);
            jsonObject.put("area2", area2);
            jsonObject.put("area3", area3);
            jsonObject.put("isDate", isDate);
            jsonObject.put("date", date);
            jsonObject.put("weekday", weekday);
            jsonObject.put("time", time);
            jsonObject.put("isError", isError);
            jsonObject.put("error", error);
        } catch (JSONException e) {
            throw new JSONException(e.getMessage());
        }
        return jsonObject.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeDouble(water);
        dest.writeDouble(mixer1);
        dest.writeDouble(mixer2);
        dest.writeDouble(mixer3);
        dest.writeDouble(area1);
        dest.writeDouble(area2);
        dest.writeDouble(area3);
        dest.writeInt(isDate);
        dest.writeString(date);
        dest.writeString(weekday);
        dest.writeString(time);
        dest.writeInt(isError);
        dest.writeString(error);
    }
}
