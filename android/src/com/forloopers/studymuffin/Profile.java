package com.forloopers.studymuffin;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Profile {
    private String firstName;
    private String lastName;
    private int numStudyPoints;
    private float numBakeryMoney;
    private boolean hasPurchasedPastelTheme;
    private boolean hasPurchasedElleWoodsTheme;
    public static String PROFILE_FILE = "com.example.studymuffin.profile";

    public Profile(String firstName, String lastName, int numStudyPoints, int numBakeryMoney) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numStudyPoints = numStudyPoints;
        this.numBakeryMoney = numBakeryMoney;
        this.hasPurchasedPastelTheme = false;
        this.hasPurchasedElleWoodsTheme = false;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNumPoints() {
        return numStudyPoints;
    }

    public void addPoints(int numStudyPoints) {
        this.numStudyPoints += numStudyPoints;
    }

    public void substractPoints(int numStudyPoints) {
        this.numStudyPoints -= numStudyPoints;
    }

    public void setPoints(int numStudyPoints) {
        this.numStudyPoints = numStudyPoints;
    }

    public float getnumBakeryMoney() {
        return numBakeryMoney;
    }

    public void addBakeryMoney(float numBakeryMoney) {
        this.numBakeryMoney += numBakeryMoney;
    }

    public void substractBakeryMoney(float numBakeryMoney) {
        this.numBakeryMoney -= numBakeryMoney;
    }

    public void setBakeryMoney(float numBakeryMoney) {
        this.numBakeryMoney = numBakeryMoney;
    }

    public boolean hasPurchasedPastelTheme() {
        return hasPurchasedPastelTheme;
    }

    public void setHasPurchasedPastelTheme(boolean hasPurchasedPastelTheme) {
        this.hasPurchasedPastelTheme = hasPurchasedPastelTheme;
    }

    public boolean hasPurchasedElleWoodsTheme() {
        return hasPurchasedElleWoodsTheme;
    }

    public void setHasPurchasedElleWoodsTheme(boolean hasPurchasedElleWoodsTheme) {
        this.hasPurchasedElleWoodsTheme = hasPurchasedElleWoodsTheme;
    }

    /**
     * load the profile's information from the JSON file
     * @param context the application's context
     * @return the user's profile
     */
    public static Profile loadProfile(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(PROFILE_FILE, null);

        Type collectionType = new TypeToken<Profile>(){}.getType();
        Profile profile = new Gson().fromJson(json, collectionType);

        if (profile != null) {
            return profile;
        } else {
            return new Profile("Profile", "One", 0, 0);
        }
    }

    /**
     * save the profile's information both locally to a JSON file
     * as well as to the Firebase instance associated with their account,
     * if it exists
     * @param context the application's context
     * @param profile The user's profile to save
     */
    public static void saveProfile(Context context, Profile profile) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(profile);

        editor.putString(PROFILE_FILE, json);

        editor.apply();

        // TODO upload the updated profile to the user's account in Firebase
    }

    /**
     * save the profile's information both locally to a JSON file
     * as well as to the Firebase instance associated with their account,
     * if it exists
     * @param context the application's context
     */
    public void save(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(this);

        editor.putString(PROFILE_FILE, json);

        editor.apply();
    }
}
