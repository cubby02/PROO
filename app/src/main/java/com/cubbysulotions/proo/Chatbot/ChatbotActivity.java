package com.cubbysulotions.proo.Chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatbotActivity extends AppCompatActivity {

    RecyclerView chatsRV;
    EditText userMsg;
    FloatingActionButton sendMsg;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    ArrayList<ChatModel> chatModelArrayList;
    ChatRVAdapter chatRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_layout);

        chatsRV = findViewById(R.id.rdChats);
        userMsg = findViewById(R.id.userMessage);
        sendMsg = findViewById(R.id.sendBtn);

        chatModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatModelArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userMsg.getText().toString().isEmpty()){
                    Toast.makeText(ChatbotActivity.this, "Please enter your message", Toast.LENGTH_LONG).show();
                    return;
                }

                getResponse(userMsg.getText().toString());
                userMsg.setText("");
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        chatsRV.scrollToPosition(chatRVAdapter.getItemCount() - 1);
                    }
                });

    }

    private void getResponse(String msg){
        chatModelArrayList.add(new ChatModel(msg, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(chatRVAdapter.getItemCount() - 1);
        String url = "http://api.brainshop.ai/get?bid=169348&key=WAwnZQU93wXVPYxS&uid=[uid]&msg="+msg;
        String BASE_URL = "http://api.brainshop.ai/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MessageModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                if (response.isSuccessful()){
                    MessageModel modal = response.body();
                    chatModelArrayList.add(new ChatModel(modal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                    chatsRV.scrollToPosition(chatRVAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                chatModelArrayList.add(new ChatModel("Please revert your question", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
                Log.d("Error", t.getMessage());
                chatsRV.scrollToPosition(chatRVAdapter.getItemCount() - 1);
            }
        });
    }
}