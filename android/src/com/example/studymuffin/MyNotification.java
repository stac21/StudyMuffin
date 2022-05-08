package com.example.studymuffin;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Calendar;

public class MyNotification {
    private NotificationCompat.Builder builder;

    public static final String CHANNEL_ID = "com.example.studymuffin.channel_id";
    public static final String CHANNEL_NAME = "com.example.studymuffin.channel_name";
    public static final String ALARM_ACTION = "com.example.studymuffin.notification";
    public static final String ALARM_TASK = "com.example.studymuffin.alarmTask";
    public static final String CLICKED_ACTION = "com.example.studymuffin.clicked_action";
    public static final String TASK_INTENT = "com.example.studymuffin.task";
    public static final String CHECK_INTENT = "com.example.studymuffin.check_intent";
    public static final String CHECK_ACTION = "com.example.studymuffin.check_intent_action";
    // is used in the AlarmReceiver class to distinguish what subclass of Task we are using
    public static final String TASK_TYPE = "com.example.studymuffin.taskType";
    public static final int ASSIGNMENT = 0;
    public static final int ASSESSMENT = 1;
    public static final int PHYSICAL_MEETING = 2;
    public static final int VIRTUAL_MEETING = 3;
    public static final int CHECK_REQUEST_CODE = 0;
    public static final int TASK_REQUEST_CODE = 1;

    public MyNotification(Context context, Task task) {
        String json = new Gson().toJson(task);

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        nm.createNotificationChannel(channel);

        Intent taskIntent = new Intent(context, AlarmReceiver.class);
        taskIntent.putExtra(TASK_INTENT, json);
        taskIntent.setAction(CLICKED_ACTION);
        PendingIntent taskPi = PendingIntent.getBroadcast(context, TASK_REQUEST_CODE, taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        this.builder = new NotificationCompat.Builder(context,
                CHANNEL_ID);

        this.builder.setAutoCancel(true);
        this.builder.setSmallIcon(R.drawable.ic_cake_white_24dp);
        this.builder.setTicker(task.getName() + ": " + task.getDescription());

        Calendar calendar = Calendar.getInstance();

        System.out.println("calendar time: " + calendar.getTime());

        //this.builder.setWhen(calendar.getTimeInMillis());

        this.builder.setContentTitle(task.getName());

        this.builder.setContentText(task.getDescription());
        this.builder.setContentIntent(taskPi);
        // TODO set this value to the value of the task's priority value
        this.builder.setPriority(NotificationCompat.PRIORITY_MIN);

        this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        this.builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        nm.notify(task.getUniqueId(), this.builder.build());
    }

    public static void registerAlarm(Context context, Task task) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra(ALARM_TASK, new Gson().toJson(task));
        i.setAction(ALARM_ACTION);

        int taskType;

        if (task instanceof Assignment) {
            taskType = ASSIGNMENT;
        } else if (task instanceof Assessment) {
            taskType = ASSESSMENT;
        } else if (task instanceof PhysicalMeeting) {
            taskType = PHYSICAL_MEETING;
        } else {
            taskType = VIRTUAL_MEETING;
        }

        i.putExtra(TASK_TYPE, taskType);

        PendingIntent pi = PendingIntent.getBroadcast(context, task.getUniqueId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, task.getStartTimeHour());
        calendar.set(Calendar.MINUTE, task.getStartTimeMinute());
        // must set the second to zero else we'll get instances where the notification goes off late
        calendar.set(Calendar.SECOND, 0);

        // get the user's preferred offset
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString("notification_offsets", "-1");
        String[] values = context.getResources().getStringArray(R.array.notification_offsets_values);
        int offset = MyNotification.getOffsetInMillis(value, values);

        // subtract the offset time
        calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);

        System.out.println("Offset time: " + offset);

        System.out.println("Calendar time in MyNotification: " + calendar.getTime());

        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        /*
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);

         */
    }

    private static int getOffsetInMillis(String value, String[] values) {
        if (value.equals(values[0])) {
            return 0;
        } else if (value.equals(values[1])) {
            // 10 minutes in milliseconds
            return 10 * 60 * 1000;
        } else if (value.equals(values[2])) {
            // 30 minutes in milliseconds
            return 30 * 60 * 1000;
        } else {
            // 60 minutes in milliseconds
            return 60 * 60 * 1000;
        }
    }
}
