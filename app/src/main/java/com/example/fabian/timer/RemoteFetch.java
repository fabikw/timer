package com.example.fabian.timer;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Fabian on 11-Jan-17.
 */

public class RemoteFetch {

    private static final String YAHOO_API =
            "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";

    public static final String query = "select * where woeid in (select woeid from geo.places(1) where text = \"%s\")";

    public static JSONObject getJSON(Context context, String city){
        try {
            String q = String.format(query,city);
            URL url = new URL(String.format(YAHOO_API, q));
            URLConnection connection =
                    (URLConnection)url.openConnection();

            //connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
