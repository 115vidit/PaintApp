package com.example.paintapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager
{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public Context context;

    private static final String LOGIN = "IS_LOGIN";
    static final String NAME = "NAME";
    private static final String EMAIL = "EMAIL";

    @SuppressLint("CommitPrefEdits")
    SessionManager(Context context){
        this.context = context;
        int PRIVATE_MODE = 0;
        sharedPreferences = context.getSharedPreferences("LOGIN", PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    void createSession(String name, String email){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    private boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    void checklogin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MenuActivity)context).finish();
        }
    }

    HashMap<String, String > getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));

        return user;
    }
    void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MenuActivity)context).finish();
    }
}
