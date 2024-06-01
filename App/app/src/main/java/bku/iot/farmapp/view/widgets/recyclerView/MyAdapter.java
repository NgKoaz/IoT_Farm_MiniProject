package bku.iot.farmapp.view.widgets.recyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import bku.iot.farmapp.R;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.view.common.MyActivity;
import bku.iot.farmapp.view.pages.ScheduleActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<Schedule> dataList;
    private final MyActivity myActivity;
    private SwitchListener switchListener;

    public MyAdapter(MyActivity myActivity, List<Schedule> dataList, SwitchListener switchListener) {
        this.myActivity = myActivity;
        this.dataList = dataList;
        this.switchListener = switchListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myActivity).inflate(R.layout.schedule_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position % 2 == 1)
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_odd);
        else
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_even);

        Schedule schedule = dataList.get(position);

        holder.bind(schedule);

        holder.root.setOnClickListener(v -> {
            Bundle extras = new Bundle();
            extras.putString("page", "EDIT");
            extras.putParcelable("schedule", schedule);
            myActivity.startNewActivity(ScheduleActivity.class, extras);
        });

        holder.aSwitch.setOnClickListener(view -> {
            switchListener.onClicked(view, schedule.isOn != 1 , schedule, position);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView nameText, timeText, dayText;
        private final Switch aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            nameText = itemView.findViewById(R.id.schedule_info_nameField);
            timeText = itemView.findViewById(R.id.schedule_info_time);
            dayText = itemView.findViewById(R.id.schedule_info_day);
            aSwitch = itemView.findViewById(R.id.schedule_info_switch);
        }

        public void bind(Schedule schedule) {
            nameText.setText(schedule._name);
            timeText.setText(schedule._time);
            if (schedule.weekday != null && schedule.weekday.size() > 1){
                if (schedule.weekday.size() == 7) {
                    dayText.setText("Everyday");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(getWeekday(schedule.weekday.get(0)));
                for (int i = 1; i < schedule.weekday.size(); i++) {
                    sb.append(", ");
                    sb.append(getWeekday(i));
                }
                dayText.setText(sb.toString());
            } else {
                dayText.setText(schedule.date);
            }

            aSwitch.setChecked(schedule.isOn == 1);
        }

        private String getWeekday(Integer wd){
            String text;
            switch (wd){
                case 0:
                    text = "Mon";
                    break;
                case 1:
                    text = "Tue";
                    break;
                case 2:
                    text = "Wed";
                    break;
                case 3:
                    text = "Thur";
                    break;
                case 4:
                    text = "Fri";
                    break;
                case 5:
                    text = "Sat";
                    break;
                case 6:
                    text = "Sun";
                    break;
                default:
                    text = "";
            }
            return text;
        }
    }

    public interface SwitchListener {
        void onClicked(View buttonView, boolean isCheck, Schedule schedule, int position);
    }

}