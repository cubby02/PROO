package com.cubbysulotions.proo.Journal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.R;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

        private List<Journal> list;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


        public JournalAdapter(List<Journal> list) {
            this.list = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView content, time, date;
            public ImageButton like;
            public ViewHolder(final View itemView){
                super(itemView);
                content = itemView.findViewById(R.id.contentTxtView);
                time = itemView.findViewById(R.id.timeTxt);
                date = itemView.findViewById(R.id.dateTxt);
                like = itemView.findViewById(R.id.likeBtn);

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
            ViewHolder vh = null;
            try {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View itemView = inflater.inflate(R.layout.timeline_journal_item, parent, false);
                vh = new ViewHolder(itemView);
            } catch (Exception e){
                Log.e("JournalError", "Message: ", e);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Journal journal = list.get(position);
            holder.content.setText(journal.getTitle());
            holder.time.setText(CalendarUtils.formattedTime(LocalTime.parse(journal.getTime())));
            holder.date.setText(CalendarUtils.formattedShortDate(LocalDate.parse(journal.getDate())));
        }

    public void updateDataSet(List<Journal> newResult){
        if(newResult!=null){
            list = newResult;
        }
        notifyDataSetChanged();
    }
    public void clear() {
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}
