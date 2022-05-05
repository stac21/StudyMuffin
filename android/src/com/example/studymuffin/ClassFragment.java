package com.example.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClassFragment extends Fragment {
    private View view;
    private CardAdapter cardAdapter;
    public static int selectedCardPosition;
    public static boolean isCardSelected = false;

    public static final String COURSE_FILE = "com.example.studymuffin.course_file";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_class_list, container, false);
        Context context = this.view.getContext();

        ArrayList<CourseInfo> list = loadCourseList(context);
        this.cardAdapter = new CardAdapter(list);

        this.makeRecyclerView();

        FloatingActionButton fab = this.view.findViewById(R.id.classFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());

                View v = getLayoutInflater().inflate(R.layout.classdialog, null);

                final EditText nameEt = (EditText) v.findViewById(R.id.name_et);
                final EditText linkEt = v.findViewById(R.id.link_et);
                final EditText locationEt = v.findViewById(R.id.location_et);
                final EditText instructorEt = v.findViewById(R.id.instructor_et);
                final EditText dayTimeEt = v.findViewById(R.id.day_time_et);

                dialog.setTitle("Create Class");
                dialog.setView(v);
                // may have to set this to false
                dialog.setCancelable(true);
                dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                                String dayTime = dayTimeEt.getText().toString();
                                String link = linkEt.getText().toString();

                                if (name.length() != 0 && location.length() != 0 &&
                                        instructor.length() != 0 && dayTime.length() != 0 &&
                                        link.length() != 0) {
                                    // TODO make a spinner or something for instructors
                                    String firstName = instructor.substring(0,
                                            instructor.indexOf(" "));
                                    String lastName = instructor.substring(
                                            instructor.indexOf(" ") + 1);

                                    cardAdapter.addCard(nameEt.getText().toString(),
                                            new Instructor(firstName, lastName), location, link,
                                            new ArrayList<String>(), 0, 0,
                                            0, 0, Color.RED);

                                    alertDialog.dismiss();
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

    private class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
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

            v.getContext().startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            isCardSelected = true;
            selectedCardPosition = this.getAdapterPosition();

            ClassFragment.this.getActivity().invalidateOptionsMenu();

            return true;
        }
    }

    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(COURSE_FILE, null);

        Type collectionType = new TypeToken<ArrayList<CourseInfo>>(){}.getType();

        return new Gson().fromJson(json, collectionType);
    }

    public static void saveCourseList(Context context, ArrayList<CourseInfo> courseList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(courseList);

        editor.putString(COURSE_FILE, json);

        editor.apply();
    }

    public static void addTaskToClass(Context context, int courseIndex, Task task) {
        ArrayList<CourseInfo> courseList = loadCourseList(context);
        CourseInfo ci = courseList.get(courseIndex);
        System.out.println("Course name: " + ci.getTitle());
        courseList.get(courseIndex).addTask(task);

        saveCourseList(context, courseList);
    }
}
