package bku.iot.farmapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ScheduleInfo implements Parcelable {
    public String email;
    public String type;
    public String name;
    public double water;
    public double mixer1, mixer2, mixer3;
    public double area1, area2, area3;
    public byte isDate;
    public String date;
    public String weekday;
    public String time;



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
        isDate = in.readByte();
        date = in.readString();
        weekday = in.readString();
        time = in.readString();
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
                        byte isDate, String date, String weekday, String time) {
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
        dest.writeByte(isDate);
        dest.writeString(date);
        dest.writeString(weekday);
        dest.writeString(time);
    }
}
