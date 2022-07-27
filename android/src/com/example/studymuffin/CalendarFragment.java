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
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarFragment extends Fragment {
    private View view;

    public static final int ASSESSMENT_COLOR = Color.CYAN;
    public static final int ASSIGNMENT_COLOR = Color.RED;
    public static final int PHYSICAL_MEETING_COLOR = Color.GREEN;
    public static final int VIRTUAL_MEETING_COLOR = Color.YELLOW;
    public static ArrayList<CourseInfo> courseList;
    public static RecyclerView todoListRecyclerView;
    public static CompactCalendarView monthlyCalendarView;
    public static CalendarCardAdapter cardAdapter;
    public static boolean isCardSelected = false;
    public static int selectedCardPosition;
    public static SortPreference sortPreference;

    private static boolean loadFromDb = true;

    public static Profile profile = MainActivity.profile;

    public static final String TASK_FILE = "com.example.studymuffin.tasks_file";
    public static final String SORT_PREFERENCE_FILE = "com.example.studymuffin.sort_preference";
    public static final String TASK_ID_COUNTER_FILE = "com.example.studymuffin.task_id_counter_file";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Context context = this.view.getContext();
        this.setHasOptionsMenu(true);

        final FloatingActionButton fab = view.findViewById(R.id.calendar_fab);
        final TextView monthLabel = view.findViewById(R.id.calendar_month_label);

        monthlyCalendarView = view.findViewById(R.id.compact_calendar_view);

        if (sortPreference == null) {
            System.out.println("sortPreference is null");
            sortPreference = loadSortPreference(context);
            System.out.println("Finished loading sort preference");
        }

        cardAdapter = new CalendarCardAdapter(loadTaskList(context));

        courseList = ClassFragment.loadCourseList(context);

        this.makeRecyclerView();

        Resources r = getResources();

        final String[] months = r.getStringArray(R.array.months_array);

        final String[] courseNamesArr = new String[courseList.size()];

        for (int i = 0; i < courseNamesArr.length; i++) {
            courseNamesArr[i] = courseList.get(i).getTitle();
        }

        // set the month label to the current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        monthLabel.setText(months[currentMonth]);
        populateCalendar(cardAdapter.getTaskList());

        monthlyCalendarView.shouldScrollMonth(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fabView) {
                Context context = fabView.getContext();

                if (courseList.size() == 0) {
                    Toast.makeText(context, r.getString(R.string.create_course),
                            Toast.LENGTH_LONG).show();

                    return;
                }

                TaskSelectionDialog selectionDialog = new TaskSelectionDialog(context);
                AlertDialog alertDialog = selectionDialog.getAlertDialog();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

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

    @Override
    public void onResume() {
        super.onResume();
        
        cardAdapter.setTaskList(loadTaskList(view.getContext()));
    }

    /**
     * populates the calendar with all of the month's tasks
     * TODO make this efficient so that it only checks the current month's tasks
     * @param taskList the list of tasks
     */
    public void populateCalendar(ArrayList<Task> taskList) {
        for (int j = 0; j < taskList.size(); j++) {
            addTaskToCalendar(taskList.get(j));
        }
    }

    /**
     * add's a dot on the calendar for the date of the task
     * @param task the task to add to the calendar view
     */
    public static void addTaskToCalendar(Task task) {
        int color;

        if (task instanceof Assignment) {
            color = ASSIGNMENT_COLOR;
        } else if (task instanceof Assessment) {
            color = ASSESSMENT_COLOR;
        } else if (task instanceof PhysicalMeeting) {
            color = PHYSICAL_MEETING_COLOR;
        } else {
            color = VIRTUAL_MEETING_COLOR;
        }

        monthlyCalendarView.addEvent(new Event(color,
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
        todoListRecyclerView = this.view.findViewById(R.id.todo_list);
        todoListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this.getContext());
        todoListRecyclerView.setLayoutManager(llm);
        todoListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        todoListRecyclerView.setAdapter(cardAdapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {
        protected CheckBox checkBox;

        public CardViewHolder(View v) {
            super(v);

            this.checkBox = (CheckBox) v.findViewById(R.id.todoCheckBox);

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

            try {
                getActivity().invalidateOptionsMenu();
            } catch (NullPointerException e) {
                Toast.makeText(v.getContext(), "Try reloading the application",
                        Toast.LENGTH_SHORT).show();
            }

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
            v.checkBox.setChecked(task.isCompleted());
            v.checkBox.setClickable(false);
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.task_layout, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(Task task, int courseIndex) {
            this.taskList.add(task);

            this.notifyItemInserted(this.getItemCount() + 1);
            this.sort();
            // notify the recyclerview that the list has changed when the list is sorted
            this.notifyItemRangeChanged(0, this.getItemCount());

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

        /**
         * sorts the tasklist based on the user's preferred attribute using merge sort
         */
        public void sort() {
            this.mergeSort(0, this.getItemCount() - 1);
        }

        /**
         * sorts the task list by the attribute that the user has chosen in their sort preference.
         * @param p the starting index of the list
         * @param r the ending index of the list
         */
        public void mergeSort(int p, int r) {
            // if list's size > 1
            if (p < r) {
                int q = (p + r) / 2;

                mergeSort(p, q);
                mergeSort(q + 1, r);
                merge(p, q, r);
            }
        }

        /**
         * merge the two subarrays [p..q] and [q + 1..r]
         * @param p the start index of the merged subarray
         * @param q the middle index of the merged subarray
         * @param r the end index of the merged subarray
         */
        public void merge(int p, int q, int r) {
            int lowSize = q - p + 1;
            int highSize = r - q;
            Task[] lowHalf = new Task[lowSize];
            Task[] highHalf = new Task[highSize];
            int i = 0, j = 0, k = p;

            for (int n = 0; k <= q; n++, k++) {
                lowHalf[n] = this.taskList.get(k);
            }
            for (int n = 0; k <= r; n++, k++) {
                highHalf[n] = this.taskList.get(k);
            }

            k = p;

            while (i < lowSize && j < highSize) {
                Task lowVal = lowHalf[i];
                Task highVal = highHalf[j];

                if (lowVal.compareToByPreference(highVal) < 0) {
                    this.taskList.set(k++, lowVal);
                    i++;
                } else {
                    this.taskList.set(k++, highVal);
                    j++;
                }
            }

            while (i < lowSize) {
                this.taskList.set(k++, lowHalf[i++]);
            }
            while (j < highSize) {
                this.taskList.set(k++, highHalf[j++]);
            }
        }
    }

    /**
     * load the user's preference of how the todo list should be sorted
     * @param context the application's context
     * @return the user's sort preference
     */
    public static SortPreference loadSortPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(SORT_PREFERENCE_FILE, null);

        Type collectionType = new TypeToken<SortPreference>(){}.getType();
        SortPreference sortPreference = new Gson().fromJson(json, collectionType);

        if (sortPreference != null) {
            return sortPreference;
        } else {
            return SortPreference.DUE_DATE;
        }
    }

    /**
     * save the user's preference of how the todo list should be sorted
     * @param context the application's context
     */
    public static void saveSortPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(sortPreference);

        editor.putString(SORT_PREFERENCE_FILE, json);

        editor.apply();
    }

    /**
     * loads the task list
     * @param context the application's context
     * @return the loaded task list
     */
    public static ArrayList<Task> loadTaskList(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(TASK_FILE, null);
        ArrayList<Task> taskList = new ArrayList<>();

        if (MainActivity.firebaseUser != null && MainActivity.firebaseUser.getEmail() != null &&
                loadFromDb) {
            final ArrayList<Task> loadedTaskList = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ref = db.collection("Data").document("TaskData")
                    .collection(MainActivity.firebaseUser.getEmail());

            DocumentReference dataRef = ref.document(TASK_FILE);
            dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            if (data.get(TASK_ID_COUNTER_FILE) != null) {
                                Task.idCounter = Math.toIntExact((long)data.get(TASK_ID_COUNTER_FILE));
                            }

                            String dataJson = (String) data.get(TASK_FILE);
                            JsonArray jsonArray = new JsonParser().parse(dataJson).getAsJsonArray();
                            String currentElementJson;

                            for (int i = 0; i < jsonArray.size(); i++) {
                                currentElementJson = jsonArray.get(i).toString();

                                System.out.println(currentElementJson);

                                loadedTaskList.add(convertJsonToTask(currentElementJson));
                            }

                            loadFromDb = false;
                            saveTaskList(context, loadedTaskList);
                            CalendarFragment.cardAdapter.setTaskList(loadTaskList(context));

                            System.out.println("TaskList exists " + data);
                        } else {
                            System.out.println("No such document");
                        }
                    } else {
                        System.out.println("TaskList could not be retrieved" + task.getException());
                    }
                }
            });
        }
        if (json != null) {
            loadFromDb = true;
            JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
            String currentElementJson;

            System.out.println("JSONArray size = " + jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                currentElementJson = jsonArray.get(i).toString();

                System.out.println(currentElementJson);

                taskList.add(convertJsonToTask(currentElementJson));
            }
        }

        System.out.println("TaskList");

        for (int i = 0; i < taskList.size(); i++) {
            System.out.println(taskList.get(i));
        }

        return taskList;
    }

    /**
     * converts the json format of a task to its proper POJO
     * @param json the json format of a task
     * @return a task in its proper subclass of Task
     */
    public static Task convertJsonToTask(String json) {
        Gson gson = new Gson();

        if (json.contains("\"taskType\":\"" + TaskType.ASSIGNMENT.toString()
                + "\"")) {
            return gson.fromJson(json, Assignment.class);
        } else if (json.contains("\"taskType\":\"" + TaskType.ASSESSMENT
                .toString() + "\"")) {
            return gson.fromJson(json, Assessment.class);
        } else if (json.contains("\"taskType\":\"" + TaskType.VIRTUAL_MEETING
                .toString() + "\"")) {
            return gson.fromJson(json, VirtualMeeting.class);
        } else {
            return gson.fromJson(json, PhysicalMeeting.class);
        }
    }

    /**
     * save an individual task to the tasklist
     * @param context the application's context
     * @param task the task to save
     */
    public static void saveTask(Context context, Task task) {
        ArrayList<Task> taskList = loadTaskList(context);
        Task currentTask;

        for (int i = 0; i < taskList.size(); i++) {
            currentTask = taskList.get(i);

            if (currentTask.getUniqueId() == task.getUniqueId()) {
                taskList.set(i, task);
                break;
            }
        }

        saveTaskList(context, taskList);
    }

    /**
     * saves the task list to firebase if the user has an account, and locally no matter what
     * @param context the application's context
     * @param taskList an ArrayList of the user's tasks
     */
    public static void saveTaskList(Context context, ArrayList<Task> taskList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        // save the data locally
        String json = new Gson().toJson(taskList);

        editor.putString(TASK_FILE, json);
        editor.putInt(TASK_ID_COUNTER_FILE, Task.idCounter);

        editor.apply();

        // save the data to firebase
        if (MainActivity.firebaseUser != null && MainActivity.firebaseUser.getEmail() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ref = db.collection("Data").document("TaskData")
                    .collection(MainActivity.firebaseUser.getEmail());
            Map<String, Object> data = new HashMap<>();

            data.put(TASK_FILE, json);
            data.put(TASK_ID_COUNTER_FILE, Task.idCounter);

            ref.document(TASK_FILE).set(data);
        }
    }
}
