package com.example.studymuffin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        System.out.println("Alarm received with action = " + action);

        if (action.equals(MyNotification.ALARM_ACTION)) {
            System.out.println("Notification's broadcast was received");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notify = sp.getBoolean("notification_switch", true);

            if (notify) {
                Task task = null;
                Type collectionType;
                int taskType = intent.getIntExtra(MyNotification.TASK_TYPE,
                        MyNotification.ASSIGNMENT);
                // check to see whether the task has been deleted
                boolean isInTaskList = false;

                try {
                    if (taskType == MyNotification.ASSIGNMENT) {
                        collectionType = new TypeToken<Assignment>(){}.getType();
                    } else if (taskType == MyNotification.ASSESSMENT) {
                        collectionType = new TypeToken<Assessment>(){}.getType();
                    } else if (taskType == MyNotification.PHYSICAL_MEETING) {
                        collectionType = new TypeToken<PhysicalMeeting>(){}.getType();
                    } else {
                        collectionType = new TypeToken<VirtualMeeting>(){}.getType();
                    }
                    task = new Gson().fromJson(
                            intent.getStringExtra(MyNotification.ALARM_TASK),
                            collectionType
                    );

                    ArrayList<Task> taskList = CalendarFragment.loadTaskList(context);
                    for (Task t : taskList) {
                        if (task.getUniqueId() == t.getUniqueId()) {
                            isInTaskList = true;
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("For some reason the task cannot be created");
                }

                if (task != null && task.shouldNotify() && isInTaskList) {
                    new MyNotification(context, task);
                } else if (!task.shouldNotify()) {
                    System.out.println("The user opted not to be notified of that task");
                } else {
                    System.out.println("Task was null");
                }
            } else {
                System.out.println("The user has opted not to receive notifications");
            }
        } else if (action.equals(MyNotification.CLICKED_ACTION)) {
            Intent i = new Intent(context, TaskActivity.class);
            i.putExtra(MyNotification.TASK_INTENT,
                    intent.getStringExtra(MyNotification.TASK_INTENT));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        } else if (action.equals(MyNotification.CHECK_ACTION)) {
            // TODO check off the task as complete and dismiss the notification
        }
    }
}
