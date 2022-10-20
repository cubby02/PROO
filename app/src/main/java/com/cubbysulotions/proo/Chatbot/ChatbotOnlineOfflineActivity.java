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

import com.cubbysulotions.proo.Chatbot.OptionVersion.ChoiceAdapter;
import com.cubbysulotions.proo.Chatbot.OptionVersion.Choices;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ChatbotOnlineOfflineActivity extends AppCompatActivity implements BotReply, ChoiceAdapter.OnItemClickListener {


  RecyclerView chatView;
  RecyclerView choiceRV;
  ChatAdapter chatAdapter;
  ChoiceAdapter choiceAdapter;
  List<Message> messageList = new ArrayList<>();
  List<Choices> choicesList = new ArrayList<>();
  List<String> moreResult;
  List<String> topicsList;
  List<String> greetingsList;
  List<String> closingList;
  List<String> exitList;
  List<String> exitResponseList;
  EditText editMessage;
  ImageButton btnSend;

  //dialogFlow
  private SessionsClient sessionsClient;
  private SessionName sessionName;
  private String uuid = UUID.randomUUID().toString();
  private String TAG = "mainactivity";

  DBController dbController;

  String message ="";

  String selectedMonth="";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatbot_layout);
    chatView = findViewById(R.id.rdChats);
    editMessage = findViewById(R.id.userMessage);
    btnSend = findViewById(R.id.sendBtn);
    choiceRV = findViewById(R.id.selectionRecyclerView);

    chatAdapter = new ChatAdapter(messageList, this);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    chatView.setLayoutManager(manager);
    chatView.setAdapter(chatAdapter);

    choiceAdapter = new ChoiceAdapter(choicesList);
    LinearLayoutManager manager2 = new LinearLayoutManager(this);
    choiceRV.setLayoutManager(manager2);
    choiceRV.setAdapter(choiceAdapter);

    dbController = new DBController(this);
    dbController.convertCSV(this);

    selectedMonth = getIntent().getStringExtra("month");

    String[] more = getResources().getStringArray(R.array.more_results);
    moreResult = Arrays.asList(more);

    String[] topics = getResources().getStringArray(R.array.topics);
    topicsList = Arrays.asList(topics);

    String[] greetings = getResources().getStringArray(R.array.greetings);
    greetingsList = Arrays.asList(greetings);

    String[] closing = getResources().getStringArray(R.array.closing);
    closingList = Arrays.asList(closing);

    String[] exit = getResources().getStringArray(R.array.exit);
    exitList = Arrays.asList(exit);

    String[] responseExit = getResources().getStringArray(R.array.exit_response);
    exitResponseList = Arrays.asList(responseExit);

    choiceAdapter.setOnItemClickListener(ChatbotOnlineOfflineActivity.this);

    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        message = editMessage.getText().toString();
        if (!message.isEmpty()) {
          messageList.add(new Message(message, 1));
          editMessage.setText("");
          sendMessageToBot(message);
          messageList.add(new Message("", 2));
          chatView.getAdapter().notifyDataSetChanged();
          chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
        } else {
          Toast.makeText(ChatbotOnlineOfflineActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
        }
      }
    });

    setUpBot();
    messageList.add(new Message("", 2));
    sendMessageToBot(selectedMonth);


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
     if(returnResponse!=null) {
       chatAdapter.removeAt(chatAdapter.getItemCount() - 1);
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
           //adding the list of ai response to Model class
           for (int i = 0; i <  returnResponse.getQueryResult().getFulfillmentMessagesList().size(); i++){
             messageList.add(new Message(list.get(i).getText(), 0));
           }
           //populating the choices
           populateChoices(list);

         } else {
           messageList.add(new Message(botReply, 0));
           populateChoices(botReply);
         }

         choiceAdapter.updateDataSet(choicesList);
         chatAdapter.notifyDataSetChanged();
         chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
         choiceAdapter.setOnItemClickListener(ChatbotOnlineOfflineActivity.this);

       } else {
         Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
       }
     } else {
       chatAdapter.removeAt(chatAdapter.getItemCount() - 1);
       String response = dbController.getResponse(message);
       messageList.add(new Message(response, 0));
       chatAdapter.notifyDataSetChanged();
       chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
     }
  }

  //when the response is single response
  private void populateChoices(String botReply) {
    choiceAdapter.clear();
    if(exitResponseList.contains(botReply.trim())){
      for (int i = 0; i <  topicsList.size(); i++){
        choicesList.add(new Choices(topicsList.get(i)));
      }
      choicesList.add(new Choices("Go Home"));
    } else {
      if(closingList.contains(botReply.trim())){
        for (int i = 0; i <  topicsList.size(); i++){
          choicesList.add(new Choices(topicsList.get(i)));
        }
      } else {
        String text = moreResult.get(new Random().nextInt(moreResult.size()));
        String exit = exitList.get(new Random().nextInt(exitList.size()));
        choicesList.add(new Choices(text));
        choicesList.add(new Choices(exit));
      }
    }
  }

  //when the response is multiple response
  private void populateChoices(List<Message> list) {
    String lastMessage = list.get(list.size() -1).getText().trim();
    choiceAdapter.clear();
    if(exitResponseList.contains(lastMessage)){
      for (int i = 0; i <  topicsList.size(); i++){
        choicesList.add(new Choices(topicsList.get(i)));
      }
      choicesList.add(new Choices("Go Home"));
    } else {
      if(greetingsList.contains(lastMessage)){
        for (int i = 0; i <  topicsList.size(); i++){
          choicesList.add(new Choices(topicsList.get(i)));
        }
      } else{
        if(closingList.contains(lastMessage)){
          for (int i = 0; i <  topicsList.size(); i++){
            choicesList.add(new Choices(topicsList.get(i)));
          }
        } else {
          String text = moreResult.get(new Random().nextInt(moreResult.size()));
          String exit = exitList.get(new Random().nextInt(exitList.size()));
          choicesList.add(new Choices(text));
          choicesList.add(new Choices(exit));
        }
      }
    }


  }

  @Override
  public void onItemClick(int position) {
    Choices clickedItem = choicesList.get(position);
    switch (clickedItem.getChoice()){
      case "Pregnancy Symptoms":
        messageList.add(new Message(getResources().getString(R.string.symptoms), 1));
        sendMessageToBot("i want to know about pregnancy symptoms during my " + selectedMonth);
        break;
      case "Baby's Development":
        messageList.add(new Message(getResources().getString(R.string.baby), 1));
        sendMessageToBot("i want to know about my baby's development during " + selectedMonth);
        break;
      case "What to expect in Prenatal Visit":
        messageList.add(new Message(getResources().getString(R.string.expect), 1));
        sendMessageToBot("what will happen during my "+ selectedMonth +" prenatal check-up");
        break;
      case "Go Home":
        finish();
        break;
      default:
        messageList.add(new Message(clickedItem.getChoice(), 1));
        sendMessageToBot(clickedItem.getChoice());
        break;
    }
    chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
    messageList.add(new Message("", 2));
    chatView.getAdapter().notifyDataSetChanged();
  }

  private void toast(String message){
    Toast.makeText(ChatbotOnlineOfflineActivity.this, message, Toast.LENGTH_SHORT).show();
  }
}
