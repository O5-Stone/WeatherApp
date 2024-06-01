package com.sreeginy.tairp_weather.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Course {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;

    private String date;
    private String name;
    private String time;
    private String location;

    public Course(String date, String name, String time, String location) {
        this.date = date;
        this.name = name;
        this.time = time;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
