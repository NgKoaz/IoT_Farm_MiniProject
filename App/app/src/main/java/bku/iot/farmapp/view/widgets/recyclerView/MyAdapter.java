package bku.iot.farmapp.view.widgets.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import bku.iot.farmapp.R;
import bku.iot.farmapp.data.enums.Weekdays;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.view.common.MyActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<Schedule> dataList;
    private final MyActivity myActivity;
    private final boolean isSetOnListener;

    public MyAdapter(MyActivity myActivity, List<Schedule> dataList, boolean isSetOnListener) {
        this.myActivity = myActivity;
        this.dataList = dataList;
        this.isSetOnListener = isSetOnListener;
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

//        if (isSetOnListener)
//            holder.root.setOnClickListener(v -> {
//                Bundle extras = new Bundle();
//                extras.putString("page", "EDIT");
//                extras.putParcelable("scheduleInfo", scheduleInfo);
//                myActivity.startNewActivity(ScheduleActivity.class, extras);
//            });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView nameText, timeText, dayText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            nameText = itemView.findViewById(R.id.schedule_info_nameField);
            timeText = itemView.findViewById(R.id.schedule_info_time);
            dayText = itemView.findViewById(R.id.schedule_info_day);
        }

        public void bind(Schedule schedule) {
            nameText.setText(schedule._name);
            timeText.setText(schedule._time);
            if (!schedule.weekday.isEmpty()){
                setWeekdays(schedule.weekday);
            } else {
                dayText.setText(schedule.date);
            }
        }

        private void setWeekdays(String weekday){
            String text = "Each ";
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
            dayText.setText(text);
        }
    }
}