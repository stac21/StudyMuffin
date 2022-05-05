package com.example.studymuffin;

import java.util.Date;

public class Assessment extends Task {
    private float pointsEarned;
    private float pointsPossible;
    private int endTimeHour;
    private int endTimeMinute;

    public Assessment(String name, String description, Date date, int startTimeHour,
                      int startTimeMinute, boolean notify, float pointsPossible, int endTimeHour,
                      int endTimeMinute, Priority priority) {
        super(name, description, date, startTimeHour, startTimeMinute, notify, priority);

        this.pointsPossible = pointsPossible;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
    }

    public float getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(float pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public float getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(float pointsPossible) {
        this.pointsPossible = pointsPossible;
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
