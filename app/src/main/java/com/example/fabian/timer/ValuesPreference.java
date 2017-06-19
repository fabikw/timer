package com.example.fabian.timer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by fabian on 6/19/17.
 */

public class ValuesPreference {

    SharedPreferences prefs;

    public ValuesPreference(Activity activity){
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    int[] getCValues(){
        int[] values = new int[3];
        values[0] = prefs.getInt("valuesA",5);
        values[1] = prefs.getInt("valuesB",10);
        values[2] = prefs.getInt("valuesC",20);
        return values;

    }

    void setValues(int[] values){
        prefs.edit().putInt("valuesA", values[0]).commit();
        prefs.edit().putInt("valuesB", values[1]).commit();
        prefs.edit().putInt("valuesC", values[2]).commit();
    }
}
