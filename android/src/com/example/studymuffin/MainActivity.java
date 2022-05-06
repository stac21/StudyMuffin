package com.example.studymuffin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    // keeps track of the current fragment that the user is on. 0 is for the sign in page.
    public static final int SIGN_IN_PAGE = 0;
    public static int currentFragment = SIGN_IN_PAGE;
    public static boolean isInCalendarFragment = false;
    public static SearchView calendarSearchView;
    SharedPreferences sharedpreferences;
    public static Account userAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.setThemeOfApp(this);
        setContentView(R.layout.activity_main);

        loadFragment(currentFragment);

        final Button signInButton = this.findViewById(R.id.signInButton);

        final Button registerButton = this.findViewById(R.id.registerButton);
        final Button guestButton = this.findViewById(R.id.guestButton);
        final Button createAccountButton = this.findViewById(R.id.createAccountButton);
        final EditText emailET = this.findViewById(R.id.emailEditText);
        final EditText passwordET = this.findViewById(R.id.passwordEditText);
        final BottomNavigationView bottomNav = this.findViewById(R.id.bottom_nav_view);
        final EditText confirmPasswordET = this.findViewById(R.id.confirmPasswordEditText);
        final ImageView studMuffin = this.findViewById(R.id.main_stud_muffin);
        final Button createProfile = this.findViewById(R.id.createProfile);
        final EditText firstName = this.findViewById(R.id.firstName);
        final EditText lastName = this.findViewById(R.id.lastName);
        final TextView incorrectUsernamePassword = this.findViewById(R.id.incorrectUsernamePassword);

        mAuth = FirebaseAuth.getInstance();
        userAccount = Account.loadAccount(MainActivity.this);

        if (currentFragment != SIGN_IN_PAGE) {
            bottomNav.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            guestButton.setVisibility(View.INVISIBLE);
            emailET.setVisibility(View.INVISIBLE);
            passwordET.setVisibility(View.INVISIBLE);
            confirmPasswordET.setVisibility(View.INVISIBLE);
            registerButton.setVisibility(View.INVISIBLE);
            studMuffin.setVisibility(View.INVISIBLE);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(emailET.getText().toString());
                System.out.println(matcher);

                if(matcher.matches() == true){
                    bottomNav.setVisibility(View.VISIBLE);
                }else{
                    bottomNav.setVisibility(View.INVISIBLE);
                }
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                mAuth = FirebaseAuth.getInstance();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            signInButton.setVisibility(View.INVISIBLE);
                            guestButton.setVisibility(View.INVISIBLE);
                            emailET.setVisibility(View.INVISIBLE);
                            passwordET.setVisibility(View.INVISIBLE);
                            confirmPasswordET.setVisibility(View.INVISIBLE);
                            registerButton.setVisibility(View.INVISIBLE);
                            studMuffin.setVisibility(View.INVISIBLE);

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                        }else{
                            Toast.makeText(MainActivity.this, "Incorrect Username or Password. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirmPasswordET.setVisibility(View.VISIBLE);
                passwordET.setVisibility(View.INVISIBLE);
                createAccountButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
            }
        });

        //Create a new account with valid email and matching passwords
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordET.setVisibility(View.INVISIBLE);
                createAccountButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                ActionCodeSettings actionCodeSettings =
                        ActionCodeSettings.newBuilder()
                                // URL you want to redirect back to. The domain (www.example.com) for this
                                // URL must be whitelisted in the Firebase Console.
                                .setUrl("https://firebase.google.com/docs/auth/android/email-link-auth")
                                // This must be true
                                .setHandleCodeInApp(true)
                                .setAndroidPackageName(
                                        "com.example.studymuffin",
                                        true, /* installIfNotAvailable */
                                        "12"    /* minimumVersion */)
                                .build();

                mAuth = FirebaseAuth.getInstance();

