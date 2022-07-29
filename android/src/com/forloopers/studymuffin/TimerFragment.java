package com.forloopers.studymuffin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimerFragment extends Fragment {
    private View view;
    private int interval;
    private int initialInterval;
    private MyTimer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_timer, container, false);

        final EditText hoursET = this.view.findViewById(R.id.hours_et);
        final EditText minutesET = this.view.findViewById(R.id.minutes_et);
        final EditText secondsET = this.view.findViewById(R.id.seconds_et);
        final Button startStopButton = this.view.findViewById(R.id.button_start_stop);
        final Button resetButton = this.view.findViewById(R.id.button_reset);

        this.timer = new MyTimer(this.view);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                // if the user is starting the timer
                if (startStopButton.getText().toString().equals(context.getString(
                        R.string.start_button))) {
                    startStopButton.setText(context.getString(R.string.pause_button));
                    resetButton.setVisibility(View.VISIBLE);

                    if (timer.isPaused()) {
                        interval = timer.getInterval();
                    } else {
                        /*
                        if (timer.isFinished()) {
                            initialInterval = interval;
                        }

                         */

                        String hoursStr = hoursET.getText().toString();
                        String minutesStr = minutesET.getText().toString();
                        String secondsStr = secondsET.getText().toString();

                        int hours = (hoursStr.length() == 0) ? 0 : Integer.parseInt(hoursStr);
                        int minutes = (minutesStr.length() == 0) ? 0 : Integer.parseInt(minutesStr);
                        int seconds = (secondsStr.length() == 0) ? 0 : Integer.parseInt(secondsStr);

                        // this interval is expressed in milliseconds
                        interval = 1000 * (3600 * hours + 60 * minutes + seconds);
                        initialInterval = interval;

                        System.out.println("Hours = " + hours);
                        System.out.println("Minutes = " + minutes);
                        System.out.println("Seconds = " + seconds);

                        System.out.println("Interval = " + interval);
                    }

                    timer.setInterval(interval);
                    timer.start();
                }
                // if the user is pausing the timer
                else {
                    startStopButton.setText(context.getString(R.string.start_button));
                    resetButton.setVisibility(View.INVISIBLE);

                    timer.stop();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.reset(initialInterval);
            }
        });

        return this.view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.timer.isExited()) {
            Context context = this.view.getContext();

            Toast.makeText(context, context.getString(R.string.timer_exited),
                    Toast.LENGTH_SHORT).show();
        }

        System.out.println("OnResume called");
    }

    @Override
    public void onPause() {
        super.onPause();

        this.timer.stop();
        this.timer.setIsExited(true);

        System.out.println("OnPause called");
    }

    private String getMinSecFormat(int interval) {
        String minutes = this.interval / 60 + "";
        String seconds = this.interval % 60 + "";

        // if minutes or seconds is one digit
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return minutes + ":" + seconds;
    }
}
