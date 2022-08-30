package com.cubbysulotions.proo.ModelsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cubbysulotions.proo.R;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent events = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView, events.time);
        setEvents(convertView, events.events);
        return convertView;
    }

    private void setEvents(View convertView, ArrayList<CalendarEvents> events) {
        TextView event1 = convertView.findViewById(R.id.event1);
        TextView event2 = convertView.findViewById(R.id.event2);
        TextView event3 = convertView.findViewById(R.id.event3);

        if(events.size() == 0){
            hideEvents(event1);
            hideEvents(event2);
            hideEvents(event3);
        } else if(events.size() == 1){
            setEvent(event1, events.get(0));
            hideEvents(event2);
            hideEvents(event3);
        } else if(events.size() == 2){
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            hideEvents(event3);
        } else if(events.size() == 3){
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
        } else {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);

            String eventNotShown = String.valueOf(events.size() - 2);
            eventNotShown += " More Events";
            event3.setText(eventNotShown);
        }
    }

    private void setEvent(TextView tv, CalendarEvents calendarEvents) {
        tv.setText(calendarEvents.getName());
        tv.setVisibility(View.VISIBLE);
    }

    private void hideEvents(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }

    private void setHour(View convertView, LocalTime time) {
        TextView txtTimeCell = convertView.findViewById(R.id.txtTimeCell);
        txtTimeCell.setText(CalendarUtils.formattedShortTime(time));
    }
}
