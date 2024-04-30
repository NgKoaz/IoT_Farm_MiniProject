package bku.iot.farmapp.view.widgets.recyclerView;// MyAdapter.java

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import bku.iot.farmapp.R;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.ScheduleInfo;
import bku.iot.farmapp.view.common.Navigation;
import bku.iot.farmapp.view.pages.ScheduleActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<ScheduleInfo> dataList;
    private final Context context;
    private boolean isSetOnListener;

    public MyAdapter(Context context, List<ScheduleInfo> dataList, boolean isSetOnListener) {
        this.context = context;
        this.dataList = dataList;
        this.isSetOnListener = isSetOnListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position % 2 == 1)
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_odd);
        else
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_even);

        ScheduleInfo scheduleInfo = dataList.get(position);
        holder.bind(scheduleInfo);

        if (isSetOnListener)
            holder.root.setOnClickListener(v -> {
                Bundle extras = new Bundle();
                extras.putString("page", "EDIT");
                extras.putParcelable("scheduleInfo", scheduleInfo);
                Navigation.startNewActivity(context, ScheduleActivity.class, extras);
            });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView nameField, waterField, mixerField, areaField, timeStartField;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            nameField = itemView.findViewById(R.id.schedule_info_nameField);
            waterField = itemView.findViewById(R.id.schedule_info_waterField);
            mixerField = itemView.findViewById(R.id.schedule_info_mixerField);
            areaField = itemView.findViewById(R.id.schedule_info_areaField);
            timeStartField = itemView.findViewById(R.id.schedule_info_timeStartField);
        }

        public void bind(ScheduleInfo info) {
            nameField.setText(info.name);
            waterField.setText(String.valueOf(info.water));
            mixerField.setText(String.format("%s - %s - %s", info.mixer1, info.mixer2, info.mixer3));
            areaField.setText(String.format("%s - %s - %s", info.area1, info.area2, info.area3));

            if (info.isDate == 0){
                setWeekdays(info.time, info.weekday);
            } else {
                setDate(info.time, info.date);
            }
        }

        private void setDate(String time, String date){
            String text = time + " " + date;
            timeStartField.setText(text);
        }

        private void setWeekdays(String time, String weekday){
            String text = time + "Each ";
            switch (weekday){
                case Weekdays.MONDAY:
                    text += "Monday";
                    break;
                case Weekdays.TUESDAY:
                    text += "Tuesday";
                    break;
                case Weekdays.WEDNESDAY:
                    text += "Wednesday";
                    break;
                case Weekdays.THURSDAY:
                    text += "Thursday";
                    break;
                case Weekdays.FRIDAY:
                    text += "Friday";
                    break;
                case Weekdays.SATURDAY:
                    text += "Saturday";
                    break;
                case Weekdays.SUNDAY:
                    text += "Sunday";
                    break;
                default:
            }
            timeStartField.setText(text);
        }
    }
}