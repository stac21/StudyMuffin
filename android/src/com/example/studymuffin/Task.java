package com.example.studymuffin;

import java.util.ArrayList;
import java.util.Date;

// needed to make this not abstract so that the saving/loading to/from json would work
public class Task {
    private String name;
    private String description;
    private Date date;
    private int startTimeHour;
    private int startTimeMinute;
    private boolean completed;
    private boolean notify;
    private Priority priority;
    private ArrayList<Goal> goals;

    public Task(String name, String description, Date date, int startTimeHour, int startTimeMinute,
                boolean notify, Priority priority) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.completed = false;
        this.notify = notify;
        this.priority = priority;
        goals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(int startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public int getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(int startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ArrayList<Goal> getGoals() { return goals; }

    public void setGoals(ArrayList<Goal> goals) { this.goals = goals; }
}
