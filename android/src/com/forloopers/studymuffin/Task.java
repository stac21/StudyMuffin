package com.forloopers.studymuffin;

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
    private int courseId;
    private int uniqueId;
    public static int idCounter;
    private TaskType taskType;
    private ArrayList<Goal> goals;

    public Task(String name, String description, Date date, int startTimeHour, int startTimeMinute,
                boolean notify, Priority priority, int courseId, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.completed = false;
        this.notify = notify;
        this.priority = priority;
        this.courseId = courseId;
        // need to change this uniqueId to actually be unique
        this.uniqueId = idCounter++;
        this.taskType = taskType;
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

    public boolean shouldNotify() {
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

    public int getCourseId() {
        return courseId;
    }

    public int getUniqueId() {
        return this.uniqueId;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int mIdCounter) {
        idCounter = mIdCounter;
    }

    public TaskType getTaskType() {
        return this.taskType;
    }

    public ArrayList<Goal> getGoals() { return goals; }

    public void setGoals(ArrayList<Goal> goals) { this.goals = goals; }

    public void addToGoals(Goal goal){ goals.add(goal); }

    public void removeFromGoals(Goal goal) {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getName().equals(goal.getName())) {
                goals.remove(i);
                break;
            }
        }
    }

    /**
     * compares two tasks by the attribute the user selected in their sort preference, found in
     * CalendarFragment
     * @param other the task to compare this task to
     * @return 0 if equal, < 0 if this task is less than other, > 0 if this task is greater than other
     */
    public int compareToByPreference(Task other) {
        if (other == null) {
            return 1;
        }

        switch (CalendarFragment.sortPreference) {
            case DUE_DATE:
                return this.date.compareTo(other.getDate());
            case PRIORITY:
                return this.priority.compareTo(other.getPriority());
            default:
                // this branch should never be selected. Only here to ensure the function returns
                // from all paths
                return 1;
        }
    }
}
