package com.example.myapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    private long idReminder;

    private Date date;
    private String message;
    private String username;

    public Reminder(long idReminder, Date date, String message, String username) {
        this.idReminder = idReminder;
        this.date = date;
        this.message = message;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getIdReminder() {
        return idReminder;
    }

    public void setIdReminder(long idReminder) {
        this.idReminder = idReminder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
