package com.forloopers.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE_TITLE = "EXTRA_NOTE_TITLE";

    private boolean colourNavbar;
    private String title, note;
    private EditText noteText, titleText;
    private AlertDialog dialog;
    private String titleKey, noteKey;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String userName;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private @ColorInt
    int colourPrimary, colourFont, colourBackground;

    //Intent is an abstract description of an operation to be performed
    //Intent(Context packageContext, Class cls) create intent for a specific component

    public static Intent getStartIntent(Context context, String title) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        titleText = findViewById(R.id.et_title);
        noteText = findViewById(R.id.et_note);

//        mAuth = FirebaseAuth.getInstance();
//        userName = mAuth.getCurrentUser().toString();
        userName = "sarita.chap@gmail.com";


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //Get title string from NotesFragment to use for unique keys
        String titleString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                titleString= null;
            } else {
                titleString = extras.get("com.example.studymuffin.NotesFragment").toString();
            }
        } else {
            titleString= (String) savedInstanceState.getSerializable("com.example.studymuffin.NotesFragment");
        }
        titleText.setText(titleString);
        titleKey = titleText.getText().toString();

        //Get saved note data through sharedpreferences and apply it
        String retrivedNote = sharedpreferences.getString(titleKey, "");
        noteText.setText(retrivedNote);

        // If activity started from a share intent
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                noteText.setText(sharedText);
                note = sharedText;
                title = "";
            }
        } else { // If activity started from the notes list
            title = intent.getStringExtra(EXTRA_NOTE_TITLE);
            if (title == null || TextUtils.isEmpty(title)) {
                title = "";
                note = "";
                noteText.requestFocus();
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(getString(R.string.new_note));
            } else {
                titleText.setText(title);
                note = HelperUtils.readFile(NoteActivity.this, title);
                noteText.setText(note);
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(title);
            }
        }

        getSettings(PreferenceManager.getDefaultSharedPreferences(NoteActivity.this));
        applySettings();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        note = noteText.getText().toString().trim();
        if (getCurrentFocus() != null)
            getCurrentFocus().clearFocus();
    }

    @Override
    public void onPause() {

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> docData = new HashMap<>();
        LocalDate date = LocalDate.now();
        int month = date.getMonth().getValue();
        int day = date.getDayOfMonth();
        int year = date.getYear();

        docData.put("NoteText", noteText.getText().toString());
        docData.put("Month", month);
        docData.put("Day", day);
        docData.put("Year", year);


        //Get account name
        //replace account name in collection path
        //document path is titleKey
        db.collection("Data").document("NoteData")
                .collection(userName).document(titleText.getText().toString()).set(docData, SetOptions.merge());
        editor.putString(titleKey, noteText.getText().toString());
        editor.commit();

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_undo:
                noteText.setText(note);
                noteText.setSelection(noteText.getText().length());
                return (true);


            case R.id.btn_delete:
                dialog = new AlertDialog.Builder(NoteActivity.this, R.style.AlertDialogTheme)
                        .setTitle(getString(R.string.confirm_delete))
                        .setMessage(getString(R.string.confirm_delete_text))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (HelperUtils.fileExists(NoteActivity.this, title)) {
                                    deleteFile(title + HelperUtils.TEXT_FILE_EXTENSION);
                                }

                                //Deletes stuff locally
                                ArrayList<String> titleArray = new ArrayList<>();
                                HashSet<String> titleSet = new HashSet<>();
                                titleArray.addAll(sharedpreferences.getStringSet("key", titleSet));
                                titleArray.remove(titleText.getText().toString());
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(titleKey, "");
                                editor.commit();
                                titleSet.addAll(titleArray);
                                editor.putStringSet("key", titleSet);
                                editor.commit();

                                //Deletes Stuff From Firebase
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Data").document("NoteData")
                                        .collection(userName).document(titleText.getText().toString()).delete();
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp))
                        .show();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().getDecorView().setBackgroundColor(colourPrimary);
                }
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    private void getSettings(SharedPreferences preferences) {
        colourPrimary = preferences.getInt(HelperUtils.PREFERENCE_COLOUR_PRIMARY, ContextCompat.getColor(NoteActivity.this, R.color.beige));
        colourFont = preferences.getInt(HelperUtils.PREFERENCE_COLOUR_FONT, Color.BLACK);
        colourBackground = preferences.getInt(HelperUtils.PREFERENCE_COLOUR_BACKGROUND, Color.WHITE);
        colourNavbar = preferences.getBoolean(HelperUtils.PREFERENCE_COLOUR_NAVBAR, false);
    }

    private void applySettings() {
        HelperUtils.applyColours(NoteActivity.this, colourPrimary, colourNavbar);

        // Set text field underline colour
        noteText.setBackgroundTintList(ColorStateList.valueOf(colourPrimary));
        titleText.setBackgroundTintList(ColorStateList.valueOf(colourPrimary));

        // Set actionbar and background colour
        findViewById(R.id.scroll_view).setBackgroundColor(colourBackground);
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colourPrimary));

        // Set font colours
        titleText.setTextColor(colourFont);
        noteText.setTextColor(colourFont);

        // Set hint colours
        titleText.setHintTextColor(ColorUtils.setAlphaComponent(colourFont, 120));
        noteText.setHintTextColor(ColorUtils.setAlphaComponent(colourFont, 120));
    }



}