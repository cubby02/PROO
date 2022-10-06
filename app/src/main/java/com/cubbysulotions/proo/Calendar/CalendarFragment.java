package com.cubbysulotions.proo.Calendar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.Calendar.Utilities.AllTaskAdapter;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Calendar.Utilities.MainCalendar.CalendarAdapter;
import com.cubbysulotions.proo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hari.bounceview.BounceView;

import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.daysInMonthArray;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.monthYearFormatter;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    private ImageButton btnPrevious, btnNext, btnWeekly;
    private TextView txtMonth;
    private RecyclerView calendarRecyclerView, allTaskRecylerView;
    NavController navController;

    private TextView calendarTxtLayout, allTaskTxtLayout;

    private LinearLayout calendarLayout, allTaskLayout;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        BounceView.addAnimTo(btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        BounceView.addAnimTo(btnNext);
        txtMonth = view.findViewById(R.id.txtMonth);
        //btnWeekly = view.findViewById(R.id.btnWeekly);
        calendarRecyclerView = view.findViewById(R.id.calendarDatesRecyclerView);
        navController = Navigation.findNavController(view);
        calendarTxtLayout = view.findViewById(R.id.txtCalendar);
        BounceView.addAnimTo(calendarTxtLayout);
        allTaskTxtLayout = view.findViewById(R.id.txtTasks);
        BounceView.addAnimTo(allTaskTxtLayout);
        calendarLayout = view.findViewById(R.id.calendarLayoutView);
        allTaskLayout = view.findViewById(R.id.allTasksLayout);
        allTaskRecylerView = view.findViewById(R.id.allTasksRV);

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("events").child(currentUser.getUid());

        selectedDate = LocalDate.now();
        setMonthView();
        setAlltasks();

        allTaskTxtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTaskTxtLayout.setBackgroundResource(R.drawable.bg_btn_2);
                allTaskTxtLayout.setTextColor(Color.WHITE);
                calendarTxtLayout.setBackground(null);
                calendarTxtLayout.setTextColor(Color.parseColor("#323232"));
                calendarLayout.setVisibility(View.GONE);
                allTaskLayout.setVisibility(View.VISIBLE);

            }
        });

        calendarTxtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarTxtLayout.setBackgroundResource(R.drawable.bg_btn_2);
                calendarTxtLayout.setTextColor(Color.WHITE);
                allTaskTxtLayout.setBackground(null);
                allTaskTxtLayout.setTextColor(Color.parseColor("#323232"));
                allTaskLayout.setVisibility(View.GONE);
                calendarLayout.setVisibility(View.VISIBLE);
            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonth();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonth();
            }
        });
    }

    private void setAlltasks() {
        try{
            List<DailyEvent> events = new ArrayList<>();
            AllTaskAdapter allTaskAdapter = new AllTaskAdapter(events);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            allTaskRecylerView.setLayoutManager(manager);
            allTaskRecylerView.setAdapter(allTaskAdapter);


            reference.orderByChild("dateString").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyEvent ev = new DailyEvent();

                    for(DataSnapshot data : snapshot.getChildren()){
                        ev = data.getValue(DailyEvent.class);
                        events.add(ev);

                    }

                    Collections.reverse(events);
                    allTaskAdapter.updateDataSet(events);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Main Screen error", "exception", e);
        }
    }

    String monthDate;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthDate = CalendarUtils.monthDayFormatter(selectedDate);
        txtMonth.setText(monthYearFormatter(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, monthDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void nextMonth() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void previousMonth() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null){
            selectedDate = date;
            setMonthView();
            Bundle bundle = new Bundle();
            bundle.putString("date", String.valueOf(selectedDate));
            navController.navigate(R.id.action_calendarFragment_to_weeklyCalendarFragment, bundle);
        }
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}