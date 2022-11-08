package com.cubbysulotions.proo.Calendar.Utilities.Daily;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Journal.JournalAdapter;
import com.cubbysulotions.proo.R;

import java.util.List;

public class DailyEventRVAdapter extends RecyclerView.Adapter<DailyEventRVAdapter.ViewHolder> {

        private List<DailyEvent> list;

        private OnItemClickListener mListener;

        private String date;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }


        public DailyEventRVAdapter(List<DailyEvent> list, String date) {
            this.list = list;
            this.date = date;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView eventTxt;
            public ViewHolder(final View itemView){
                super(itemView);
                eventTxt = itemView.findViewById(R.id.dailyEventCell);
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
            View itemView = inflater.inflate(R.layout.daily_event_cell, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DailyEventRVAdapter.ViewHolder holder, int position) {
            DailyEvent event = list.get(position);
            holder.eventTxt.setText(event.getName());

            holder.eventTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavController navController = Navigation.findNavController(view);
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                            .setEnterAnim(R.anim.slide_in_up)
                            .setExitAnim(R.anim.wait_anim)
                            .setPopEnterAnim(R.anim.wait_anim)
                            .setPopExitAnim(R.anim.slide_in_down)
                            .build();

                    Bundle bundle = new Bundle();
                    bundle.putString("id", event.getId());
                    bundle.putString("name", event.getName());
                    bundle.putString("content", event.getContent());
                    bundle.putString("dateString", event.getDateString());
                    bundle.putString("timeString", event.getTimeString());
                    bundle.putString("notificationID", event.getNotificationID());
                    bundle.putString("requestCode", event.getRequestCode());

                    bundle.putString("date", date);

                    navController.navigate(R.id.action_weeklyCalendarFragment_to_viewEventFragment, bundle, navOptions);
                }
            });
        }

    public void updateDataSet(List<DailyEvent> newResult){
        if(newResult!=null){
            list = newResult;
        }
        notifyDataSetChanged();
    }
}
