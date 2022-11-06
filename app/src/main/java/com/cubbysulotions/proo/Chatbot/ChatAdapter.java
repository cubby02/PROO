package com.cubbysulotions.proo.Chatbot;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
      case 0: //ai
        holder.aiMessage.setVisibility(View.VISIBLE);
        holder.userMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.messageReceive.setText(message);
        break;
      case 1: //user
        holder.userMessage.setVisibility(View.VISIBLE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.messageSend.setText(message);
        break;
      case 2: //ai typing
        holder.userMessage.setVisibility(View.GONE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.VISIBLE);
        break;
      case 3: //first index
        holder.userMessage.setVisibility(View.GONE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.midContent.setVisibility(View.GONE);
        holder.lastContent.setVisibility(View.GONE);
        holder.bot.setVisibility(View.INVISIBLE);

        holder.list.setVisibility(View.VISIBLE);
        holder.firstContent.setVisibility(View.VISIBLE);
        holder.firstIndex.setText(message);
        break;
      case 4: //mid index
        holder.userMessage.setVisibility(View.GONE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.firstContent.setVisibility(View.GONE);
        holder.lastContent.setVisibility(View.GONE);
        holder.bot.setVisibility(View.INVISIBLE);

        holder.list.setVisibility(View.VISIBLE);
        holder.midContent.setVisibility(View.VISIBLE);
        holder.midIndex.setText(message);
        break;
      case 5: //last index
        holder.userMessage.setVisibility(View.GONE);
        holder.aiMessage.setVisibility(View.GONE);
        holder.typing.setVisibility(View.GONE);
        holder.firstContent.setVisibility(View.GONE);
        holder.midContent.setVisibility(View.GONE);
        holder.bot.setVisibility(View.VISIBLE);

        holder.list.setVisibility(View.VISIBLE);
        holder.lastContent.setVisibility(View.VISIBLE);
        holder.lastIndex.setText(message);
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
    RelativeLayout list, firstContent, midContent, lastContent;
    TextView firstIndex, midIndex, lastIndex;
    ImageView bot, bot2;


    MyViewHolder(@NonNull View itemView) {
      super(itemView);
      messageSend = itemView.findViewById(R.id.message_send);
      messageReceive = itemView.findViewById(R.id.message_receive);
      aiMessage = itemView.findViewById(R.id.aiMessage);
      userMessage = itemView.findViewById(R.id.userMessage);
      typing = itemView.findViewById(R.id.aiMessage_Typing);
      list = itemView.findViewById(R.id.aiMessageList);
      firstContent = itemView.findViewById(R.id.aiContent_firstIndex);
      midContent = itemView.findViewById(R.id.aiContent_midIndex);
      lastContent = itemView.findViewById(R.id.aiContent_last);
      firstIndex = itemView.findViewById(R.id.ai_firstIndex);
      midIndex = itemView.findViewById(R.id.ai_midIndex);
      lastIndex = itemView.findViewById(R.id.ai_lastIndex);
      bot = itemView.findViewById(R.id.botIMG3);
      //bot2 = itemView.findViewById(R.id.botIMG2);

    }
  }

  public void removeAt(int position) {
    messageList.remove(position);
    notifyItemRemoved(position);
    notifyItemRangeChanged(position, messageList.size());
  }

}
