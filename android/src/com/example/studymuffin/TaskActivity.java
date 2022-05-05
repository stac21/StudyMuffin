package com.example.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private TextView taskTypeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.setThemeOfApp(this);
        setContentView(R.layout.activity_task);

        Resources r = this.getResources();

        final Context context = TaskActivity.this;

        // get the task information from the CalendarFragment's intent
        Intent i = this.getIntent();
        String json = i.getStringExtra("com.example.studymuffin.task");
        final Task task = CalendarFragment.convertJsonToTask(json);

        this.nameEt = this.findViewById(R.id.name_et);
        this.descriptionEt = this.findViewById(R.id.description_et);
        this.taskTypeTv = this.findViewById(R.id.task_type_tv);
        this.dateTv = this.findViewById(R.id.date_tv);
        this.startTimeTv = this.findViewById(R.id.start_time_tv);
        this.notifyCb = this.findViewById(R.id.notify_cb);
        this.priorityTv = this.findViewById(R.id.priority_tv);
        this.prioritySpinner = this.findViewById(R.id.priority_spinner);
        this.completedCb = this.findViewById(R.id.completed_cb);

        this.nameEt.setText(task.getName());
        this.descriptionEt.setText(task.getDescription());
        this.taskTypeTv.setText(this.taskTypeTv.getText().toString() + ": " +
                task.getTaskType().toString());
        // sets the date and trims it to only contain the day of week and date
        this.dateTv.setText(this.trimDateStr(task.getDate()));
        this.startTimeTv.setText(this.getAmPmFormat(task.getStartTimeHour(), task.getStartTimeMinute()));
        this.notifyCb.setChecked(task.shouldNotify());
        this.priorityTv.setText(r.getString(R.string.priority) + ": " +
                task.getPriority().toString());
        this.completedCb.setChecked(task.isCompleted());

        this.notifyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                task.setNotify(isChecked);
            }
        });

        if (task instanceof Assignment || task instanceof Assessment) {
            completedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = getLayoutInflater().inflate(R.layout.checked_dialog, null);

                        final EditText pointsEarnedEt = v.findViewById(R.id.points_earned_et);

                        builder.setTitle(R.string.add_points);
                        builder.setView(v);
                        builder.setCancelable(true);

                        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setTitle(R.string.add_points);
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String pointsEarnedStr = pointsEarnedEt.getText().toString();

                                        if (pointsEarnedStr.length() != 0) {
                                            float points = Float.parseFloat(pointsEarnedStr);

                                            if (task instanceof Assignment) {
                                                Assignment a = (Assignment) task;

                                                if (points <= a.getPointsPossible()) {
                                                    a.setPointsEarned(points);

                                                    CalendarFragment.saveTask(context, a);

                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(context,
                                                            R.string.points_earned_greater,
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Assessment a = (Assessment) task;

                                                if (points <= a.getPointsPossible()) {
                                                    a.setPointsEarned(points);

                                                    CalendarFragment.saveTask(context, a);

                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(context,
                                                            R.string.points_earned_greater,
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, R.string.empty_points_earned,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                        dialog.show();
                    }
                }
            });
        }

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