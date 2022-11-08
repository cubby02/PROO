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
import android.widget.TextView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Hours.HourEvent;
import com.cubbysulotions.proo.Calendar.Utilities.Hours.HourRVAdapter;
import com.cubbysulotions.proo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;

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

    private RecyclerView eventList;
    private FloatingActionButton btnNewEvent;
    NavController navController;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;

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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("events").child(currentUser.getUid());

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
                //navController.navigate(R.id.action_dailyFragment_to_eventEditFragment, bundle);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        setDayView();
    }

    private String monthDate;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDayView() {
        monthDate = CalendarUtils.monthDayFormatter(selectedDate);
        txtMonth.setText(monthDate);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        txtDayOfWeek.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        //HourRVAdapter adapter = new HourRVAdapter(getActivity(), hourEventList(), monthDate, );
        //eventList.setLayoutManager(layoutManager);
        //eventList.setAdapter(adapter);
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