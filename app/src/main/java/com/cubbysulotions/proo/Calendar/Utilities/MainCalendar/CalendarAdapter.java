package com.cubbysulotions.proo.Calendar.Utilities.MainCalendar;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private String monthDate;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, String monthDate) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.monthDate = monthDate;
    }

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cells, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (days.size() > 15) // month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else
            layoutParams.height = (int) (parent.getHeight());
        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("events").child(currentUser.getUid());


        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if(date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundResource(R.drawable.round_corner);

        if(date.getMonth().equals(CalendarUtils.selectedDate.getMonth())){
            holder.dayOfMonth.setTextColor(Color.WHITE);
        } else {
            holder.dayOfMonth.setTextColor(Color.parseColor("#4e4e4e"));
            holder.dayOfMonth.setAlpha(0.5F);
        }



        //Supposedly for adding dot if there's an event on that date
        List<DailyEvent> events = new ArrayList<>();

        reference.orderByChild("timeString").addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DailyEvent ev = new DailyEvent();
                for(DataSnapshot data : snapshot.getChildren()){
                    ev = data.getValue(DailyEvent.class);
                    events.add(ev);

                    String currentMonth = date.getMonth().toString();
                    String fromDB = CalendarUtils.formatteFullMonth(LocalDate.parse(ev.getDateString())).toUpperCase();

                    if(fromDB.equals(currentMonth)){
                        if(Integer.parseInt(String.valueOf(date.getDayOfMonth())) == Integer.parseInt(String.valueOf(LocalDate.parse(ev.getDateString()).getDayOfMonth()))){
                            holder.dot.setVisibility(View.VISIBLE);
                        }
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, LocalDate date);
    }




}
