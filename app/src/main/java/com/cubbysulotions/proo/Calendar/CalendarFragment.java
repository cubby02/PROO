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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.Calendar.Utilities.MainCalendar.CalendarAdapter;
import com.cubbysulotions.proo.R;

import java.time.LocalDate;
import java.util.ArrayList;

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
    private RecyclerView calendarRecyclerView;
    NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        txtMonth = view.findViewById(R.id.txtMonth);
        //btnWeekly = view.findViewById(R.id.btnWeekly);
        calendarRecyclerView = view.findViewById(R.id.calendarDatesRecyclerView);
        navController = Navigation.findNavController(view);

        selectedDate = LocalDate.now();
        setMonthView();


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        txtMonth.setText(monthYearFormatter(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
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