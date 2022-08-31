package com.cubbysulotions.proo.ModelsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class HourRVAdapter extends RecyclerView.Adapter<HourRVAdapter.ViewHolder> {

    private List<CalendarEvents> list;
    private Context context;

    //private final OnItemListener onItemListener;

    public HourRVAdapter(Context context, List<CalendarEvents> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTxt;

        public ViewHolder(final View itemView){
            super(itemView);
            eventTxt = itemView.findViewById(R.id.eventCell);
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
    public void onBindViewHolder(@NonNull HourRVAdapter.ViewHolder holder, int position) {
        CalendarEvents events = list.get(position);
        String title = CalendarUtils.formattedTime(LocalTime.parse(events.getTimeString())) + "\t" + events.getName();
        holder.eventTxt.setText(title);
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
