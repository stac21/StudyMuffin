package com.forloopers.studymuffin;

import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ClassFragment extends Fragment {
    private View view;
    public static CardAdapter cardAdapter;
    public static int selectedCardPosition;
    public static boolean isCardSelected = false;
    private TextView GPAview;
    private static int selectedStartHour;
    private static int selectedStartMinute;
    private static int selectedEndHour;
    private static int selectedEndMinute;

    private static boolean loadFromDb = true;

    public static final String COURSE_FILE = "com.example.studymuffin.courseFile";
    public static final String COURSE_ID_COUNTER_FILE = "com.example.studymuffin.course_id_counter_file";
    public static final String COURSE_INTENT = "com.example.studymuffin.course_intent";

    public static float gpaCalculator(ArrayList<CourseInfo> a, Context context) {
        float totalPoints = 0;
        int countedCourses = a.size();
        float grade;

        for (int i = 0; i < a.size(); i++) {
            grade = a.get(i).calculateClassGrade(context);

            if (grade >= 90) {
                totalPoints += 4;
            } else if (grade >= 80) {
                totalPoints += 3;
            } else if (grade >= 70) {
                totalPoints += 2;
            } else if (grade >= 60) {
                totalPoints += 1;
            } else if (grade < 0) {
                countedCourses--;
            }
        }

        return totalPoints / countedCourses;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_class_list, container, false);
        Context context = this.view.getContext();
        Resources r = context.getResources();

        cardAdapter = new CardAdapter(loadCourseList(context));

        final String[] times = r.getStringArray(R.array.time_spinner_array);
        System.out.println("CourseInfo.idCounter = " + CourseInfo.idCounter);

        this.makeRecyclerView();

        FloatingActionButton fab = this.view.findViewById(R.id.classFab);

        this.GPAview = this.view.findViewById(R.id.GPAview);
        this.GPAview.setText(gpaCalculator(cardAdapter.getCourseInfoList(), context) + "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());

                View v = getLayoutInflater().inflate(R.layout.classdialog, null);

                final EditText nameEt = (EditText) v.findViewById(R.id.name_et);
                final EditText linkEt = v.findViewById(R.id.link_et);
                final EditText locationEt = v.findViewById(R.id.location_et);
                final EditText instructorEt = v.findViewById(R.id.instructor_et);
                final EditText dayEt = v.findViewById(R.id.day_et);
                final Spinner startTimeSpinner = v.findViewById(R.id.startTimeSpinner);
                final Spinner endTimeSpinner = v.findViewById(R.id.endTimeSpinner);

                startTimeSpinner.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_dropdown_item, times));
                startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                endTimeSpinner.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_dropdown_item, times));
                endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dialog.setTitle("Create Class");
                dialog.setView(v);
                // may have to set this to false
                dialog.setCancelable(true);
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

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("Positive button clicked");

                                String name = nameEt.getText().toString();
                                String location = locationEt.getText().toString();
                                String instructor = instructorEt.getText().toString();
                                String date = dayEt.getText().toString();
                                String link = linkEt.getText().toString();
                                ArrayList<String> daysOfWeek = new ArrayList<>();
                                daysOfWeek.add(date);

                                if (name.length() != 0 && location.length() != 0 &&
                                        instructor.length() != 0 && date.length() != 0 &&
                                        link.length() != 0 && (selectedStartHour < selectedEndHour
                                        || (selectedStartHour == selectedEndHour &&
                                        selectedStartMinute < selectedEndMinute))) {
                                    // TODO make a spinner or something for instructors
                                    String firstName = instructor;
                                    String lastName = "";

                                    cardAdapter.addCard(name, new Instructor(firstName, lastName),
                                            location, link, daysOfWeek,
                                            selectedStartHour,
                                            selectedStartMinute, selectedEndHour, selectedEndMinute,
                                            Color.RED);

                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(context, "All fields must be filled out and" +
                                            " start must be less than end time", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });

        return this.view;
    }

    public void makeRecyclerView() {
        RecyclerView recyclerView = this.view.findViewById(R.id.recyclerviewClasses);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.cardAdapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {
        protected TextView titleTextView, dateTextView;

        public CardViewHolder(View v) {
            super(v);

            this.titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            this.dateTextView = (TextView) v.findViewById(R.id.dateTextView);

            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Card Clicked", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(v.getContext(), CourseActivity.class);
            CourseInfo course = cardAdapter.getCourse(this.getLayoutPosition());
            selectedCardPosition = this.getAdapterPosition();

            i.putExtra(COURSE_INTENT, new Gson().toJson(course));

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

    public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private ArrayList<CourseInfo> courseInfoList;

        public CardAdapter(ArrayList<CourseInfo> courseInfoList) {
            if (courseInfoList != null) {
                this.courseInfoList = courseInfoList;
            } else {
                this.courseInfoList = new ArrayList<>();
            }
        }

        @Override
        public int getItemCount() {
            return this.courseInfoList.size();
        }

        public CourseInfo getCourse(int position) {
            return this.courseInfoList.get(position);
        }

        public ArrayList<CourseInfo> getCourseInfoList() {
            return this.courseInfoList;
        }

        public void onBindViewHolder(CardViewHolder v, int i) {
            CourseInfo ci = this.courseInfoList.get(i);

            v.titleTextView.setText(ci.getTitle());
            v.dateTextView.setText(ci.getClassroom());
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.note_card_view, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(String title, Instructor instructor, String classroom, String zoomLink,
                            ArrayList<String> daysOfWeek, int startTimeHour,
                            int startTimeMinute, int endTimeHour, int endTimeMinute, int color) {
            CourseInfo ci = new CourseInfo(title, instructor, classroom, zoomLink, daysOfWeek,
                    startTimeHour, startTimeMinute, endTimeHour, endTimeMinute, color);

            this.courseInfoList.add(ci);

            this.notifyItemInserted(this.getItemCount() + 1);

            saveCourseList(view.getContext(), this.courseInfoList);
        }

        public void removeCard() {
            this.courseInfoList.remove(selectedCardPosition);

            this.notifyItemRemoved(selectedCardPosition);
            this.notifyItemChanged(selectedCardPosition, this.getItemCount());

            saveCourseList(view.getContext(), this.courseInfoList);
        }
    }

    public static ArrayList<CourseInfo> loadCourseList(Context context) {
        Type collectionType = new TypeToken<ArrayList<CourseInfo>>(){}.getType();
        final ArrayList<CourseInfo> courseList = new ArrayList<>();

        if (MainActivity.firebaseUser != null && MainActivity.firebaseUser.getEmail() != null &&
                loadFromDb) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference ref = db.collection("Data")
                    .document("CourseData")
                    .collection(MainActivity.firebaseUser.getEmail());

            DocumentReference dataRef = ref.document(COURSE_FILE);
            dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            if (data.get(COURSE_ID_COUNTER_FILE) != null) {
                                CourseInfo.idCounter = Math.toIntExact((long)data.get(COURSE_ID_COUNTER_FILE));
                            }

                            String dataJson = (String) data.get(COURSE_FILE);
                            System.out.println("Course Json: " + dataJson);
                            ArrayList<CourseInfo> loadedCourseList = new Gson().fromJson(dataJson,
                                    collectionType);

                            System.out.println("loadedCourseList: ");

                            for (CourseInfo ci : loadedCourseList) {
                                System.out.println(ci.getTitle());
                            }

                            if (loadedCourseList != null) {
                                courseList.addAll(loadedCourseList);

                                System.out.println("CourseList: ");

                                for (CourseInfo ci : courseList) {
                                    System.out.println(ci.getTitle());
                                }
                            }

                            loadFromDb = false;

                            System.out.println("CourseList exists");
                        } else {
                            System.out.println("No such document");
                        }
                    } else {
                        System.out.println("CourseList could not be retrieved" + task.getException());
                    }
                }
            });
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(COURSE_FILE, null);
            ArrayList<CourseInfo> loadedCourseList = new Gson().fromJson(json, collectionType);

            if (loadedCourseList != null) {
                courseList.addAll(new Gson().fromJson(json, collectionType));
            }
        }

        System.out.println("CourseList after condition");
        for (CourseInfo ci : courseList) {
            System.out.println(ci.getTitle());
        }

        return courseList;
    }

    public static void saveCourseList(Context context, ArrayList<CourseInfo> courseList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        // save the data locally
        String json = new Gson().toJson(courseList);

        editor.putString(COURSE_FILE, json);
        editor.putInt(COURSE_ID_COUNTER_FILE, CourseInfo.idCounter);

        editor.apply();

        // save the data to firebase
        if (MainActivity.firebaseUser != null && MainActivity.firebaseUser.getEmail() != null) {
            System.out.println("Course data firebase");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ref = db.collection("Data")
                    .document("CourseData")
                    .collection(MainActivity.firebaseUser.getEmail());

            Map<String, Object> data = new HashMap<>();

            data.put(COURSE_FILE, json);
            data.put(COURSE_ID_COUNTER_FILE, CourseInfo.idCounter);

            ref.document(COURSE_FILE).set(data);
        }
    }

    public static CourseInfo getCourseAtIndex(Context context, int courseIndex) {
        ArrayList<CourseInfo> courseList = loadCourseList(context);
        CourseInfo ci = courseList.get(courseIndex);

        return ci;
    }

    public static ArrayList<NoteInfo> getNotes(Context context) {
        ArrayList<CourseInfo> courseList = loadCourseList(context);
        ArrayList<NoteInfo> noteList = new ArrayList<>();

        for (int i = 0; i < courseList.size(); i++) {
            noteList.addAll(courseList.get(i).getNoteList());
        }

        return noteList;
    }

}
