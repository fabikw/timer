package com.example.fabian.timer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherFragmentOpen extends WeatherFragment{
    Typeface weatherFont;
    TextView weatherIcon;

    public WeatherFragmentOpen(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    protected JSONObject parseResponse(JSONObject response) throws JSONException {
        JSONObject r = new JSONObject();
        try{
            r.put("location", response.getString("location").toUpperCase() + "," + response.getJSONObject("sys").getString("country"));
        }catch(JSONException e){
            r.put("location","");
        }
        JSONObject details = response.getJSONArray("weather").getJSONObject(0);
        JSONObject main = response.getJSONObject("main");
        try{
            r.put("summary", details.getString("description").toUpperCase());
        }catch(JSONException e){
            r.put("summary", "");
        }
        try{
            r.put("humidity", "" + main.getString("humidity"));
        }catch(JSONException e){
            r.put("humidity", "");
        }
        try{
            r.put("pressure", "" + (int)(0.02953*main.getDouble("pressure")));
        }catch(JSONException e){
            r.put("pressure", "");
        }
        try{
            r.put("currentT", "" + (int)(main.getDouble("temp")*9.0/5+32));
        }catch(JSONException e){
            r.put("currentT", "");
        }
        try{
            DateFormat df = DateFormat.getDateTimeInstance();
            r.put("lastU", df.format(new Date(response.getLong("dt")*1000)));
            lastUpdatedMillis = response.getLong("dt")*1000;
        }catch(JSONException e){
            r.put("lastU", "");
        }
        r.put("highT", "");
        r.put("lowT", "");
        r.put("iconID",0);
        setWeatherIcon(details.getInt("id"),
                response.getJSONObject("sys").getLong("sunrise") * 1000,
                response.getJSONObject("sys").getLong("sunset") * 1000);

        return r;
    }

    /*protected void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);



        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }*/

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void updateWeather(){
        CityPreference cp = new CityPreference(getActivity());
        updateWeatherData(cp.getCity());
    }
    
}
