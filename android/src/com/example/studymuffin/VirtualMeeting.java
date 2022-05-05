package com.example.studymuffin;

import java.util.Date;

public class VirtualMeeting extends Meeting {
    private String link;

    public VirtualMeeting(String name, String description, Date date, int startTimeHour,
                          int startTimeMinute, boolean notify, boolean recurring, int endTimeHour,
                          int endTimeMinute, String link, Priority priority) {
        super(name, description, date, startTimeHour, startTimeMinute, notify, recurring, endTimeHour,
                endTimeMinute, priority);

        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
