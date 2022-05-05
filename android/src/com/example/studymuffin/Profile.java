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
    private int numPoints;
    public static String DATA_FILE = "com.example.studymuffin.profile";

    public Profile(String firstName, String lastName, int numPoints) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numPoints = numPoints;
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
        return numPoints;
    }

    public void addPoints(int numPoints) {
        this.numPoints += numPoints;
    }

    public void substractPoints(int numPoints) {
        this.numPoints -= numPoints;
    }

    /**
     * load the profile's information from the JSON file
     * @param context the application's context
     * @return the user's profile
     */
    public Profile loadProfile(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(DATA_FILE, null);

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

        editor.putString(DATA_FILE, json);

        editor.apply();

        // TODO upload the updated profile to the user's account in Firebase
    }
}
