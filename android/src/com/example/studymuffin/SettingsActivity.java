package com.example.studymuffin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static int PASTEL_PRICE = 10;
    public static int ELLE_WOODS_PRICE = 20;

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
            final Context context = SettingsActivity.this;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            String themeValue = sp.getString(key, "light");
            String[] values = context.getResources().getStringArray(
                    R.array.theme_values);

            // light theme
            if (themeValue.equals(values[0])) {
                this.recreate();
            }
            // dark theme
            else if (themeValue.equals(values[1])) {
                this.recreate();
            }
            // pastel theme
            else if (themeValue.equals(values[2])) {
                if (!MainActivity.profile.hasPurchasedPastelTheme() &&
                        MainActivity.profile.getnumBakeryMoney() < PASTEL_PRICE) {
                    // switch the selected theme to the default theme
                    editor.putString(key, values[0]);
                    editor.apply();

                    Toast.makeText(context, "Not enough money, you need " +
                            PASTEL_PRICE + " bakery money to unlock Pastel Mode, you currently have "
                            + MainActivity.profile.getnumBakeryMoney() + " Bakery Money", Toast.LENGTH_LONG).show();
                } else if (!MainActivity.profile.hasPurchasedPastelTheme() &&
                        MainActivity.profile.getnumBakeryMoney() >= PASTEL_PRICE) {
                    MainActivity.profile.substractBakeryMoney(PASTEL_PRICE);
                    MainActivity.profile.setHasPurchasedPastelTheme(true);
                    MainActivity.profile.save(context);
                    this.recreate();

                    Toast.makeText(context, "You have unlocked Pastel mode, " +
                                    PASTEL_PRICE + " bakery money has been deducted, you have "
                                    + MainActivity.profile.getnumBakeryMoney() + " Bakery Money remaining",
                            Toast.LENGTH_LONG).show();
                } else if (MainActivity.profile.hasPurchasedPastelTheme()) {
                    this.recreate();
                }
            }
            // elle woods theme
            else if (themeValue.equals(values[3])) {
                if (!MainActivity.profile.hasPurchasedElleWoodsTheme() &&
                        MainActivity.profile.getnumBakeryMoney() < ELLE_WOODS_PRICE) {
                    // switch the selected theme to the default theme
                    editor.putString(key, values[0]);
                    editor.apply();

                    Toast.makeText(context, "Not enough money, you need " +
                                    ELLE_WOODS_PRICE + " bakery money to unlock Elle Woods Mode, you currently" +
                                    " have " + MainActivity.profile.getnumBakeryMoney() + " Bakery Money",
                            Toast.LENGTH_LONG).show();
                } else if (!MainActivity.profile.hasPurchasedElleWoodsTheme() &&
                        MainActivity.profile.getnumBakeryMoney() >= ELLE_WOODS_PRICE) {
                    MainActivity.profile.substractBakeryMoney(ELLE_WOODS_PRICE);
                    MainActivity.profile.setHasPurchasedElleWoodsTheme(true);
                    MainActivity.profile.save(context);
                    this.recreate();

                    Toast.makeText(context, "You have unlocked Elle Woods mode, " +
                                    ELLE_WOODS_PRICE + " bakery money has been deducted, you have "
                                    + MainActivity.profile.getnumBakeryMoney() + " Bakery Money remaining",
                            Toast.LENGTH_LONG).show();
                } else if (MainActivity.profile.hasPurchasedElleWoodsTheme()) {
                    this.recreate();
                }
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    public static void setThemeOfApp(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                activity.getBaseContext());

        if (sharedPreferences.getString("theme", "pastel").equals("pastel")) {
            activity.setTheme(R.style.PastelMode);
        } else if (sharedPreferences.getString("theme", "elle_woods").equals("elle_woods")) {
            activity.setTheme(R.style.ElleWoodsMode);
        } else if (sharedPreferences.getString("theme", "dark").equals("dark")) {
            activity.setTheme(R.style.DarkMode);
        } else {
            activity.setTheme(R.style.LightMode);
        }
    }
}