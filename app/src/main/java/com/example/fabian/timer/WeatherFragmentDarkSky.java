package com.example.fabian.timer;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hamweather.aeris.communication.Action;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisCommunicationTask;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.AerisRequest;
import com.hamweather.aeris.communication.Endpoint;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.communication.loaders.ForecastsTask;
import com.hamweather.aeris.communication.loaders.ForecastsTaskCallback;
import com.hamweather.aeris.communication.loaders.ObservationsTask;
import com.hamweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.hamweather.aeris.communication.parameter.LimitParameter;
import com.hamweather.aeris.communication.parameter.ParameterBuilder;
import com.hamweather.aeris.communication.parameter.PlaceParameter;
import com.hamweather.aeris.model.AerisError;
import com.hamweather.aeris.model.AerisResponse;
import com.hamweather.aeris.model.ForecastPeriod;
import com.hamweather.aeris.model.Observation;
import com.hamweather.aeris.model.Place;
import com.hamweather.aeris.response.ForecastsResponse;
import com.hamweather.aeris.response.ObservationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherFragmentDarkSky extends WeatherFragment {
    /*Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView highTemperatureField;
    TextView lowTemperatureField;
    //TextView weatherIcon;
    ImageView weatherIconImg;
    Timer timer;



    Handler handler;*/

    public WeatherFragmentDarkSky(){
        handler = new Handler();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateWeatherData(final double lat, final double lon){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetchDarkSky.getJSON(getActivity(), lat, lon);
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
            r.put("location", location(response.getDouble("latitude"), response.getDouble("longitude")));
        }catch(JSONException e){
            r.put("location","");
        }
        JSONObject currentWeather = response.getJSONObject("currently");
        try{
            r.put("summary", currentWeather.getString("summary").toUpperCase());
        }catch(JSONException e){
            r.put("summary", "");
        }
        try{
            r.put("humidity", "" + (int)(100*currentWeather.getDouble("humidity")));
        }catch(JSONException e){
            r.put("humidity", "");
        }
        try{
            r.put("pressure", "" + (int)(0.02953*currentWeather.getDouble("pressure")));
        }catch(JSONException e){
            r.put("pressure", "");
        }
        try{
            r.put("currentT", "" + (int)currentWeather.getLong("temperature"));
        }catch(JSONException e){
            r.put("currentT", "");
        }
        try{
            DateFormat df = DateFormat.getDateTimeInstance();
            r.put("lastU", df.format(new Date(currentWeather.getLong("time")*1000)));
        }catch(JSONException e){
            r.put("lastU", "");
        }
        if (response.has("daily")) {
            try {
                JSONObject dailyData = response.getJSONObject("daily").getJSONArray("data").getJSONObject(0);
                r.put("highT", "" + (int) dailyData.getDouble("temperatureMax"));
                r.put("lowT", "" + (int) dailyData.getDouble("temperatureMin"));
            } catch (JSONException e) {
                r.put("highT", "");
                r.put("lowT", "");
            }
        }else{
            r.put("highT", "");
            r.put("lowT", "");
        }
        try{
            r.put("iconID",(int)getActivity().getResources().getIdentifier(currentWeather.getString("icon").replaceAll("-","_"),"drawable",getContext().getPackageName()));
        }catch(JSONException e){
            r.put("iconID",0);
        }
        return r;
    }

    /*private void renderWeather(JSONObject response){


        /*try {
            cityField.setText(location(response.getDouble("latitude"), response.getDouble("longitude")));
            JSONObject currentWeather = response.getJSONObject("currently");
            detailsField.setText(currentWeather.getString("summary").toUpperCase() +
                    "\n" + "Humidity:" + (int)(100*currentWeather.getDouble("humidity")) + "%" +
                    "\n" + "Pressure:" + (int)(0.02953*currentWeather.getDouble("pressure")) + " in");
            currentTemperatureField.setText((int)currentWeather.getDouble("temperature")+" F");
            DateFormat df = DateFormat.getDateTimeInstance();
            updatedField.setText("Last update: "+df.format(new Date(currentWeather.getLong("time")*1000)));
            if (response.has("daily")) {
                try {
                    JSONObject dailyData = response.getJSONObject("daily").getJSONArray("data").getJSONObject(0);
                    highTemperatureField.setText((int)dailyData.getDouble("temperatureMax") + " F");
                    lowTemperatureField.setText((int)dailyData.getDouble("temperatureMin") + " F");
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            int id = getActivity().getResources().getIdentifier(currentWeather.getString("icon").replaceAll("-","_"),"drawable",getContext().getPackageName());
            if (id != 0) {
                Drawable d = getResources().getDrawable(id);
                weatherIconImg.setImageDrawable(d);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    public void updateWeather(){
        CityPreference cp = new CityPreference(getActivity());
        updateWeatherData(cp.getLat(), cp.getLon());
    }


    public String location(double lat, double lon) {
        try {
            List<Address> ads = new Geocoder(getActivity()).getFromLocation(lat,lon,1);
            if (ads == null) {
                return "";
            }else {
                Address ad = ads.get(0);
                if (ad.getCountryCode().equals("US")) {
                    return ad.getLocality() + ", " + ad.getAdminArea();
                }else {
                    return ad.getLocality() + ", " + ad.getCountryCode();
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
}
