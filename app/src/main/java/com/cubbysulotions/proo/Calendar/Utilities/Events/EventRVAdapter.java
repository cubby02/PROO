package com.cubbysulotions.proo.Calendar.Utilities.Events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventRVAdapter extends RecyclerView.Adapter<EventRVAdapter.ViewHolder> {

    private List<CalendarEvents> list;
    private Context context;

    //private final OnItemListener onItemListener;

    public EventRVAdapter(Context context, List<CalendarEvents> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeText;
        public TextView dateText;
        public TextView eventNameText;
        public ViewHolder(final View itemView){
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
            dateText = itemView.findViewById(R.id.dateText);
            eventNameText = itemView.findViewById(R.id.eventNameText);
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
        View itemView = inflater.inflate(R.layout.event_cell, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRVAdapter.ViewHolder holder, int position) {
        CalendarEvents events = list.get(position);

        holder.timeText.setText(CalendarUtils.formattedTime(LocalTime.parse(events.getTimeString())));
        holder.dateText.setText(CalendarUtils.formattedShortDate(LocalDate.parse(events.getDateString())));
        holder.eventNameText.setText(events.getName());
    }

    public void updateDataSet(List<CalendarEvents> newResult){
        if(newResult!=null){
            list = newResult;
        }
        notifyDataSetChanged();
    }

    public interface OnItemListener{
        void onItemClick(int position, LocalDate date);
    }




}
