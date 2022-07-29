package com.forloopers.studymuffin;

import java.util.Date;

public class Assignment extends Task {
    private float pointsEarned;
    private float pointsPossible;

    public Assignment(String name, String description, Date date, int startTimeHour,
                      int startTimeMinute, boolean notify, float pointsPossible,
                      Priority priority, int courseId) {
        super(name, description, date, startTimeHour, startTimeMinute, notify, priority, courseId,
                TaskType.ASSIGNMENT);

        this.pointsPossible = pointsPossible;
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
}
