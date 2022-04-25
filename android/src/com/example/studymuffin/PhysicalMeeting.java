package com.example.studymuffin;

import java.util.Date;

public class PhysicalMeeting extends Meeting {
    private String location;

    public PhysicalMeeting(String name, String description, Date date, int startTimeHour,
                           int startTimeMinute, boolean notify, boolean recurring, int endTimeHour,
                           int endTimeMinute, String location, Priority priority, int courseId) {
        super(name, description, date, startTimeHour, startTimeMinute, notify, recurring, endTimeHour,
                endTimeMinute, priority, courseId);

        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
