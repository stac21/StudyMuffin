package com.example.studymuffin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static Profile profile = MainActivity.profile;
    private static boolean pastelAccess = false;
    private static boolean elleAccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).registerOnSharedPreferenceChangeListener(this);
        setThemeOfApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme")) {
            this.recreate();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    public static void setThemeOfApp(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        if (sharedPreferences.getString("theme", "pastel").equals("pastel")) {
            if (!pastelAccess && profile.getnumBakeryMoney() < 5000) {
                Toast.makeText(activity.getBaseContext(), "Not enough points, you need 5000 bakery money to unlock Pastel Mode, you currently have "
                        + profile.getnumBakeryMoney() + " Bakery Money", Toast.LENGTH_LONG).show();
            } else if (!pastelAccess && profile.getnumBakeryMoney() >= 5000) {
                profile.substractBakeryMoney(5000);
                Toast.makeText(activity.getBaseContext(), "You have unlocked Pastel mode, 5000 bakery money has been deducted, you have "
                        + profile.getnumBakeryMoney() + " Bakery Money remaining", Toast.LENGTH_LONG).show();
                pastelAccess = true;
            }
            if (pastelAccess) {
                activity.setTheme(R.style.PastelMode);
            } else {
                if (sharedPreferences.getString("theme", "elle_woods").equals("elle_woods")) {
                    if (!elleAccess && profile.getnumBakeryMoney() < 10000) {
                        Toast.makeText(activity.getBaseContext(), "Not enough points, you need 10000 bakery money to unlock Elle Woods Mode, you currently have "
                                + profile.getnumBakeryMoney() + " Bakery Money", Toast.LENGTH_LONG).show();
                    } else if (!elleAccess && profile.getnumBakeryMoney() >= 10000) {
                        profile.substractBakeryMoney(10000);
                        Toast.makeText(activity.getBaseContext(), "You have unlocked Elle Woods mode, 10000 bakery money has been deducted, you have "
                                + profile.getnumBakeryMoney() + " Bakery Money remaining", Toast.LENGTH_LONG).show();
                        elleAccess = true;
                    }
                    if (elleAccess) {
                        activity.setTheme(R.style.ElleWoodsMode);
                    }
                } else {
                    if (sharedPreferences.getString("theme", "dark").equals("dark")) {
                        activity.setTheme(R.style.DarkMode);
                    } else {
                        activity.setTheme(R.style.LightMode);
                    }
                }
            }
        }
    }
}