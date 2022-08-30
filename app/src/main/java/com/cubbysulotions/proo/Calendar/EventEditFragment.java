package com.cubbysulotions.proo.Calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cubbysulotions.proo.ModelsClasses.CalendarEvents;
import com.cubbysulotions.proo.ModelsClasses.CalendarUtils;
import com.cubbysulotions.proo.R;

import java.time.LocalTime;

import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.selectedDate;


public class EventEditFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_event_edit, container, false);
    }

    private Button btnSave;
    private TextView eventDate, eventTime;
    private EditText eventName;

    LocalTime time;

    NavController navController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSave = view.findViewById(R.id.btnSaveEvent);
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        eventName = view.findViewById(R.id.eventName);
        navController = Navigation.findNavController(view);

        time = LocalTime.now();
        eventDate.setText("Date: " + CalendarUtils.formattedDate(selectedDate));
        eventTime.setText("Time: " + CalendarUtils.formattedTime(time));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveActionEvent();
            }
        });
    }

    private void saveActionEvent() {
        String eventNameTxt = eventName.getText().toString();
        CalendarEvents newEvent = new CalendarEvents(eventNameTxt, selectedDate, time);
        CalendarEvents.eventsList.add(newEvent);
        String flag = getArguments().getString("flag");
        switch (flag){
            case "FROM_WEEKLY":
                navController.navigate(R.id.action_eventEditFragment_to_weeklyCalendarFragment);
                break;
            case "FROM_DAILY":
                navController.navigate(R.id.action_eventEditFragment_to_dailyFragment);
                break;
        }

    }
}