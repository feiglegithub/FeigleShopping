package com.feigle.shopping.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    public static final String USER = "user";
    public static final String KEY_Name = "name";

    public static String sharedPreferencesRead(Context context,String name,String key,String def){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(key,def);
        return str;
    }
    public static void sharedPreferencesWrite(Context context,String name,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
