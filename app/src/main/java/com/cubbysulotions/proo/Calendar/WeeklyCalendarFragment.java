package com.cubbysulotions.proo.Calendar;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.ModelsClasses.CalendarAdapter;
import com.cubbysulotions.proo.ModelsClasses.CalendarEvents;
import com.cubbysulotions.proo.ModelsClasses.CalendarUtils;
import com.cubbysulotions.proo.ModelsClasses.EventAdapter;
import com.cubbysulotions.proo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.daysInMonthArray;
import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.daysInWeekArray;
import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.monthYearFormatter;
import static com.cubbysulotions.proo.ModelsClasses.CalendarUtils.selectedDate;

public class WeeklyCalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_weekly_calendar, container, false);
    }

    private Button btnPrevious, btnNext;
    private TextView txtMonth;
    private RecyclerView calendarRecyclerView;
    private ListView eventList;
    private FloatingActionButton btnNewEvent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPreviousWeekly);
        btnNext = view.findViewById(R.id.btnNextWeekly);
        txtMonth = view.findViewById(R.id.txtMonthWeekly);
        calendarRecyclerView = view.findViewById(R.id.calendarDatesRecyclerViewWeekly);
        btnNewEvent = view.findViewById(R.id.addEvent);
        eventList = view.findViewById(R.id.eventList);
        NavController navController = Navigation.findNavController(view);

        selectedDate = LocalDate.now();
        setWeekVIew();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.plusWeeks(1);
                setWeekVIew();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.minusWeeks(1);
                setWeekVIew();
            }
        });

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_weeklyCalendarFragment_to_eventEditFragment);
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekVIew() {
        txtMonth.setText(monthYearFormatter(selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<CalendarEvents> dailyEvent = CalendarEvents.eventsForDate(selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getActivity(), dailyEvent);
        eventList.setAdapter(eventAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {

        selectedDate = date;
        setWeekVIew();
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }
}