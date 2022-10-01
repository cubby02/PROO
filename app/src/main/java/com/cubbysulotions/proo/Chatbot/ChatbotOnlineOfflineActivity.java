package com.cubbysulotions.proo.Chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.R;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatbotOnlineOfflineActivity extends AppCompatActivity implements BotReply {

  RecyclerView chatView;
  ChatAdapter chatAdapter;
  List<Message> messageList = new ArrayList<>();
  EditText editMessage;
  ImageButton btnSend;

  //dialogFlow
  private SessionsClient sessionsClient;
  private SessionName sessionName;
  private String uuid = UUID.randomUUID().toString();
  private String TAG = "mainactivity";

  DBController dbController;

  String message ="";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatbot_layout);
    chatView = findViewById(R.id.rdChats);
    editMessage = findViewById(R.id.userMessage);
    btnSend = findViewById(R.id.sendBtn);

    chatAdapter = new ChatAdapter(messageList, this);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    chatView.setLayoutManager(manager);
    chatView.setAdapter(chatAdapter);

    dbController = new DBController(this);
    dbController.convertCSV(this);


    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        message = editMessage.getText().toString();
        if (!message.isEmpty()) {
          messageList.add(new Message(message, false));
          editMessage.setText("");
          sendMessageToBot(message);
          chatView.getAdapter().notifyDataSetChanged();
          chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
        } else {
          Toast.makeText(ChatbotOnlineOfflineActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
        }
      }
    });

    setUpBot();
    //sendMessageToBot("Welcome");
  }

  private void setUpBot() {
    try {
      InputStream stream = this.getResources().openRawResource(R.raw.credential);
      GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
              .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
      String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

      SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
      SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
              FixedCredentialsProvider.create(credentials)).build();
      sessionsClient = SessionsClient.create(sessionsSettings);
      sessionName = SessionName.of(projectId, uuid);

      Log.d(TAG, "projectId : " + projectId);
    } catch (Exception e) {
      Log.d(TAG, "setUpBot: " + e.getMessage());
    }
  }

  private void sendMessageToBot(String message) {
    QueryInput input = QueryInput.newBuilder()
        .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
    new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
  }

  @Override
  public void callback(DetectIntentResponse returnResponse) {
    Log.d("Test", "Response" + returnResponse);
     if(returnResponse!=null) {
       String botReply = returnResponse.getQueryResult().getFulfillmentText();

       List<Message> list = new ArrayList<>();
       for (int i = 0; i <  returnResponse.getQueryResult().getFulfillmentMessagesList().size(); i++){
         String rawText = returnResponse.getQueryResult().getFulfillmentMessages(i).getText().toString();
         String processedText = rawText.replace("text: \"", "");
         String processedText2 = processedText.replace("\\", "");
         String finalText = processedText2.replace("\"", "");
         list.add(new Message(finalText));
       }

       if(!botReply.isEmpty()){
         if(!list.isEmpty()){
           for (int i = 0; i <  returnResponse.getQueryResult().getFulfillmentMessagesList().size(); i++){
             messageList.add(new Message(list.get(i).getText(), true));
           }
         } else {
           messageList.add(new Message(botReply, true));
         }

         chatAdapter.notifyDataSetChanged();
         chatView.scrollToPosition(chatAdapter.getItemCount() - 1);


       }else {
         Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
       }
     } else {
       String response = dbController.getResponse(message);
       messageList.add(new Message(response, true));
       chatAdapter.notifyDataSetChanged();
       chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
     }
  }
}
