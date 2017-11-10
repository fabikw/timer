package com.example.fabian.timer;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Fabian on 11-Jan-17.
 */

public class RemoteFetchDarkSky {

    private static final String DARK_SKY_API =
            "https://api.darksky.net/forecast/%s/%f,%f?exclude=minutely,hourly,alerts,flags";

    @Nullable
    public static JSONObject getJSON(Context context, double lat, double lon){
        try {
            URL url = new URL(String.format(DARK_SKY_API, context.getString(R.string.darksky_client_secret), lat,lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

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
            if(!data.has("currently")){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
