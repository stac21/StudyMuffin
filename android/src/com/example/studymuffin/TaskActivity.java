package com.example.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
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
    // the task that is currently being viewed by the user
    private Task task;

    private View view;
    public static TaskActivity.TaskCardAdapter cardAdapter;
    public static boolean isCardSelected = false;
    public static boolean isSearching = false;
    public static int selectedCardPosition;

    public static final String GOAL_FILE = "com.example.studymuffin.goal_file";
    // the amount of points to reward the user when they finish a task
    public static final int NUM_POINTS = 1000;

    public FloatingActionButton addGoal;
    private String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Resources r = this.getResources();
        final Context context = TaskActivity.this;

        // get the task information from the CalendarFragment's intent
        Intent i = this.getIntent();
        String json = i.getStringExtra("com.example.studymuffin.task");
        this.task = CalendarFragment.convertJsonToTask(json);

        ArrayList<Goal> goalList = task.getGoals();

        for (Goal g : goalList) {
            System.out.println(g.getName());
        }
        cardAdapter = new TaskCardAdapter(goalList);
        makeRecyclerView();

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
        this.taskTypeTv.append(": " + task.getTaskType().toString());
        // sets the date and trims it to only contain the day of week and date
        this.dateTv.setText(this.trimDateStr(task.getDate()));
        this.startTimeTv.setText(getAmPmFormat(task.getStartTimeHour(), task.getStartTimeMinute()));
        this.notifyCb.setChecked(task.shouldNotify());
        this.priorityTv.setText(r.getString(R.string.priority) + ": " +
                task.getPriority().toString());
        this.completedCb.setChecked(task.isCompleted());
        
        addGoal = this.findViewById(R.id.add_goal);

        this.notifyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                task.setNotify(isChecked);

                CalendarFragment.saveTask(context, task);
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
                                completedCb.setChecked(false);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setTitle(R.string.add_points);
                        dialog.setCanceledOnTouchOutside(false);
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

                                                    MainActivity.profile.addPoints(NUM_POINTS);
                                                    MainActivity.profile.save(context);
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

                                                    MainActivity.profile.addPoints(NUM_POINTS);
                                                    MainActivity.profile.save(context);
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

        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fabView) {
                Context context = fabView.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(addGoal.getContext());
                View viewInflated = LayoutInflater.from(addGoal.getContext()).inflate(R.layout.dialog, (ViewGroup) addGoal.getRootView(), false);

                final EditText input = (EditText) viewInflated.findViewById(R.id.name_et);
                viewInflated.findViewById(R.id.classSpinnerTextView).setVisibility(View.INVISIBLE);
                viewInflated.findViewById(R.id.classSpinner).setVisibility(View.INVISIBLE);

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        m_Text = input.getText().toString();
                        Goal g = new Goal(m_Text);
                        cardAdapter.addCard(g);
                        Log.i("add goal", m_Text);

                        for (Goal goal : task.getGoals()) {
                            System.out.println(goal.getName());
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    /**
     * trims the date to just have the day of week, month, day of month, and year
     * @param date the task's date
     * @return a formatted string representation of the date
     */
    private static String trimDateStr(Date date) {
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

    //Start of Goal/Subtasks code
    public void makeRecyclerView() {
        RecyclerView recyclerView = TaskActivity.this.findViewById(R.id.recyclerViewGoals);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        protected CheckBox checkBox;

        public CardViewHolder(View v) {
            super(v);

            this.checkBox = (CheckBox) v.findViewById(R.id.todoCheckBox);
            this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedCardPosition = getAdapterPosition();

                        Goal goal = TaskActivity.cardAdapter.getGoal(selectedCardPosition);
                        goal.setCompleted(true);

                        System.out.println("Card at " + selectedCardPosition + " clicked");
                        TaskActivity.cardAdapter.removeCard();
                    }
                }
            });
        }

    }


    public class TaskCardAdapter extends RecyclerView.Adapter<TaskActivity.CardViewHolder> {
        private ArrayList<Goal> goalList;

        public TaskCardAdapter(ArrayList<Goal> goalList) {
            if (goalList != null) {
                this.goalList = goalList;
            } else {
                this.goalList = new ArrayList<>();
            }
        }

        @Override
        public int getItemCount() {
            return this.goalList.size();
        }

        public Goal getGoal(int position) {
            return this.goalList.get(position);
        }

        public ArrayList<Goal> getGoalList() {
            return this.goalList;
        }

        public void setGoalList(ArrayList<Goal> goalList) {
            int prevSize = this.goalList.size();

            this.goalList = goalList;

            this.notifyItemRangeRemoved(0, prevSize);
            this.notifyItemRangeInserted(0, goalList.size());
        }

        public void onBindViewHolder(TaskActivity.CardViewHolder v, int i) {
            Goal goal = this.goalList.get(i);

            v.checkBox.setText(goal.getName());
        }

        public TaskActivity.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.task_layout, parent, false);

            return new TaskActivity.CardViewHolder(itemView);
        }

        public void addCard(Goal goal) {
            //this.goalList.add(goal);

            this.notifyItemInserted(this.getItemCount() + 1);

            task.addToGoals(goal);
            CalendarFragment.saveTask(TaskActivity.this, task);
        }

        public void removeCard() {
            Goal goal = this.goalList.get(selectedCardPosition);

            this.goalList.remove(selectedCardPosition);

            this.notifyItemRemoved(selectedCardPosition);
            this.notifyItemChanged(selectedCardPosition, this.getItemCount());

            task.removeFromGoals(goal);
            CalendarFragment.saveTask(TaskActivity.this, task);
        }
    }
    public static void saveGoalList(Context context, ArrayList<Goal> goalList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(goalList);

        editor.putString(GOAL_FILE, json);

        editor.apply();
    }
}