package com.cubbysulotions.proo.Chatbot;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

  private List<Message> messageList;
  private Activity activity;

  public ChatAdapter(List<Message> messageList, Activity activity) {
    this.messageList = messageList;
    this.activity = activity;
  }

  @NonNull @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(activity).inflate(R.layout.adapter_message_one, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    String message = messageList.get(position).getMessage();
    int code = messageList.get(position).getCode();

    switch (code){
      case 0:
        holder.aiMessage.setVisibility(View.VISIBLE);
        holder.userMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.messageReceive.setText(message);
        break;
      case 1:
        holder.userMessage.setVisibility(View.VISIBLE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.messageSend.setText(message);
        break;
      case 2:
        holder.userMessage.setVisibility(View.GONE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.VISIBLE);
        break;
    }



  }

  @Override public int getItemCount() {
    return messageList.size();
  }

  static class MyViewHolder extends RecyclerView.ViewHolder{

    TextView messageSend;
    TextView messageReceive;
    RelativeLayout aiMessage;
    RelativeLayout userMessage;
    RelativeLayout typing;

    MyViewHolder(@NonNull View itemView) {
      super(itemView);
      messageSend = itemView.findViewById(R.id.message_send);
      messageReceive = itemView.findViewById(R.id.message_receive);
      aiMessage = itemView.findViewById(R.id.aiMessage);
      userMessage = itemView.findViewById(R.id.userMessage);
      typing = itemView.findViewById(R.id.aiMessage_Typing);
    }
  }

  public void removeAt(int position) {
    messageList.remove(position);
    notifyItemRemoved(position);
    notifyItemRangeChanged(position, messageList.size());
  }

}
