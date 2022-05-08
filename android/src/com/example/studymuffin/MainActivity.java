package com.example.studymuffin;

import android.content.Intent;
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
    public static Account userAccount;
    private FirebaseAuth mAuth;
    public static Profile profile;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.setThemeOfApp(this);
        setContentView(R.layout.activity_main);

        final Button signInButton = this.findViewById(R.id.signInButton);

        final Button registerButton = this.findViewById(R.id.registerButton);
        final Button guestButton = this.findViewById(R.id.guestButton);
        final Button createAccountButton = this.findViewById(R.id.createAccountButton);
        final EditText emailET = this.findViewById(R.id.emailEditText);
        final EditText passwordET = this.findViewById(R.id.passwordEditText);
        this.bottomNav = this.findViewById(R.id.bottom_nav_view);
        final EditText confirmPasswordET = this.findViewById(R.id.confirmPasswordEditText);
        final ImageView studMuffin = this.findViewById(R.id.main_stud_muffin);
        final Button createProfile = this.findViewById(R.id.createProfile);
        final EditText firstName = this.findViewById(R.id.firstName);
        final EditText lastName = this.findViewById(R.id.lastName);
        final TextView incorrectUsernamePassword = this.findViewById(R.id.incorrectUsernamePassword);

        loadFragment(currentFragment);

        mAuth = FirebaseAuth.getInstance();
        userAccount = Account.loadAccount(MainActivity.this);
        profile = Profile.loadProfile(MainActivity.this);

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

                signInButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                emailET.setVisibility(View.INVISIBLE);
                passwordET.setVisibility(View.INVISIBLE);
                confirmPasswordET.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
                studMuffin.setVisibility(View.INVISIBLE);

                MenuItem calendarItem = bottomNav.getMenu().findItem(R.id.nav_calendar);
                loadFragment(R.id.nav_calendar);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordET.setVisibility(View.VISIBLE);
                createAccountButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);

                MenuItem calendarItem = bottomNav.getMenu().findItem(R.id.nav_calendar);
                loadFragment(R.id.nav_calendar);
            }
        });

        //Not the right file
        //Create a new account with valid email and matching passwords
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                    // save the account information locally
                                    userAccount = new Account(email, password);
                                    userAccount.save(MainActivity.this);
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                confirmPasswordET.setVisibility(View.INVISIBLE);
                createAccountButton.setVisibility(View.INVISIBLE);
                guestButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);


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

                MenuItem calendarItem = bottomNav.getMenu().findItem(R.id.nav_calendar);
                loadFragment(R.id.nav_calendar);
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

        try {
            MenuItem item = bottomNav.getMenu().findItem(itemId);
            item.setChecked(true);
        } catch (NullPointerException e) {
            System.out.println("Item was null. ItemId = " + itemId);
        }

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

        if (CalendarFragment.isCardSelected || ClassFragment.isCardSelected) {
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
            if (currentFragment == R.id.nav_calendar) {
                CalendarFragment.cardAdapter.removeCard();
            } else if (currentFragment == R.id.nav_classes) {
                ClassFragment.cardAdapter.removeCard();
            }

            MainActivity.this.invalidateOptionsMenu();
        } else if (id == R.id.clear_selection_item) {
            CalendarFragment.isCardSelected = false;
            ClassFragment.isCardSelected = false;

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
        } else if (id == R.id.monthly_layout_item) {
            CalendarFragment.monthlyCalendarView.setVisibility(View.VISIBLE);
            CalendarFragment.todoListRecyclerView.setVisibility(View.INVISIBLE);
        } else if (id == R.id.todo_list_item) {
            CalendarFragment.monthlyCalendarView.setVisibility(View.INVISIBLE);
            CalendarFragment.todoListRecyclerView.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }
}
