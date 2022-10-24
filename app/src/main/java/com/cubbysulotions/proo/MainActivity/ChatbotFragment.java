package com.cubbysulotions.proo.MainActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cubbysulotions.proo.Chatbot.ChatbotOnlineOfflineActivity;
import com.cubbysulotions.proo.Chatbot.DBController;
import com.cubbysulotions.proo.R;

public class ChatbotFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    private Button start;
    private Spinner month;
    private String selectedMonth="";
    DBController db;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        start = view.findViewById(R.id.btnStartChat);
        month = view.findViewById(R.id.month);
        db = new DBController(getActivity());

        ((MainActivity)getActivity()).updateStatusBarColor("#FFFFFFFF");
        ((MainActivity)getActivity()).setLightStatusBar(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(adapter);

        String savedMonth = db.getSavedMonth();

        if(savedMonth != null){
            int pos = adapter.getPosition(savedMonth);
            month.setSelection(pos);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMonth = month.getSelectedItem().toString();
                add(selectedMonth);
                Intent chat = new Intent(getActivity(), ChatbotOnlineOfflineActivity.class);
                chat.putExtra("month", selectedMonth);
                startActivity(chat);
            }
        });
    }

    public void add(String month){
        boolean add = db.saveMonth(month);
        if(add){
            toast("Month saved");
        } else {
            toast("Failed!");
        }
    }

    public void toast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}