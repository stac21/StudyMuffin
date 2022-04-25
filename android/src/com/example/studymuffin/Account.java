package com.example.studymuffin;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Account {
    private String email;
    private String password;
    public static final String ACCOUNT_FILE = "com.example.studymuffin.account_file";

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        //return email;
        // here as temp code. Remove when I can confirm that I can create my own account
        return "sarita.chap@gmail.com";
    }

    public String getPassword() {
        return password;
    }

    /**
     * save the account info locally
     * @param context the application's context
     */
    public void save(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String json = new Gson().toJson(this);

        editor.putString(ACCOUNT_FILE, json);

        editor.apply();
    }

    /**
     *
     * @param context the appliction's context
     * @return the account saved locally to the device. Null if no account (user is a guest)
     */
    public static Account loadAccount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(ACCOUNT_FILE, null);

        Type collectionType = new TypeToken<Account>(){}.getType();

        return new Gson().fromJson(json, collectionType);
    }
}
