package com.example.studymuffin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
    private TextView classColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Context context = CourseActivity.this;

        Intent i = this.getIntent();
        String json = i.getStringExtra(ClassFragment.COURSE_INTENT);
        Type collectionType = new TypeToken<CourseInfo>(){}.getType();
        CourseInfo course = new Gson().fromJson(json, collectionType);

        this.className = this.findViewById(R.id.className);
        this.gradeTV = this.findViewById(R.id.gradeTV);
        this.classInstructor = this.findViewById(R.id.classInstructor);
        this.classRoom = this.findViewById(R.id.classRoom);
        this.classLink = this.findViewById(R.id.classLink);
        this.classColor = this.findViewById(R.id.classColor);

        this.className.setText(course.getTitle());
        this.gradeTV.setText(course.calculateClassGrade(context) + "%");
        this.classInstructor.setText(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        this.classRoom.setText(course.getClassroom());
        this.classLink.setText(course.getZoomLink());
        this.classColor.setText(course.getColor() + "");
    }
}