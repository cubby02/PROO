package com.cubbysulotions.proo.Calendar.Utilities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalendarEvents {

    public static ArrayList<CalendarEvents> eventsList = new ArrayList<>();

    public static ArrayList<CalendarEvents> eventsForDate(LocalDate date){
        ArrayList<CalendarEvents> events = new ArrayList<>();

        for(CalendarEvents event : eventsList){
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    public static ArrayList<CalendarEvents> eventsForDateandTime(LocalDate date, LocalTime time){
        ArrayList<CalendarEvents> events = new ArrayList<>();

        for(CalendarEvents event : eventsList){
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if(event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }

        return events;
    }

    private String name;
    private LocalDate date;
    private LocalTime time;
    private String id;
    private String dateString;
    private String timeString;
    private String requestCode;
    private String notificationID;
    private String content;

    public CalendarEvents(){}

    public CalendarEvents(String name, String id, String dateString, String timeString, String requestCode, String notificationID, String content) {
        this.name = name;
        this.id = id;
        this.dateString = dateString;
        this.timeString = timeString;
        this.requestCode = requestCode;
        this.notificationID = notificationID;
        this.content = content;
    }


    public CalendarEvents(String name, LocalDate date, LocalTime time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }
}
