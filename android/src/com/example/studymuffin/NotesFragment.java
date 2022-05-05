package com.example.studymuffin;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class NotesFragment extends Fragment {
    private View view;
    private CardAdapter cardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ArrayList<NoteInfo> list = new ArrayList<>();
        list.add(new NoteInfo("Binary Search", 9, 12));

        this.cardAdapter = new CardAdapter(list);
        this.view = inflater.inflate(R.layout.fragment_notes, container, false);
        this.makeRecyclerView();

        final String[] classes = {
                "CECS 327",
                "CECS 491"
        };

        FloatingActionButton fab = this.view.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());

                View v = getLayoutInflater().inflate(R.layout.dialog, null);

                final EditText nameET = (EditText) v.findViewById(R.id.name_et);
                final TextView classTV = v.findViewById(R.id.classSpinnerTextView);

                Spinner classSpinner = (Spinner) v.findViewById(R.id.classSpinner);
                classSpinner.setAdapter(new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, classes));

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
        RecyclerView recyclerView = this.view.findViewById(R.id.recyclerviewNotes);
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

            Intent i = new Intent(v.getContext(), NoteActivity.class);

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
    }
}
