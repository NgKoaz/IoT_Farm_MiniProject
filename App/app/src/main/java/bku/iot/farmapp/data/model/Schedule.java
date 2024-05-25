package bku.iot.farmapp.data.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule {
    public String scheduleId;
    public String email;
    public String type;
    public String _name;
    public int volume;
    public List<Integer> ratio;
    public String date;
    public String weekday;
    public String _time;
    public String error;

    public Schedule() {}

    public Schedule(String email, String type, String _name, int volume, List<Integer> ratio, String date, String weekday, String _time) {
        this.email = email;
        this.type = type;
        this._name = _name;
        this.volume = volume;
        this.ratio = ratio;
        this.date = date;
        this.weekday = weekday;
        this._time = _time;
    }

    public Schedule(String scheduleId, String email, String type, String _name, int volume, List<Integer> ratio, String date, String weekday, String _time, String error) {
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
                String[] parts = value.substring(1, value.length() - 1).split(", ");
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
                weekday = value;
                break;
            case "time":
                _time = value;
                break;
            case "error":
                error = value;
                break;
            default:
                Log.e("Schedule Model", "WRONG TYPE NAME!");
        }
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
            jsonObject.put("error", error);
        } catch (JSONException e) {
            throw new JSONException(e.getMessage());
        }
        return jsonObject.toString();
    }

}
