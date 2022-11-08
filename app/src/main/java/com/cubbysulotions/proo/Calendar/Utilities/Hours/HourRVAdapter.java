package com.cubbysulotions.proo.Calendar.Utilities.Hours;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Calendar.Utilities.Daily.DailyEventRVAdapter;
import com.cubbysulotions.proo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;

public class HourRVAdapter extends RecyclerView.Adapter<HourRVAdapter.ViewHolder>{

        private List<HourEvent> hour;
        private Context context;
        private String monthDate;
        private View view;
        private String date;

        private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

            public HourRVAdapter(Context context, List<HourEvent> hour, String monthDate, View view, String date) {
                this.context = context;
                this.hour = hour;
                this.monthDate = monthDate;
                this.view = view;
                this.date = date;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {


                public TextView txtTimeCell;
                private RecyclerView ChildRecyclerView;
                private ImageView dotted;
                public ViewHolder(final View itemView){
                    super(itemView);

                    txtTimeCell = itemView.findViewById(R.id.txtTimeCell);
                    ChildRecyclerView = itemView.findViewById(R.id.dailyEventListRecyclerView);
                    dotted = itemView.findViewById(R.id.dottedLine);
                }
            }

        @Override
        public int getItemCount() {
            return hour.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.hour_cell, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        private FirebaseAuth mAuth;
        FirebaseUser currentUser;
        FirebaseDatabase database;
        DatabaseReference reference;
        //List<DailyEvent> globalEvents;


        @Override
        public void onBindViewHolder(@NonNull HourRVAdapter.ViewHolder holder, int position)  {

            //Initialize firebase
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            database = FirebaseDatabase.getInstance();
            reference = database.getReference().child("events").child(currentUser.getUid());

            HourEvent hourEvent = hour.get(position);
            holder.txtTimeCell.setText(CalendarUtils.formattedShortTime(hourEvent.time));


            List<DailyEvent> events = new ArrayList<>();
            GridLayoutManager layoutManager = new GridLayoutManager(holder.ChildRecyclerView.getContext(), 1, GridLayoutManager.VERTICAL, false);
            layoutManager.setInitialPrefetchItemCount(events.size());

            DailyEventRVAdapter childItemAdapter = new DailyEventRVAdapter(events, date);
            holder.ChildRecyclerView.setLayoutManager(layoutManager);
            holder.ChildRecyclerView.setAdapter(childItemAdapter);
            holder.ChildRecyclerView.setRecycledViewPool(viewPool);

            reference.orderByChild("timeString").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyEvent ev = new DailyEvent();

                    ArrayList<String> time = new ArrayList<>();
                    for(int i = 0; i < hourEventList().size(); i++){
                        time.add(CalendarUtils.formattedShortTime(hourEvent.time));
                    }

                    for(DataSnapshot data : snapshot.getChildren()){
                        ev = data.getValue(DailyEvent.class);
                        if(monthDate.equals(CalendarUtils.monthDayFormatter(LocalDate.parse(ev.getDateString())))){
                            if(CalendarUtils.formattedShortTime(hourEvent.time).equals(CalendarUtils.formattedShortTime(LocalTime.parse(ev.getTimeString())))){
                                events.add(ev);
                                holder.dotted.setVisibility(View.GONE);
                            }
                        }
                    }
                    childItemAdapter.updateDataSet(events);
                    //Log.d("test", "Time: " + CalendarUtils.formattedShortTime(hourEvent.time) +"Global events: " + globalEvents);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        private ArrayList<HourEvent> hourEventList() {
            ArrayList<HourEvent> list = new ArrayList<>();
            for (int hour = 1; hour < 24; hour++){
                LocalTime time = LocalTime.of(hour, 0);
                ArrayList<CalendarEvents> events = CalendarEvents.eventsForDateandTime(selectedDate, time);
                HourEvent hourEvent = new HourEvent(time, events);
                list.add(hourEvent);
            }
            return list;
        }
}
