package com.cubbysulotions.proo.Calendar.Utilities.Events;

public class DailyEvent {
    private String name;
    private String id;
    private String dateString;
    private String timeString;
    private String requestCode;
    private String notificationID;

    public DailyEvent(String name, String id, String dateString, String timeString) {
        this.name = name;
        this.id = id;
        this.dateString = dateString;
        this.timeString = timeString;
    }

    public DailyEvent(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
