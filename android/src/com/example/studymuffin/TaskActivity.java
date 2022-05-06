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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private View view;
    public static TaskActivity.TaskCardAdapter cardAdapter;
    public static boolean isCardSelected = false;
    public static boolean isSearching = false;
    public static int selectedCardPosition;

    public static final String GOAL_FILE = "com.example.studymuffin.goal_file";

    public FloatingActionButton addGoal;
    private String m_Text;

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

        ArrayList<Goal> goalList = new ArrayList<>();
        cardAdapter = new TaskCardAdapter(goalList);
        makeRecyclerView();

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

        addGoal = this.findViewById(R.id.add_goal);

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
            this.goalList.add(goal);

            this.notifyItemInserted(this.getItemCount() + 1);


            //saveTask(context, task)
            saveGoalList(TaskActivity.this, this.goalList);
        }

        public void removeCard() {
            this.goalList.remove(selectedCardPosition);

            this.notifyItemRemoved(selectedCardPosition);
            this.notifyItemChanged(selectedCardPosition, this.getItemCount());

            //saveTask(context, task)
            saveGoalList(TaskActivity.this, this.goalList);
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