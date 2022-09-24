package com.cubbysulotions.proo.Chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatModel> chatModels;
    private Context context;

    public ChatRVAdapter(ArrayList<ChatModel> chatModels, Context context) {
        this.chatModels = chatModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_item, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ai_msg_item, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatModels.get(position);
        switch (chatModel.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder)holder).botTV.setText(chatModel.getMessage());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatModels.get(position).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userTV;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.userMsg);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder{
        TextView botTV;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botTV = itemView.findViewById(R.id.aiMsg);
        }
    }


}
