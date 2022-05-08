package com.example.studymuffin;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;

public class CourseInfo {
    private String title;
    private Instructor instructor;
    private String classroom;
    private String zoomLink;
    private ArrayList<String> daysOfWeek;
    private ArrayList<NoteInfo> noteList;
    private int startTimeHour;
    private int startTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;
    private int color;
    private int uniqueId;
    public static int idCounter;

    public CourseInfo(String title, Instructor instructor, String classroom, String zoomLink,
                      ArrayList<String> daysOfWeek, int startTimeHour,
                      int startTimeMinute, int endTimeHour, int endTimeMinute, int color) {
        this.title = title;
        this.instructor = instructor;
        this.classroom = classroom;
        this.zoomLink = zoomLink;
        this.daysOfWeek = daysOfWeek;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
        this.color = color;
        this.noteList = new ArrayList<>();
        this.uniqueId = idCounter++;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public float calculateClassGrade(Context context) {
        float pointspossible = 0;
        float pointsearned = 0;

        ArrayList<Task> taskList = CalendarFragment.loadTaskList(context);

        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);

            System.out.println("Task id: " + task.getCourseId());
            System.out.println("Course id: " + this.uniqueId);

            // if the task belongs to this course, use it to calculate the grade
            if (task.getCourseId() == this.uniqueId) {
                if (task instanceof Assessment) {
                    System.out.println("Instance of Assessment");

                    Assessment a = (Assessment) taskList.get(i);
                    pointspossible= pointspossible + a.getPointsPossible();
                    pointsearned= pointsearned + a.getPointsEarned();
                } else if (task instanceof Assignment) {
                    System.out.println("Instance of Assignment");

                    Assignment b = (Assignment) taskList.get(i);
                    pointspossible = pointspossible + b.getPointsPossible();
                    pointsearned = pointsearned + b.getPointsEarned();
                } else {
                    System.out.println("Still registering as a task");
                }
            }
        }

        System.out.println(pointsearned);
        System.out.println(pointspossible);

        return (pointsearned / pointspossible)*100;
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

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getZoomLink() {
        return zoomLink;
    }

    public void setZoomLink(String zoomLink) {
        this.zoomLink = zoomLink;
    }

    public ArrayList<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(ArrayList<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<NoteInfo> getNoteList() {
        return this.noteList;
    }

    public int getUniqueId() {
        return this.uniqueId;
    }
}
