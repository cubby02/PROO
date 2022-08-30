package com.cubbysulotions.proo.Calendar;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cubbysulotions.proo.ModelsClasses.CalendarEvents;
import com.cubbysulotions.proo.ModelsClasses.CalendarUtils;
import com.cubbysulotions.proo.ModelsClasses.HourAdapter;
import com.cubbysulotions.proo.ModelsClasses.HourEvent;
import com.cubbysulotions.proo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.selectedDate;

public class DailyFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_daily, container, false);
    }

    private Button btnPrevious, btnNext;
    private TextView txtMonth, txtDayOfWeek;
    private RecyclerView calendarRecyclerView;
    private ListView eventList;
    private FloatingActionButton btnNewEvent;
    NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPrevious = view.findViewById(R.id.btnPreviousDay);
        btnNext = view.findViewById(R.id.btnNextDay);
        txtMonth = view.findViewById(R.id.txtMonthDaily);
        txtDayOfWeek = view.findViewById(R.id.txtDayOfWeek);
        btnNewEvent = view.findViewById(R.id.addDailyEvent);
        eventList = view.findViewById(R.id.dailyEventList);
        navController = Navigation.findNavController(view);

        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.minusDays(1);
                setDayView();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.plusDays(1);
                setDayView();
            }
        });

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flag = "FROM_DAILY";
                Bundle bundle = new Bundle();
                bundle.putString("flag", flag);
                navController.navigate(R.id.action_dailyFragment_to_eventEditFragment, bundle);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        setDayView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDayView() {
        txtMonth.setText(CalendarUtils.monthDayFormatter(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        txtDayOfWeek.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getActivity(), hourEventList());
        eventList.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++){
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<CalendarEvents> events = CalendarEvents.eventsForDateandTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }
        return list;
    }


}