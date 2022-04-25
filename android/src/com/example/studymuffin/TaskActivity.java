package com.example.studymuffin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskActivity extends AppCompatActivity {
    private EditText nameEt;
    private EditText descriptionEt;
    private TextView dateTv;
    private TextView startTimeTv;
    private CheckBox notifyCb;
    private TextView priorityTv;
    private Spinner prioritySpinner;
    private CheckBox completedCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Resources r = this.getResources();

        // get the task information from the CalendarFragment's intent
        Intent i = this.getIntent();
        String json = i.getStringExtra("com.example.studymuffin.task");
        Type collectionType = new TypeToken<Task>(){}.getType();
        Task task = new Gson().fromJson(json, collectionType);

        this.nameEt = this.findViewById(R.id.name_et);
        this.descriptionEt = this.findViewById(R.id.description_et);
        this.dateTv = this.findViewById(R.id.date_tv);
        this.startTimeTv = this.findViewById(R.id.start_time_tv);
        this.notifyCb = this.findViewById(R.id.notify_cb);
        this.priorityTv = this.findViewById(R.id.priority_tv);
        this.prioritySpinner = this.findViewById(R.id.priority_spinner);
        this.completedCb = this.findViewById(R.id.completed_cb);

        this.nameEt.setText(task.getName());
        this.descriptionEt.setText(task.getDescription());
        // sets the date and trims it to only contain the day of week and date
        this.dateTv.setText(this.trimDateStr(task.getDate()));
        this.startTimeTv.setText(this.getAmPmFormat(task.getStartTimeHour(), task.getStartTimeMinute()));
        this.notifyCb.setChecked(task.shouldNotify());
        this.priorityTv.setText(r.getString(R.string.priority) + ": " +
                task.getPriority().toString());
        this.completedCb.setChecked(task.isCompleted());

        // set up the priority spinner
        final String[] priorities = r.getStringArray(R.array.priority_array);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        task.setPriority(Priority.LOW);
                        break;
                    case 1:
                        task.setPriority(Priority.MEDIUM);
                        break;
                    case 2:
                        task.setPriority(Priority.HIGH);
                        break;
                }

                // save the new priority
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * trims the date to just have the day of week, month, day of month, and year
     * @param date the task's date
     * @return a formatted string representation of the date
     */
    private String trimDateStr(Date date) {
        DateFormat df = new SimpleDateFormat("E, MMM dd, yyyy");

        return df.format(date);
    }

    private String getAmPmFormat(int hour, int minute) {
        String time = "";
        // the hour passed in is in 24 hour format so convert the hour to am pm format
        // TODO fix bug where the hour doesn't display when the hour is 12
        int convertedHour = (hour > 12) ? hour - 12 : hour;

        if (convertedHour < 10) {
            time += "0" + convertedHour;
        }

        time += ":";

        if (minute < 10) {
            time += "0" + minute;
        }

        time += (hour < 12) ? " AM" : " PM";

        return time;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present
        this.getMenuInflater().inflate(R.menu.menu_task, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editItem = menu.findItem(R.id.edit_item);
        MenuItem saveItem = menu.findItem(R.id.save_item);
        MenuItem cancelItem = menu.findItem(R.id.cancel_item);



        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_item) {

        }

        return super.onOptionsItemSelected(item);
    }
}