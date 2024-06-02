package bku.iot.farmapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HistorySchedule implements Parcelable {
    private String name;
    private int volume;
    private List<Integer> ratio;
    private String startTime;
    private String endTime;

    public HistorySchedule(String name, String volume, String ratio, String startTime, String endTime) {
        this.name = name;
        this.volume = Integer.parseInt(volume);

        String[] parts = ratio.substring(1, ratio.length() - 1).replace(" ", "").split(",");
        this.ratio = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                this.ratio.add(Integer.parseInt(part));
            }
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }


    protected HistorySchedule(Parcel in) {
        name = in.readString();
        volume = in.readInt();
        ratio = new ArrayList<>();
        in.readList(ratio, Integer.class.getClassLoader());
        startTime = in.readString();
        endTime = in.readString();
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public List<Integer> getRatio() {
        return ratio;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public static final Creator<HistorySchedule> CREATOR = new Creator<HistorySchedule>() {
        @Override
        public HistorySchedule createFromParcel(Parcel in) {
            return new HistorySchedule(in);
        }

        @Override
        public HistorySchedule[] newArray(int size) {
            return new HistorySchedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(volume);
        dest.writeList(ratio);
        dest.writeString(startTime);
        dest.writeString(endTime);
    }
}
