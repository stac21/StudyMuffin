package com.example.studymuffin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MyNotification {
    private NotificationCompat.Builder builder;

    private static final String CHANNEL_ID = "com.example.studymuffin.channel_id";
    private static final String CHANNEL_NAME = "com.example.studymuffin.channel_name";
    private static int uniqueID = 1;

    public MyNotification(Context context, Task task) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("com.example.studymuffin.channel_id",
                "com.example.studymuffin.channel_name", NotificationManager.IMPORTANCE_DEFAULT);

        nm.createNotificationChannel(channel);

        this.builder = new NotificationCompat.Builder(context,
                "com.example.studymuffin.channel_id");

        this.builder.setAutoCancel(true);
        this.builder.setSmallIcon(R.drawable.ic_cake_white_24dp);
        this.builder.setTicker(task.getName() + ": " + task.getDescription());

        Calendar calendar = Calendar.getInstance();

        System.out.println("calendar time: " + calendar.getTime());

        //this.builder.setWhen(calendar.getTimeInMillis());

        this.builder.setContentTitle(task.getName());

        this.builder.setContentText(task.getDescription());
        this.builder.setPriority(NotificationCompat.PRIORITY_MIN);

        this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        this.builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        nm.notify(uniqueID++, this.builder.build());
    }
}
