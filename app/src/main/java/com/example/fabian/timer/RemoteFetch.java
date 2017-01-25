package com.example.fabian.timer;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Fabian on 11-Jan-17.
 */

public class RemoteFetch {

    private static final String YAHOO_API =
            "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";

    public static final String query = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text = \"%s\")";

    public static JSONObject getJSON(Context context, String city){
        try {
            String q = URLEncoder.encode(String.format(query,city),"UTF-8");
            q = q.replace("+","%20");
            URL url = new URL(String.format(YAHOO_API, q));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
            int code = connection.getResponseCode();
            // This value will be 404 if the request was not
            // successful
            if(code != 200){
                return null;
            }

            InputStream i = connection.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(i));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());


            return data;
        }catch(Exception e){
            return null;
        }
    }
}
