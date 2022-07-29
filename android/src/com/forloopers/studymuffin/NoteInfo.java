package com.forloopers.studymuffin;

public class NoteInfo {
    public String title;
    public String description;
    public int monthEdited;
    public int dayEdited;
    public String className;

    public NoteInfo(String title, int monthEdited, int dayEdited) {
        this.title = title;
        this.description = null;
        this.monthEdited = monthEdited;
        this.dayEdited = dayEdited;
    }

    public int getMonthEdited() {
        return monthEdited;
    }

    public void setMonthEdited(int monthEdited) {
        this.monthEdited = monthEdited;
    }

    public int getDayEdited() {
        return dayEdited;
    }

    public void setDayEdited(int dayEdited) {
        this.dayEdited = dayEdited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName(){return className;}

    public void setClassName(String className){ this.className = className;}

    private String getMonthString() {
        switch (this.monthEdited) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }

    public String getDateEdited() {
        return this.getMonthString() + ", " + this.dayEdited;
    }
}
