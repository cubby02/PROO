package com.cubbysulotions.proo.Calendar.Utilities.Events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.R;

import java.util.List;

public class EventAdapter extends ArrayAdapter<CalendarEvents> {


    public EventAdapter(@NonNull Context context, List<CalendarEvents> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CalendarEvents events = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        //TextView eventCell = convertView.findViewById(R.id.eventCell);
        String eventTitle = CalendarUtils.formattedTime(events.getTime()) + "\t" + events.getName();
        //eventCell.setText(eventTitle);
        return convertView;
    }


}
