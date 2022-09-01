package com.cubbysulotions.proo.ModelsClasses;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourEvent {
    LocalTime time;
    ArrayList<CalendarEvents> events;
    private ArrayList<DailyEvent> dailyEvent;

    public HourEvent(){}

    public HourEvent(LocalTime time, ArrayList<CalendarEvents> events) {
        this.time = time;
        this.events = events;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<CalendarEvents> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<CalendarEvents> events) {
        this.events = events;
    }

    public ArrayList<DailyEvent> getDailyEvent() {
        return dailyEvent;
    }

    public void setDailyEvent(ArrayList<DailyEvent> dailyEvent) {
        this.dailyEvent = dailyEvent;
    }
}
