package bku.iot.farmapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schedule implements Parcelable {
    public String scheduleId = "";
    public String email = "";
    public String type = "";
    public String _name = "";
    public int volume;
    public List<Integer> ratio;
    public String date = "";
    public List<Integer> weekday;
    public String _time = "";
    public int isOn = 0;
    public String error = "";

    public Schedule() {}

    public Schedule(String email, String type, String _name, int volume, List<Integer> ratio, String date, List<Integer> weekday, String _time) {
        this.email = email;
        this.type = type;
        this._name = _name;
        this.volume = volume;
        this.ratio = ratio;
        this.date = date;
        this.weekday = weekday;
        this._time = _time;
    }

    public Schedule(String scheduleId, String email, String type, String _name, int volume, List<Integer> ratio, String date, List<Integer> weekday, String _time, String error) {
        this.scheduleId = scheduleId;
        this.email = email;
        this.type = type;
        this._name = _name;
        this.volume = volume;
        this.ratio = ratio;
        this.date = date;
        this.weekday = weekday;
        this._time = _time;
        this.error = error;
    }

    public void assignAttribute(String key, String value){
        switch (key){
            case "scheduleId":
                scheduleId = value;
                break;
            case "email":
                email = value;
                break;
            case "type":
                type = value;
                break;
            case "name":
                _name = value;
                break;
            case "volume":
                volume = Integer.parseInt(value);
                break;
            case "ratio":
                if (value.isEmpty()) return;
                String[] parts = value.substring(1, value.length() - 1).replace(" ", "").split(",");
                ratio = new ArrayList<>();
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        ratio.add(Integer.parseInt(part));
                    }
                }
                break;
            case "date":
                date = value;
                break;
            case "weekday":
                if (value.isEmpty()) return;
                String[] weekdays = value.substring(1, value.length() - 1).replace(" ", "").split(",");
                weekday = new ArrayList<>();
                for (String part : weekdays) {
                    if (!part.isEmpty()) {
                        weekday.add(Integer.parseInt(part));
                    }
                }
                break;
            case "time":
                _time = value;
                break;
            case "isOn":
                isOn = Integer.parseInt(value);
                break;
            case "error":
                error = value;
                break;
            default:
                Log.e("Schedule Model", "WRONG TYPE NAME!");
        }
    }

    public Schedule(JSONObject jsonObject) throws JSONException {
        try {
            // Retrieving all key-value pairs using keys()
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.get(key).toString();
                assignAttribute(key, value);
            }
        } catch (JSONException e){
            e.printStackTrace();
            throw new JSONException(e.getMessage());
        }
    }

    public void shallowCopy(Schedule schedule) {
        this.scheduleId = schedule.scheduleId;
        this.email = schedule.email;
        this.type = schedule.type;
        this._name = schedule._name;
        this.volume = schedule.volume;
        this.ratio = schedule.ratio;
        this.date = schedule.date;
        this.weekday = schedule.weekday;
        this._time = schedule._time;
        this.isOn = schedule.isOn;
        this.error = schedule.error;
    }

    public void setIsOn(int value) {
        isOn = value;
    }

    public String toJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("scheduleId", scheduleId);
            jsonObject.put("email", email);
            jsonObject.put("type", type);
            jsonObject.put("name", _name);
            jsonObject.put("volume", volume);
            jsonObject.put("ratio", (ratio == null) ? "[]" : ratio);
            jsonObject.put("date", date);
            jsonObject.put("weekday", weekday);
            jsonObject.put("time", _time);
            jsonObject.put("isOn", isOn);
            jsonObject.put("error", error);
        } catch (JSONException e) {
            throw new JSONException(e.getMessage());
        }
        return jsonObject.toString();
    }

    protected Schedule(Parcel in) {
        scheduleId = in.readString();
        email = in.readString();
        type = in.readString();
        _name = in.readString();
        volume = in.readInt();
        ratio = new ArrayList<>();
        in.readList(ratio, Integer.class.getClassLoader());
        date = in.readString();
        weekday = new ArrayList<>();
        in.readList(weekday, Integer.class.getClassLoader());
        _time = in.readString();
        isOn = in.readInt();
        error = in.readString();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(scheduleId);
        parcel.writeString(email);
        parcel.writeString(type);
        parcel.writeString(_name);
        parcel.writeInt(volume);
        parcel.writeList(ratio);
        parcel.writeString(date);
        parcel.writeList(weekday);
        parcel.writeString(_time);
        parcel.writeInt(isOn);
        parcel.writeString(error);
    }
}
