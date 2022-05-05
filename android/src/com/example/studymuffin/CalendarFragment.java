package com.example.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {
    private View view;
    private static final int LECTURE_DAY_COLOR = Color.CYAN;
    private static final int ASSIGNMENT_DAY_COLOR = Color.RED;
    private ArrayList<CourseInfo> courseList;
    private static CompactCalendarView calendarView;
    public static CalendarCardAdapter cardAdapter;
    public static boolean isCardSelected = false;
    public static boolean isSearching = false;
    public static int selectedCardPosition;

    public static final String TASK_FILE = "com.example.studymuffin.task_file";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Context context = this.view.getContext();
        this.setHasOptionsMenu(true);

        final FloatingActionButton fab = view.findViewById(R.id.calendar_fab);
        final TextView monthLabel = view.findViewById(R.id.calendar_month_label);
        calendarView = view.findViewById(R.id.compact_calendar_view);

        // TODO: save the tasks into the course list
        ArrayList<Task> list = loadTaskList(context);

        cardAdapter = new CalendarCardAdapter(list);

        this.makeRecyclerView();

        Resources r = getResources();

        final String[] months = r.getStringArray(R.array.months_array);

        courseList = ClassFragment.loadCourseList(context);

        if (courseList == null) {
            courseList = new ArrayList<>();
        }

        ArrayList<String> courseTitles = new ArrayList<>();

        for (int i = 0; i < courseList.size(); i++) {
            courseTitles.add(courseList.get(i).getTitle());
        }

        final String[] courseNamesArr = new String[courseTitles.size()];
        courseTitles.toArray(courseNamesArr);

        // set the month label to the current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        monthLabel.setText(months[currentMonth]);
        populateCalendar(courseList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fabView) {
                Context context = fabView.getContext();
                TaskSelectionDialog selectionDialog = new TaskSelectionDialog(context);
                AlertDialog alertDialog = selectionDialog.getAlertDialog();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (courseList.size() == 0) {
                            Toast.makeText(context, r.getString(R.string.create_course),
                                    Toast.LENGTH_LONG).show();

                            return;
                        }
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int checkedId = selectionDialog.getCheckedId();

                                if (checkedId == R.id.assignment_rb) {
                                    AssignmentDialog assignmentDialog = new AssignmentDialog(
                                            context, courseNamesArr);
                                    AlertDialog dialog = assignmentDialog.getAlertDialog();

                                    dialog.show();
                                } else if (checkedId == R.id.assessment_rb) {
                                    AssessmentDialog assessmentDialog = new AssessmentDialog(
                                            context, courseNamesArr);
                                    AlertDialog dialog = assessmentDialog.getAlertDialog();

                                    dialog.show();
                                } else if (checkedId == R.id.physical_meeting_rb) {
                                    PhysicalMeetingDialog physicalDialog = new
                                            PhysicalMeetingDialog(context, courseNamesArr);
                                    AlertDialog dialog = physicalDialog.getAlertDialog();

                                    dialog.show();
                                } else if (checkedId == R.id.virtual_meeting_rb) {
                                    VirtualMeetingDialog virtualDialog = new
                                            VirtualMeetingDialog(context, courseNamesArr);
                                    AlertDialog dialog = virtualDialog.getAlertDialog();

                                    dialog.show();
                                }

                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });

        return view;
    }

    /**
     * populates the calendar with all of the month's tasks
     * TODO make this efficient so that it only checks the current month's tasks
     * @param courseList the list of courses
     */
    public void populateCalendar(ArrayList<CourseInfo> courseList) {
        ArrayList<Task> taskList;

        for (int i = 0; i < courseList.size(); i++) {
            taskList = courseList.get(i).getTaskList();

            for (int j = 0; j < taskList.size(); j++) {
                System.out.println("Date: " + taskList.get(j).getDate());
                System.out.println("Date in epoch: " + taskList.get(j).getDate().getTime());

                addTaskToCalendar(taskList.get(j));
            }
        }
    }

    /**
     * add's a dot on the calendar for the date of the task
     * @param task the task to add to the calendar view
     */
    public static void addTaskToCalendar(Task task) {
        calendarView.addEvent(new Event(ASSIGNMENT_DAY_COLOR,
                task.getDate().getTime(), task.getName()));
    }

    private int getDayOfYear(int day, int month) {
        int total = 0;

        for (int i = 0; i < month; i++) {
            total += this.getDaysInMonth(i);
        }

        total += day;

        return total;
    }

    /**
     * returns the number of days in the month
     * @param month the current month represented as an int
     * @return the number of days in the given month
     */
    private byte getDaysInMonth(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return 31;
            case Calendar.FEBRUARY:
                return 28;
            case Calendar.MARCH:
                return 31;
            case Calendar.APRIL:
                return 30;
            case Calendar.MAY:
                return 31;
            case Calendar.JUNE:
                return 30;
            case Calendar.JULY:
                return 31;
            case Calendar.AUGUST:
                return 31;
            case Calendar.SEPTEMBER:
                return 30;
            case Calendar.OCTOBER:
                return 31;
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.DECEMBER:
                return 31;
            default:
                return 0;
        }
    }

    private String getDayOfWeekName(int num) {
        switch (num) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "";
        }
    }

    public void makeRecyclerView() {
        RecyclerView recyclerView = this.view.findViewById(R.id.recyclerViewCalendar);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {
        protected CheckBox checkBox;

        public CardViewHolder(View v) {
            super(v);

            this.checkBox = (CheckBox) v.findViewById(R.id.todoCheckBox);
            this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedCardPosition = getAdapterPosition();

                        Task task = CalendarFragment.cardAdapter.getTask(selectedCardPosition);
                        task.setCompleted(true);

                        System.out.println("Card at " + selectedCardPosition + " clicked");
                        CalendarFragment.cardAdapter.removeCard();
                    }
                }
            });

            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), TaskActivity.class);
            Task task = cardAdapter.getTask(this.getLayoutPosition());
            selectedCardPosition = this.getAdapterPosition();

            i.putExtra("com.example.studymuffin.task", new Gson().toJson(task));

            v.getContext().startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            isCardSelected = true;
            selectedCardPosition = this.getAdapterPosition();

            CalendarFragment.this.getActivity().invalidateOptionsMenu();

            return true;
        }
    }

    public class CalendarCardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private ArrayList<Task> taskList;

        public CalendarCardAdapter(ArrayList<Task> taskList) {
            if (taskList != null) {
                this.taskList = taskList;
            } else {
                this.taskList = new ArrayList<>();
            }
        }

        @Override
        public int getItemCount() {
            return this.taskList.size();
        }

        public Task getTask(int position) {
            return this.taskList.get(position);
        }

        public ArrayList<Task> getTaskList() {
            return this.taskList;
        }

        public void setTaskList(ArrayList<Task> taskList) {
            int prevSize = this.taskList.size();

            this.taskList = taskList;

            this.notifyItemRangeRemoved(0, prevSize);
            this.notifyItemRangeInserted(0, taskList.size());
        }

        public void onBindViewHolder(CardViewHolder v, int i) {
            Task task = this.taskList.get(i);

            v.checkBox.setText(task.getName());
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.task_layout, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(Task task) {
            this.taskList.add(task);

            this.notifyItemInserted(this.getItemCount() + 1);

            saveTaskList(view.getContext(), this.taskList);
        }

        public void removeCard() {
            this.taskList.remove(selectedCardPosition);

            this.notifyItemRemoved(selectedCardPosition);
            this.notifyItemChanged(selectedCardPosition, this.getItemCount());

            saveTaskList(view.getContext(), this.taskList);
        }

        /**
         * filters the list so that only the tasks with the search query in their name
         * are displayed within the recyclerview
         * @param searchQuery the string to filter the list by
         */
        public void filterList(String searchQuery) {
            // search the task names to see if they contain the search query
            ArrayList<Task> filteredTasks = new ArrayList<>();

            for (int i = 0; i < this.taskList.size(); i++) {
                if (this.taskList.get(i).getName().contains(searchQuery)) {
                    filteredTasks.add(this.taskList.get(i));
                }
            }

            this.setTaskList(filteredTasks);
        }
    }

    public static ArrayList<Task> loadTaskList(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(TASK_FILE, null);

        Type collectionType = new TypeToken<ArrayList<Task>>(){}.getType();
        ArrayList<Task> taskList = new Gson().fromJson(json, collectionType);

        return taskList;
    }

    public static void saveTaskList(Context context, ArrayList<Task> taskList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(taskList);

        editor.putString(TASK_FILE, json);

        editor.apply();
    }
}
