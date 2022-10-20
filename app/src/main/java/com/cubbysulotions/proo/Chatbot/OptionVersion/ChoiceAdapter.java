package com.cubbysulotions.proo.Chatbot.OptionVersion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.util.List;

import hari.bounceview.BounceView;

public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ViewHolder> {

    private List<Choices> list;
    private OnItemClickListener mListener;


    public ChoiceAdapter(List<Choices> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView choice;
            public ViewHolder(final View itemView){
                super(itemView);
                choice = itemView.findViewById(R.id.choiceTxt);
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
            View itemView = inflater.inflate(R.layout.btn_choice_item, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Choices choices = list.get(position);
            BounceView.addAnimTo(holder.choice);
            holder.choice.setText(choices.getChoice());
            holder.choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    public void updateDataSet(List<Choices> newResult){
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
