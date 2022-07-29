package com.forloopers.studymuffin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CourseActivity extends AppCompatActivity {
    private TextView className;
    private TextView gradeTV;
    private TextView classInstructor;
    private TextView classRoom;
    private TextView classLink;
    private TextView classSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.setThemeOfApp(this);
        setContentView(R.layout.activity_course);

        Context context = CourseActivity.this;

        Intent i = this.getIntent();
        String json = i.getStringExtra(ClassFragment.COURSE_INTENT);
        Type collectionType = new TypeToken<CourseInfo>(){}.getType();
        CourseInfo course = new Gson().fromJson(json, collectionType);

        String classGrade = String.format("%.2f", course.calculateClassGrade(context));
        String classGradeStr = (classGrade).equals("NaN") ? "Input tasks to see a grade" :
                classGrade + "%";

        String startTimeStr = getAmPmFormat(course.getStartTimeHour(), course.getStartTimeMinute());
        String endTimeStr = getAmPmFormat(course.getEndTimeHour(), course.getEndTimeMinute());
        String classSchedule = course.getDaysOfWeek().get(0) + " " + startTimeStr + "-" + endTimeStr;

        this.className = this.findViewById(R.id.className);
        this.gradeTV = this.findViewById(R.id.gradeTV);
        this.classInstructor = this.findViewById(R.id.classInstructor);
        this.classRoom = this.findViewById(R.id.classRoom);
        this.classLink = this.findViewById(R.id.classLink);
        this.classSchedule = this.findViewById(R.id.classSchedule);

        this.className.setText(course.getTitle());
        this.gradeTV.setText(classGradeStr);
        this.classInstructor.setText(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        this.classRoom.setText(course.getClassroom());
        this.classLink.setText(course.getZoomLink());
        this.classSchedule.setText(classSchedule);
    }

    /**
     * turns the hour and minute to AM PM time format
     * @param hour the hour
     * @param minute the minute
     * @return a string representation of the time in AM PM format
     */
    public static String getAmPmFormat(int hour, int minute) {
        String time = "";

        int convertedHour = (hour > 12) ? hour - 12 : hour;

        time += convertedHour;
        time += ":";

        if (minute < 10) {
            time += "0" + minute;
        } else {
            time += minute;
        }

        time += (hour < 12) ? " AM" : " PM";

        return time;
    }
}