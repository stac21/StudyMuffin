package com.example.studymuffin;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotesFragment extends Fragment {
    private  View view;
    private CardAdapter cardAdapter;
    private ArrayList<NoteInfo> list;
    private Set<String> titleList;
    private String userName;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NOTES_FILE = "com.example.studymuffin.notes_file";

    public NotesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        list = new ArrayList<>();
        titleList = new HashSet<>();
        //mAuth = FirebaseAuth.getInstance();
        //userName = mAuth.getCurrentUser().toString();
        userName = "sarita.chap@gmail.com";

        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        //Get stored list of titles
        titleList.addAll(sharedpreferences.getStringSet("key", titleList));
        //titleList = sharedpreferences.getStringSet("key", titleList);

        //list.add(new NoteInfo("Hello", 4, 1));
        //titleList.add("Hello");
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (MainActivity.firebaseUser != null) {
            ArrayList<String> titleArray
                    = new ArrayList<>();
            titleArray.addAll(titleList);

            //go through stored string set and add notes to list
            for (int j = 0; j < titleArray.size(); j++) {
                list.add(new NoteInfo(titleArray.get(j), sharedpreferences.getInt(titleArray.get(j) + "month", 1),
                        sharedpreferences.getInt(titleArray.get(j) + "day", 1)));
            }
        } else {
            list = loadLocalNoteList(getContext());
        }

        //list.add(new NoteInfo("Binary Search", 9, 12));

        this.cardAdapter = new CardAdapter(list);
        this.view = inflater.inflate(R.layout.fragment_notes, container, false);
        this.makeRecyclerView();

        ArrayList<CourseInfo> courseList = ClassFragment.loadCourseList(getContext());

        final String[] courseNamesArr = new String[courseList.size()];

        for (int i = 0; i < courseNamesArr.length; i++) {
            courseNamesArr[i] = courseList.get(i).getTitle();
        }

        FloatingActionButton fab = this.view.findViewById(R.id.floatingActionButton);

        //Add new note button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());

                View v = getLayoutInflater().inflate(R.layout.dialog, null);

                final EditText nameET = (EditText) v.findViewById(R.id.name_et);
                final TextView classTV = v.findViewById(R.id.classSpinnerTextView);

                Spinner classSpinner = (Spinner) v.findViewById(R.id.classSpinner);
                classSpinner.setAdapter(new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, courseNamesArr));

                classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 4) {
                            Calendar currentTime = Calendar.getInstance();
                            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = currentTime.get(Calendar.MINUTE);

                            final TimePickerDialog tmDialog = new TimePickerDialog(
                                    getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int userMinute) {

                                }
                            }, hour, minute, true);

                            tmDialog.setTitle("Select Time");
                            tmDialog.show();
                        } else {
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                dialog.setTitle("Create Note");
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
                                dialog.cancel();
                                LocalDate date = LocalDate.now();
                                int month = date.getMonth().getValue();
                                int day = date.getDayOfMonth();
                                int year = date.getYear();
                                cardAdapter.addCard(nameET.getText().toString(), month, day);

                                //Saving Note Card Titles
                                titleList.add(nameET.getText().toString());
                                editor.putStringSet("key", titleList);
                                editor.putInt(nameET.getText().toString() + "month", month);
                                editor.putInt(nameET.getText().toString() + "day", day);
                                editor.commit();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> docData = new HashMap<>();
                                docData.put("NoteTitle", nameET.getText().toString());
                                docData.put("NoteText", "");
                                docData.put("Month", month);
                                docData.put("Day", day);
                                docData.put("Year", year);

                                //Get account name
                                //replace account name in collection path
                                //document path is titleKey
                                db.collection("Data").document("NoteData")
                                        .collection(userName).document(nameET.getText().toString()).set(docData);

                                makeRecyclerView();
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });

        return this.view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        titleList = new HashSet<>();

        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Get stored list of titles
        titleList.addAll(sharedpreferences.getStringSet("key", titleList));

        ArrayList<String> titleArray
                = new ArrayList<>();
        titleArray.addAll(titleList);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        //go through stored string set and add notes to list
        for(int j = 0; j < titleArray.size(); j++){
            list.add(new NoteInfo(titleArray.get(j), sharedpreferences.getInt(titleArray.get(j) + "month", 1),
                    sharedpreferences.getInt(titleArray.get(j) + "day", 1)));
        }

        //list.add(new NoteInfo("Binary Search", 9, 12));

        this.cardAdapter = new CardAdapter(list);
        makeRecyclerView();
    }

    public void makeRecyclerView() {
        RecyclerView recyclerView = this.view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(this.cardAdapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {
        public TextView titleTextView, dateTextView;
        public String titleString;

        public CardViewHolder(View v) {
            super(v);

            this.titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            this.dateTextView = (TextView) v.findViewById(R.id.dateTextView);


            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }


        //Access Note by clicking on the note card
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), NoteActivity.class);
            this.titleString = titleTextView.getText().toString();
            i.putExtra("com.example.studymuffin.NotesFragment", titleString);

            v.getContext().startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }

    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private ArrayList<NoteInfo> noteInfoList;

        public CardAdapter(ArrayList<NoteInfo> noteInfoList) {
            if (noteInfoList != null) {
                this.noteInfoList = noteInfoList;
            } else {
                this.noteInfoList = new ArrayList<>();
            }
        }

        @Override
        public int getItemCount() {
            return this.noteInfoList.size();
        }

        public NoteInfo getNote(int position) {
            return this.noteInfoList.get(position);
        }

        public ArrayList<NoteInfo> getNoteInfoList() {
            return this.noteInfoList;
        }

        public void onBindViewHolder(CardViewHolder v, int i) {
            NoteInfo ni = this.noteInfoList.get(i);

            v.titleTextView.setText(ni.getTitle());
            v.dateTextView.setText(ni.getDateEdited());
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.note_card_view, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(String title, int month, int day) {
            NoteInfo noteInfo = new NoteInfo(title, month, day);

            this.noteInfoList.add(noteInfo);
        }

        public void removeCard(String title, int month, int day) {
            NoteInfo noteInfo = new NoteInfo(title, month, day);

            this.noteInfoList.remove(noteInfo);
        }
    }

    /**
     * load the notes from local storage
     * @param context the application's context
     * @return
     */
    public static ArrayList<NoteInfo> loadLocalNoteList(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(NOTES_FILE, null);

        Type collectionType = new TypeToken<ArrayList<NoteInfo>>(){}.getType();
        ArrayList<NoteInfo> noteList = new Gson().fromJson(json, collectionType);

        if (noteList != null) {
            return noteList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * save the note list to local storage
     * @param context the application's context
     */
    public static void saveNoteList(Context context, ArrayList<NoteInfo> notesList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(notesList);

        editor.putString(NOTES_FILE, json);

        editor.apply();
    }
}
