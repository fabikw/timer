package com.example.fabian.timer;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Fabian on 11-Jan-17.
 */

public class CityPreference {
    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Cambridge, MA");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
