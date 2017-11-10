package com.example.fabian.timer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by Fabian on 11-Jan-17.
 */

public class CityPreference {
    SharedPreferences prefs;
    Geocoder geo;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
        geo = new Geocoder(activity);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Cambridge, MA");
    }

    float getLat(){
        return prefs.getFloat("lat", (float)42.3647559);
    }

    float getLon(){
        return prefs.getFloat("lon",(float)-71.1032591);
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
        try {
            List<Address> addresses = geo.getFromLocationName(city, 1);
            if (addresses != null){
                Address ad = addresses.get(0);
                prefs.edit().putFloat("lat", (float)ad.getLatitude()).commit();
                prefs.edit().putFloat("lon", (float)ad.getLongitude()).commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
