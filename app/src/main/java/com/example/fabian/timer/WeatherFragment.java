package com.example.fabian.timer;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabian on 2/3/17.
 */

public class WeatherFragment extends Fragment {


    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView highTemperatureField;
    TextView lowTemperatureField;
    //TextView weatherIcon;
    ImageView weatherIconImg;
    Timer timer;

    Handler handler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather_aeris, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        highTemperatureField = (TextView)rootView.findViewById(R.id.high_temperature_field);
        lowTemperatureField = (TextView)rootView.findViewById(R.id.low_temperature_field);
        weatherIconImg = (ImageView)rootView.findViewById(R.id.weather_icon_img);
        updateWeather();
        timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                updateWeather();
            }
        }, 300*1000, 600*1000);
        weatherIconImg.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateWeather();
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void changeCity(String city){
        updateWeather();
    }


    public void updateWeather(){

    }

    protected JSONObject parseResponse(JSONObject response) throws JSONException{
        return null;
    }

    protected void renderWeather(JSONObject response){
        try {
            JSONObject parsedData = parseResponse(response);
            cityField.setText(parsedData.getString("location"));
            detailsField.setText(parsedData.getString("summary") +
                    "\n" + "Humidity:" + parsedData.getString("humidity") + "%" +
                    "\n" + "Pressure:" + parsedData.getString("pressure") + " in");
            currentTemperatureField.setText(parsedData.getString("currentT")+" F");
            updatedField.setText("Last update: "+ parsedData.getString("lastU"));
            highTemperatureField.setText(parsedData.getString("highT") + " F");
            lowTemperatureField.setText(parsedData.getString("lowT") + " F");

            int id = Integer.parseInt(parsedData.getString("iconID"));
            if (id != 0) {
                Drawable d = getResources().getDrawable(id);
                weatherIconImg.setImageDrawable(d);
            }
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }


    }


}
