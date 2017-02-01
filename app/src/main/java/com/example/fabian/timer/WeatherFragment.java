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

import com.hamweather.aeris.communication.Action;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisCommunicationTask;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.AerisRequest;
import com.hamweather.aeris.communication.Endpoint;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.communication.fields.ForecastsFields;
import com.hamweather.aeris.communication.fields.ObservationFields;
import com.hamweather.aeris.communication.loaders.ForecastsTask;
import com.hamweather.aeris.communication.loaders.ForecastsTaskCallback;
import com.hamweather.aeris.communication.loaders.ObservationsTask;
import com.hamweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.hamweather.aeris.communication.parameter.FilterParameter;
import com.hamweather.aeris.communication.parameter.LimitParameter;
import com.hamweather.aeris.communication.parameter.ParameterBuilder;
import com.hamweather.aeris.communication.parameter.PlaceParameter;
import com.hamweather.aeris.model.AerisError;
import com.hamweather.aeris.model.AerisLocation;
import com.hamweather.aeris.model.AerisResponse;
import com.hamweather.aeris.model.ForecastPeriod;
import com.hamweather.aeris.model.Observation;
import com.hamweather.aeris.model.Place;
import com.hamweather.aeris.response.ForecastsResponse;
import com.hamweather.aeris.response.ObservationResponse;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

    ForecastsTask task;

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
        updateWeatherData(new CityPreference(getActivity()).getCity());
        timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                updateWeatherData(new CityPreference(getActivity()).getCity());
            }
        }, 300*1000, 600*1000);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        AerisEngine.initWithKeys(getActivity().getString(R.string.aeris_client_id), getActivity().getString(R.string.aeris_client_secret), getActivity());




    }

    private void updateWeatherData(final String city){
        PlaceParameter place = new PlaceParameter(city);
        ParameterBuilder builder = new ParameterBuilder()
                .withFilter("12hr")
                .withLimit(1)
                ;

        task = new ForecastsTask(getActivity(),
                new ForecastsTaskCallback() {

                    @Override
                    public void onForecastsFailed(AerisError error) {
                        System.out.println("ERROR");
                    }

                    @Override
                    public void onForecastsLoaded(List responses) {
                        renderWeather(responses);
                    }

                });
        try {
            task.requestClosest(place,builder.build());
        }catch(Exception e){
            task.onFail(new AerisError());
        }
        /*new Thread(){
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
                            renderWeather();
                        }
                    });
                }
            }
        }.start();*/
    }

    private void renderWeather(List responses){
        ForecastsResponse obb = (ForecastsResponse)responses.get(0);
        ForecastPeriod ob = obb.getPeriod(0);
        AerisRequest request = new AerisRequest(new Endpoint(EndpointType.PLACES), Action.CLOSEST,new PlaceParameter(obb.getLocation()), new LimitParameter(1));
        AerisCommunicationTask t = new AerisCommunicationTask(getActivity(),
                new AerisCallback() {
                    @Override
                    public void onResult(EndpointType endpoint, AerisResponse response) {
                        Place loc = response.getResponse(0).place;
                        cityField.setText(loc.name.toUpperCase(Locale.US)+", "+ (loc.country.equalsIgnoreCase("US") ? loc.state.toUpperCase(Locale.US) : loc.country.toUpperCase(Locale.US)));
                    }
                },request);
        try {
            t.execute();
        }catch(Exception e){
        }
        detailsField.setText(ob.weather.toUpperCase(Locale.US) +
                "\n" + "Humidity:" + ob.humidity + "%" +
                "\n" + "Pressure:" + ob.pressureIN + " in");
        currentTemperatureField.setText(ob.tempF+" F");
        DateFormat df = DateFormat.getDateTimeInstance();
        updatedField.setText("Last update: "+df.format(new Date(ob.timestamp.longValue()*1000)));
        highTemperatureField.setText(ob.maxTempF+" F");
        lowTemperatureField.setText(ob.minTempF+" F");
        /*try {
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

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }*/
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

        //timer.cancel();
        updateWeatherData(city);
        /*timer.schedule( new TimerTask() {
            public void run() {
                updateWeatherData(new CityPreference(getActivity()).getCity());
            }
        }, 300*1000, 600*1000);*/
    }
}
