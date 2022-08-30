package com.cubbysulotions.proo.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cubbysulotions.proo.ModelsClasses.CalendarEvents;
import com.cubbysulotions.proo.ModelsClasses.CalendarUtils;
import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.selectedDate;


public class EventEditFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_event_edit, container, false);
    }

    private Button btnSave, btnCancel, btnDate, btnTime;
    private TextView eventDate, eventTime;
    private EditText eventName;

    LocalTime time;

    NavController navController;

    int hour, minute;
    int year, months, day;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSave = view.findViewById(R.id.btnSaveEvent);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        eventName = view.findViewById(R.id.eventName);
        navController = Navigation.findNavController(view);

        time = LocalTime.now();
        btnDate.setText(CalendarUtils.formattedDate(selectedDate));
        btnTime.setText(CalendarUtils.formattedTime(time));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveActionEvent();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpDateDialog();
            }
        });
    }

    private void popUpDateDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDate = LocalDate.of(year, month + 1, day);
                btnDate.setText(CalendarUtils.formattedDate(selectedDate));
            }
        };

        year = Calendar.getInstance().get(Calendar.YEAR);
        months = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, year, months, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();

    }

    private void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                time = LocalTime.of(selectedHour, selectedMinute);
                btnTime.setText(CalendarUtils.formattedTime(time));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener,hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    private void cancel(){
        String flag = getArguments().getString("flag");
        switch (flag){
            case "FROM_WEEKLY":
                navController.navigate(R.id.action_eventEditFragment_to_weeklyCalendarFragment);
                break;
            case "FROM_DAILY":
                navController.navigate(R.id.action_eventEditFragment_to_dailyFragment);
                break;
            case "FROM_MONTH":
                navController.navigate(R.id.action_eventEditFragment_to_calendarFragment);
                break;
        }
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