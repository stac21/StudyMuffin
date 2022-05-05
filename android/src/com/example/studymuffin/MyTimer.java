package com.example.studymuffin;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyTimer {
    private CountDownTimer timer;
    private boolean isRunning;
    private boolean isPaused;
    private int mInterval;
    private static final int UPDATES_PER_SECOND = 50;
    private static final int DELAY = 1000 / UPDATES_PER_SECOND;
    private EditText hoursET;
    private EditText minutesET;
    private EditText secondsET;
    private View view;
    private boolean isFinished;
    private boolean isExited;
    private Profile profile;

    public MyTimer(View view) {
        this.view = view;
        this.hoursET = this.view.findViewById(R.id.hours_et);
        this.minutesET = this.view.findViewById(R.id.minutes_et);
        this.secondsET = this.view.findViewById(R.id.seconds_et);
        this.isRunning = false;
        this.isPaused = false;
        this.isFinished = true;
        this.isExited = false;
        this.profile = new Profile("Dave", "Smith", 0, 0);
    }

    public void start() {
        this.isRunning = true;
        this.isPaused = false;
        this.isFinished = false;

        this.timer = new CountDownTimer(mInterval, DELAY) {
            @Override
            public void onTick(long millisUntilFinished) {
                String[] time = getHourMinSecFormat(mInterval);

                hoursET.setText(time[0]);
                minutesET.setText(time[1]);
                secondsET.setText(time[2]);

                mInterval -= DELAY;
            }

            @Override
            public void onFinish() {
                Context context = view.getContext();
                String initialTime = context.getString(R.string.timer_initial);

                hoursET.setText(initialTime);
                minutesET.setText(initialTime);
                secondsET.setText(initialTime);

                Toast.makeText(context, context.getString(R.string.timer_finished),
                        Toast.LENGTH_SHORT).show();

                Button startButton = view.findViewById(R.id.button_start_stop);
                Button resetButton = view.findViewById(R.id.button_reset);

                startButton.setText(context.getString(R.string.start_button));
                resetButton.setVisibility(View.INVISIBLE);

                isFinished = true;

                profile.addPoints(10);
            }
        }.start();
    }

    public void stop() {
        if (this.isRunning) {
            this.timer.cancel();

            this.isPaused = true;
            this.isRunning = false;
        }
    }

    public void reset(int initialInterval) {
        this.stop();

        Button startButton = view.findViewById(R.id.button_start_stop);
        Button resetButton = view.findViewById(R.id.button_reset);

        String[] time = getHourMinSecFormat(initialInterval);

        for (int i = 0; i < time.length; i++) {
            System.out.println(time[i]);
        }

        hoursET.setText(time[0]);
        minutesET.setText(time[1]);
        secondsET.setText(time[2]);

        this.mInterval = initialInterval;

        startButton.setText(view.getContext().getString(R.string.start_button));
        resetButton.setVisibility(View.INVISIBLE);
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    private String[] getHourMinSecFormat(int interval) {
        String hours = this.mInterval / (DELAY * UPDATES_PER_SECOND) / 3600 + "";
        String minutes = this.mInterval / (DELAY * UPDATES_PER_SECOND) / 60 % 60 + "";
        String seconds = this.mInterval / (DELAY * UPDATES_PER_SECOND) % 60 + "";

        // if hours, minutes or seconds is one digit
        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return new String[] {
            hours, minutes, seconds
        };
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public boolean isExited() {
        return this.isExited;
    }

    public void setIsExited(boolean isExited) {
        this.isExited = isExited;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public int getInterval() {
        return this.mInterval;
    }
}
