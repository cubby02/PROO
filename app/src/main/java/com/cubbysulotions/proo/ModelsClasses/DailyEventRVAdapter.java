package com.cubbysulotions.proo.ModelsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;

import java.util.List;

public class DailyEventRVAdapter extends RecyclerView.Adapter<DailyEventRVAdapter.ViewHolder> {

        private List<DailyEvent> list;


        public DailyEventRVAdapter(List<DailyEvent> list) {
            this.list = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView eventTxt;
            public ViewHolder(final View itemView){
                super(itemView);
                eventTxt = itemView.findViewById(R.id.dailyEventCell);
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
            View itemView = inflater.inflate(R.layout.daily_event_cell, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DailyEventRVAdapter.ViewHolder holder, int position) {
            DailyEvent event = list.get(position);
            holder.eventTxt.setText(event.getName());
        }

    public void updateDataSet(List<DailyEvent> newResult){
        if(newResult!=null){
            list = newResult;
        }
        notifyDataSetChanged();
    }
}
