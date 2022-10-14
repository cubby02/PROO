package com.cubbysulotions.proo.Journal;

public class Journal {
    private String content;
    private String photo;
    private String date;
    private String time;
    private String like;

    public Journal() {
    }

    public Journal(String content, String photo, String date, String time, String like) {
        this.content = content;
        this.photo = photo;
        this.date = date;
        this.time = time;
        this.like = like;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }
}
