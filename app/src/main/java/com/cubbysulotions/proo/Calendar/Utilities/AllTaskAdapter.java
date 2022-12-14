package com.cubbysulotions.proo.Calendar.Utilities;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Journal.JournalAdapter;
import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AllTaskAdapter extends RecyclerView.Adapter<AllTaskAdapter.ViewHolder> {

        private List<DailyEvent> list;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public AllTaskAdapter(List<DailyEvent> list) {
            this.list = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView day, date, month, time, description;
            public ImageView options;
            public ViewHolder(final View itemView){
                super(itemView);
                title = itemView.findViewById(R.id.txtAgendaTitle);
                day = itemView.findViewById(R.id.day);
                date = itemView.findViewById(R.id.date);
                month = itemView.findViewById(R.id.month);
                time = itemView.findViewById(R.id.time);
                description = itemView.findViewById(R.id.description);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                mListener.onItemClick(position);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_task, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull AllTaskAdapter.ViewHolder holder, int position) {
            DailyEvent event = list.get(position);
            holder.title.setText(event.getName());
            String content = event.getContent();

            if(content.equals("")){
                holder.description.setText("No Content");
            } else {
                holder.description.setText(content);
            }

            holder.day.setText(CalendarUtils.formattedDayOfWeek(LocalDate.parse(event.getDateString())));
            holder.date.setText(CalendarUtils.formattedDayOnly(LocalDate.parse(event.getDateString())));
            holder.month.setText(CalendarUtils.formattedMonth(LocalDate.parse(event.getDateString())));
            holder.time.setText(CalendarUtils.formattedTime(LocalTime.parse(event.getTimeString())));


        }



    public void updateDataSet(List<DailyEvent> newResult){
        if(newResult!=null){
            list = newResult;
        }
        notifyDataSetChanged();
    }
}
