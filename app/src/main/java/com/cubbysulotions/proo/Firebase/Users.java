package com.cubbysulotions.proo.Firebase;

public class Users {
    public String firstname;
    public String lastname;
    public String email;
    public String weeks;

    public Users() {
    }

    public Users(String firstname, String lastname, String email, String weeks) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.weeks = weeks;
    }

    public Users(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
