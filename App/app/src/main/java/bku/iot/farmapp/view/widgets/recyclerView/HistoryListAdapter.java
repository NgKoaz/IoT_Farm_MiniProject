package bku.iot.farmapp.view.widgets.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import bku.iot.farmapp.R;
import bku.iot.farmapp.data.model.HistorySchedule;
import bku.iot.farmapp.data.model.Schedule;
import bku.iot.farmapp.view.common.MyActivity;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private final MyActivity myActivity;
    private final List<HistorySchedule> historyScheduleList;

    public HistoryListAdapter(MyActivity myActivity, List<HistorySchedule> historyScheduleList) {
        this.myActivity = myActivity;
        this.historyScheduleList = historyScheduleList;
    }

    @NonNull
    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myActivity).inflate(R.layout.history_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListAdapter.ViewHolder holder, int position) {
        if (position % 2 == 1)
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_odd);
        else
            holder.root.setBackgroundResource(R.drawable.bg_schedule_info_even);

        HistorySchedule historySchedule = historyScheduleList.get(position);

        holder.bind(historySchedule);
    }

    @Override
    public int getItemCount() {
        return historyScheduleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private TextView name, startTime, endTime, volume, water, mixer1, mixer2, mixer3, area1, area2, area3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            name = itemView.findViewById(R.id.history_info_name);
            startTime = itemView.findViewById(R.id.history_info_startTime);
            endTime = itemView.findViewById(R.id.history_info_endTime);
            volume = itemView.findViewById(R.id.history_info_volume);
            water = itemView.findViewById(R.id.history_info_water);
            mixer1 = itemView.findViewById(R.id.history_info_mixer1);
            mixer2 = itemView.findViewById(R.id.history_info_mixer2);
            mixer3 = itemView.findViewById(R.id.history_info_mixer3);
            area1 = itemView.findViewById(R.id.history_info_area1);
            area2 = itemView.findViewById(R.id.history_info_area2);
            area3 = itemView.findViewById(R.id.history_info_area3);
        }

        private void bind(HistorySchedule historySchedule) {
            name.setText(historySchedule.getName());
            startTime.setText(historySchedule.getStartTime());
            endTime.setText(historySchedule.getEndTime());

            int vol = historySchedule.getVolume();
            volume.setText(String.format("%s ml", vol));

            List<Integer> ratio = historySchedule.getRatio();
            int wt = ratio.get(0);
            int m1 = ratio.get(1);
            int m2 = ratio.get(2);
            int m3 = ratio.get(3);
            int a1 = ratio.get(4);
            int a2 = ratio.get(5);
            int a3 = ratio.get(6);

            // Convert from ratio to milliliter.
            int wtMl = (vol / (m1 + m2 + m3 + wt)) * wt;
            int m1Ml = (vol / (m1 + m2 + m3 + wt)) * m1;
            int m2Ml = (vol / (m1 + m2 + m3 + wt)) * m2;
            int m3Ml = (vol / (m1 + m2 + m3 + wt)) * m3;

            int a1Ml = (vol / (a1 + a2 + a3) * a1);
            int a2Ml = (vol / (a1 + a2 + a3) * a2);
            int a3Ml = (vol / (a1 + a2 + a3) * a3);

            water.setText(String.format("%s (+ %s ml)", wt, wtMl));
            mixer1.setText(String.format("%s (+ %s ml)", m1, m1Ml));
            mixer2.setText(String.format("%s (+ %s ml)", m2, m2Ml));
            mixer3.setText(String.format("%s (+ %s ml)", m3, m3Ml));
            area1.setText(String.format("%s (- %s ml)", a1, a1Ml));
            area2.setText(String.format("%s (- %s ml)", a2, a2Ml));
            area3.setText(String.format("%s (- %s ml)", a3, a3Ml));
        }
    }
}
