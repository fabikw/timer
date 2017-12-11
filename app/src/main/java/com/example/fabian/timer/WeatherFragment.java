package com.example.fabian.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.max;

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

    Handler handler = new Handler();
    Runnable runnableUpdateWeather = new Runnable() {
        @Override
        public void run() {
            updateWeather();
      /* and here comes the "trick" */
            handler.postDelayed(this, 600*1000);
        }
    };

    Runnable runnableLastUpdate = new Runnable() {
        @Override
        public void run() {
            updateLastU();
      /* and here comes the "trick" */
            handler.postDelayed(this, 60*1000);
        }
    };

    long lastUpdatedMillis;
    String lastUpdatedString;


    private final DateFormat df = new SimpleDateFormat("HH : mm : ss");


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


        handler.postDelayed(runnableUpdateWeather,300*1000);
        handler.postDelayed(runnableLastUpdate,1000);
        weatherIconImg.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateWeather();
                return true;
            }
        });



        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    updateWeather();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        getContext().registerReceiver(br, filter);


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
                    "\n" + "Humidity: " + parsedData.getString("humidity") + "%" +
                    "\n" + "Precipitation: " + parsedData.getString("precip") + "%");
            currentTemperatureField.setText(parsedData.getString("currentT")+" F");
            //updatedField.setText("Last update: "+ parsedData.getString("lastU"));
            lastUpdatedString = parsedData.getString("lastU");
            highTemperatureField.setText(parsedData.getString("highT") + " F");
            lowTemperatureField.setText(parsedData.getString("lowT") + " F");

            updateLastU();
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

    void updateLastU(){
        try{
            updatedField.setText("Last update: "+ lastUpdatedString + "\n" + prettyPrintTimeDif());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String prettyPrintTimeDif(){
        long now = new Date().getTime();
        long diff = max(0,now - lastUpdatedMillis);
        int minutesAgo = (int)(diff/60000);

        String s = minutesAgo + " minute(s) ago";
        return s;

    }

}
