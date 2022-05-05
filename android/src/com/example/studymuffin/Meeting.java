package com.example.studymuffin;

import java.util.Date;

public abstract class Meeting extends Task {
    private boolean recurring;
    private int endTimeHour;
    private int endTimeMinute;

    public Meeting(String name, String description, Date date, int startTimeHour,
                   int startTimeMinute, boolean notify, boolean recurring, int endTimeHour,
                   int endTimeMinute, Priority priority) {
        super(name, description, date, startTimeHour, startTimeMinute, notify, priority);

        this.recurring = recurring;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }
}
