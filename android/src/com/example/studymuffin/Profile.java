package com.example.studymuffin;

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
    private int numBakeryMoney;
    public static String PROFILE_FILE = "com.example.studymuffin.profile";

    public Profile(String firstName, String lastName, int numStudyPoints, int numBakeryMoney) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numStudyPoints = numStudyPoints;
        this.numBakeryMoney = numBakeryMoney;
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

    public int getnumBakeryMoney() {
        return numBakeryMoney;
    }

    public void addBakeryMoney(int numBakeryMoney) {
        this.numBakeryMoney += numBakeryMoney;
    }

    public void substractBakeryMoney(int numBakeryMoney) {
        this.numBakeryMoney -= numBakeryMoney;
    }

    /**
     * load the profile's information from the JSON file
     * @param context the application's context
     * @return the user's profile
     */
    public Profile loadProfile(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(PROFILE_FILE, null);

        Type collectionType = new TypeToken<Profile>(){}.getType();

        return new Gson().fromJson(json, collectionType);
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
}
