package com.cubbysulotions.proo.Calendar.Utilities.MainCalendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
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

        if(date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.WHITE);
        else
            holder.dayOfMonth.setTextColor(Color.parseColor("#4e4e4e"));

        /* Supposedly for adding dot if there's an event on that date
        List<CalendarEvents> events = new ArrayList<>();

        reference.orderByChild("timeString").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                CalendarEvents ev = new CalendarEvents();
                for(DataSnapshot data : snapshot.getChildren()){
                    ev = data.getValue(CalendarEvents.class);
                    events.add(ev);
                }

                if(CalendarUtils.selectedDate == LocalDate.parse(ev.getDateString())){
                    if(size < 0)
                        holder.dot.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); */


    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, LocalDate date);
    }




}
