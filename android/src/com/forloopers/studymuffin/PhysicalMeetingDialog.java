package com.forloopers.studymuffin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PhysicalMeetingDialog {
    private AlertDialog alertDialog;
    private Task mTask;
    ArrayList<CourseInfo> courseList;
    private static int selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;
    private static Date selectedDate;
    private static Calendar date;
    private static final int CUSTOM_DATE_POS = 8;
    private static final int YEAR_OFFSET = 1900;
    private static int selectedClassIndex = 0;

    public PhysicalMeetingDialog(Context context, String[] courseNamesArr) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        View v = LayoutInflater.from(context).inflate(R.layout.physical_meeting_dialog, null);
        Resources r = context.getResources();

        final EditText nameET = v.findViewById(R.id.name_et);
        final EditText descriptionET = v.findViewById(R.id.descriptionET);
        final EditText locationET = v.findViewById(R.id.location_et);
        final CheckBox notifyCB = v.findViewById(R.id.calendar_checkbox);
        final CheckBox recurringCB = v.findViewById(R.id.recurring_cb);

        final String[] times = r.getStringArray(R.array.time_spinner_array);
        final String[] days = r.getStringArray(R.array.day_spinner_array);

        Spinner classSpinner = (Spinner) v.findViewById(R.id.classSpinner);
        classSpinner.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, courseNamesArr));

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClassIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner startTimeSpinner = (Spinner) v.findViewById(R.id.startTimeSpinner);
        startTimeSpinner.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, times));

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = currentTime.get(Calendar.MINUTE);

                    final TimePickerDialog tmDialog = new TimePickerDialog(
                            view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int userMinute) {
                            selectedStartHour = hourOfDay;
                            selectedStartMinute = userMinute;
                        }
                    }, hour, minute, true);

                    tmDialog.setTitle("Select Time");
                    tmDialog.show();
                } else {
                    selectedStartHour = Integer.parseInt(times[position].substring(0, 2));
                    selectedStartMinute = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner endTimeSpinner = (Spinner) v.findViewById(R.id.endTimeSpinner);
        endTimeSpinner.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, times));

        endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = currentTime.get(Calendar.MINUTE);

                    final TimePickerDialog tmDialog = new TimePickerDialog(
                            view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int userMinute) {
                            selectedEndHour = hourOfDay;
                            selectedEndMinute = userMinute;
                        }
                    }, hour, minute, true);

                    tmDialog.setTitle("Select Time");
                    tmDialog.show();
                } else {
                    selectedEndHour = Integer.parseInt(times[position].substring(0, 2));
                    selectedEndMinute = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner daySpinner = (Spinner) v.findViewById(R.id.daySpinner);
        daySpinner.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, days));

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO can check the position to see what day the user chose and
                if (position == CUSTOM_DATE_POS) {
                    final DatePickerDialog dpDialog = new DatePickerDialog(
                            view.getContext());

                    dpDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            System.out.println("Year: " + year + " Month: " + month + " day: " + dayOfMonth);

                            selectedDate = new Date(year - YEAR_OFFSET, month, dayOfMonth);
                        }
                    });

                    dpDialog.setTitle("Select Date");
                    dpDialog.show();
                } else {
                    date = Calendar.getInstance();
                    date.set(Calendar.WEEK_OF_MONTH, date.get(Calendar.WEEK_OF_MONTH) + 1);
                    date.set(Calendar.DAY_OF_WEEK, position + 1);

                    selectedDate = date.getTime();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialog.setTitle(R.string.task_dialog_title);
        dialog.setView(v);
        // may have to set this to false
        dialog.setCancelable(true);
        final View fView = v;

        dialog.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        this.alertDialog = dialog.create();
        alertDialog.setTitle("Create Task");
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create task
                        String name = nameET.getText().toString();
                        String description = descriptionET.getText().toString();
                        String locationStr = locationET.getText().toString();
                        // TODO: make a spinner to choose the priority of the task
                        Priority priority = Priority.LOW;
                        Date currentDate = Calendar.getInstance().getTime();

                        date.set(Calendar.HOUR_OF_DAY, selectedStartHour);
                        date.set(Calendar.MINUTE, selectedStartMinute);
                        date.set(Calendar.SECOND, 0);

                        selectedDate = date.getTime();

                        Context context = fView.getContext();

                        if (name.length() != 0 && description.length() != 0 &&
                            locationStr.length() != 0 && selectedDate.compareTo(currentDate) > 0) {
                            Task task = new PhysicalMeeting(name, description, selectedDate,
                                    selectedStartHour, selectedStartMinute, notifyCB.isChecked(),
                                    recurringCB.isSelected(), selectedEndHour, selectedEndMinute,
                                    locationStr, priority, ClassFragment.getCourseAtIndex(context,
                                    selectedClassIndex).getUniqueId());

                            Toast.makeText(context, "Task created", Toast.LENGTH_SHORT).show();

                            // TODO: add the task to the selected course
                            CalendarFragment.addTaskToCalendar(task);

                            CalendarFragment.cardAdapter.addCard(task, selectedClassIndex);

                            MyNotification.registerAlarm(context, task);

                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, context.getString(R.string.empty_fields),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public AlertDialog getAlertDialog() {
        return this.alertDialog;
    }
}