//                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            mAuth
//                        }
//                    }
//                })
                String tempPassword = "StudyMuffinPassword491B04";
                mAuth.createUserWithEmailAndPassword(email, tempPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mAuth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //mAuth.signInWithEmailAndPassword(email, tempPassword);
                                                        //mAuth.getCurrentUser().updatePassword(password);
                                                        Toast.makeText(MainActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                    Toast.makeText(MainActivity.this, "Registered Successfully. Check your email to set your password and create your account", Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });




                //Email and Password Verification
                String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(emailET.getText().toString());
                System.out.println(matcher);

                //if the email is the right format + passwords match -> the app is accessible
                if(matcher.matches() == true /*&& passwordET.equals(confirmPasswordET)*/){
                    bottomNav.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.INVISIBLE);
                    guestButton.setVisibility(View.INVISIBLE);
                    emailET.setVisibility(View.INVISIBLE);
                    passwordET.setVisibility(View.INVISIBLE);
                    confirmPasswordET.setVisibility(View.INVISIBLE);
                    registerButton.setVisibility(View.INVISIBLE);
                    studMuffin.setVisibility(View.INVISIBLE);
                } else{
                    bottomNav.setVisibility(View.INVISIBLE);
                    incorrectUsernamePassword.setVisibility(View.VISIBLE);
                    confirmPasswordET.setVisibility(View.VISIBLE);
                }
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordET.setVisibility(View.INVISIBLE);
                createAccountButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
                createProfile.setVisibility(View.VISIBLE);
                lastName.setVisibility(View.VISIBLE);
                firstName.setVisibility(View.VISIBLE);
                emailET.setVisibility(View.INVISIBLE);
                passwordET.setVisibility(View.INVISIBLE);
            }
        });

        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNav.setVisibility(View.VISIBLE);
                createProfile.setVisibility(View.INVISIBLE);
                lastName.setVisibility(View.INVISIBLE);
                firstName.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                emailET.setVisibility(View.INVISIBLE);
                passwordET.setVisibility(View.INVISIBLE);
                confirmPasswordET.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
                studMuffin.setVisibility(View.INVISIBLE);
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        loadFragment(item.getItemId());

                        return true;
                    }
                }
        );
    }

    public void loadFragment(int itemId) {
        Fragment selectedFragment = null;
        currentFragment = itemId;

        if (MainActivity.isInCalendarFragment) {
            MainActivity.isInCalendarFragment = false;
            MainActivity.this.invalidateOptionsMenu();
        }

        switch (itemId) {
            case R.id.nav_calendar:
                MainActivity.isInCalendarFragment = true;
                MainActivity.this.invalidateOptionsMenu();

                selectedFragment = new CalendarFragment();
                break;
            case R.id.nav_bakery:
                selectedFragment = new BakeryFragment();
                break;
            case R.id.nav_timer:
                selectedFragment = new TimerFragment();
                break;
            case R.id.nav_notes:
                selectedFragment = new NotesFragment();
                break;
            case R.id.nav_classes:
                selectedFragment = new ClassFragment();
                break;
        }

        if (itemId != SIGN_IN_PAGE) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, selectedFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search_item);
        MenuItem deleteItem = menu.findItem(R.id.delete_item);
        MenuItem clearSelectionItem = menu.findItem(R.id.clear_selection_item);
        MenuItem settingsItem = menu.findItem(R.id.settings_item);
        MenuItem filterItem = menu.findItem(R.id.filter_item);
        MenuItem filterDueDateItem = menu.findItem(R.id.sort_due_date_item);
        MenuItem switchLayoutItem = menu.findItem(R.id.switch_layout_item);

        if (CalendarFragment.isCardSelected) {
            deleteItem.setVisible(true);
            clearSelectionItem.setVisible(true);
            settingsItem.setVisible(false);
            searchItem.setVisible(false);

            // TODO change the colors of the icons to match the current theme

            CalendarFragment.isCardSelected = false;
        } else {
            searchItem.setVisible(MainActivity.isInCalendarFragment);
            filterItem.setVisible(MainActivity.isInCalendarFragment);
            switchLayoutItem.setVisible(MainActivity.isInCalendarFragment);

            calendarSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            calendarSearchView.setSubmitButtonEnabled(true);
            calendarSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    // set the recyclerview back to the full list of tasks rather than the filtered list
                    ArrayList<Task> taskList = CalendarFragment.loadTaskList(MainActivity.this);

                    for (int i = 0; i < taskList.size(); i++) {
                        System.out.println(taskList.get(i).getName());
                    }

                    CalendarFragment.cardAdapter.setTaskList(taskList);

                    MainActivity.this.invalidateOptionsMenu();

                    return false;
                }
            });
            calendarSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null) {
                        System.out.println("Text submitted");

                        CalendarFragment.cardAdapter.filterList(query);

                        MainActivity.this.invalidateOptionsMenu();
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    System.out.println("Text changed");

                    return true;
                }
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * handles action bar items being clicked
     * @param item the action bar item that has been clicked
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_item) {
            Intent i = new Intent(this, SettingsActivity.class);

            this.startActivity(i);
        } else if (id == R.id.delete_item) {
            CalendarFragment.cardAdapter.removeCard();

            MainActivity.this.invalidateOptionsMenu();
        } else if (id == R.id.clear_selection_item) {
            CalendarFragment.isCardSelected = false;

            MainActivity.this.invalidateOptionsMenu();
        } else if (id == R.id.sort_due_date_item) {
            CalendarFragment.sortPreference = SortPreference.DUE_DATE;
            CalendarFragment.saveSortPreference(MainActivity.this);

            CalendarFragment.cardAdapter.sort();

            // notify the recyclerview that the list has changed when the list is sorted
            CalendarFragment.cardAdapter.notifyItemRangeChanged(0,
                    CalendarFragment.cardAdapter.getItemCount());

        } else if (id == R.id.sort_priority_item) {
            CalendarFragment.sortPreference = SortPreference.PRIORITY;
            CalendarFragment.saveSortPreference(MainActivity.this);

            CalendarFragment.cardAdapter.sort();

            // notify the recyclerview that the list has changed when the list is sorted
            CalendarFragment.cardAdapter.notifyItemRangeChanged(0,
                    CalendarFragment.cardAdapter.getItemCount());

        }

        return super.onOptionsItemSelected(item);
    }
}
