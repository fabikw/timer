package com.example.fabian.timer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView highTemperatureField;
    TextView lowTemperatureField;
    TextView weatherIcon;
    Timer timer;


    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        highTemperatureField = (TextView)rootView.findViewById(R.id.high_temperature_field);
        lowTemperatureField = (TextView)rootView.findViewById(R.id.low_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);


        weatherIcon.setTypeface(weatherFont);
        timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                SharedPreferences prefs = ((MainActivity)getActivity()).getPreferences(Activity.MODE_PRIVATE);
                updateWeatherData(prefs.getString("city","Cambridge,US"));
            }
        }, 0, 600*1000);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons-regular-webfont.ttf");
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

    private void renderWeather(JSONObject json){
        try {
            if (json.getJSONObject("query").getInt("count") == 0){
                Toast.makeText(getActivity(), getActivity().getString(R.string.data_not_found), Toast.LENGTH_LONG).show();
                throw new Exception();
            }
            JSONObject channel = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
            cityField.setText(channel.getJSONObject("location").getString("city") +
                    ", " +
                    channel.getJSONObject("location").getString("region") +
                    ", "+
                    channel.getJSONObject("location").getString("country"));
            JSONObject item = channel.getJSONObject("item");
            detailsField.setText(
                    item.getJSONObject("condition").getString("text").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + channel.getJSONObject("atmosphere").getString("humidity") + "%" +
                            "\n" + "Pressure: " + channel.getJSONObject("atmosphere").getString("pressure") + channel.getJSONObject("units").getString("pressure"));
            currentTemperatureField.setText(
                    String.format("%d", (int)item.getJSONObject("condition").getDouble("temp"))+ channel.getJSONObject("units").getString("temperature"));
            JSONObject fore = item.getJSONArray("forecast").getJSONObject(0);
            highTemperatureField.setText("High: "+String.format("%d", (int)fore.getDouble("high")) + channel.getJSONObject("units").getString("temperature"));
            lowTemperatureField.setText("Low: "+String.format("%d", (int)fore.getDouble("low")) + channel.getJSONObject("units").getString("temperature"));

            DateFormat df = DateFormat.getDateTimeInstance();
            DateFormat d = new SimpleDateFormat("E, dd MMM yyyy hh:mm aa z");
            d.setLenient(true);
            String s = item.getString("pubDate");
            String updatedOn = df.format(d.parse(s));
            updatedField.setText("Last update: " + s);

            /*setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);*/
            weatherIcon.setText(WeatherConditionCodes.fromInt(item.getJSONObject("condition").getInt("code")).toString());

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

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

    public void changeCity(String city){
        updateWeatherData(city);
    }
}
