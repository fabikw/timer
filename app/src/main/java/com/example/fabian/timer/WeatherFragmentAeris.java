package com.example.fabian.timer;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherFragmentAeris extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView highTemperatureField;
    TextView lowTemperatureField;
    //TextView weatherIcon;
    ImageView weatherIconImg;
    Timer timer, timer2;


    Handler handler;

    public WeatherFragmentAeris(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_aeris, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        highTemperatureField = (TextView)rootView.findViewById(R.id.high_temperature_field);
        lowTemperatureField = (TextView)rootView.findViewById(R.id.low_temperature_field);
        //weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIconImg = (ImageView)rootView.findViewById(R.id.weather_icon_img);


        //weatherIcon.setTypeface(weatherFont);
        updateWeatherData(new CityPreference(getActivity()).getCity());
        updateHighLow(new CityPreference(getActivity()).getCity());
        timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                updateWeatherData(new CityPreference(getActivity()).getCity());
            }
        }, 300*1000, 600*1000);
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                updateHighLow(new CityPreference(getActivity()).getCity());
            }
        },3600*1000,7200*1000);
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

        ObservationsTask task = new ObservationsTask(getActivity(),
                new ObservationsTaskCallback() {

                    @Override
                    public void onObservationsFailed(AerisError error) {
                        System.out.println("ERROR");
                    }

                    @Override
                    public void onObservationsLoaded(List responses) {
                        renderWeather(responses);
                    }

                });
        try {
            task.requestClosest(place);
        }catch(Exception e){
            task.onFail(new AerisError());
        }
    }

    private void updateHighLow(final String city){
        PlaceParameter place = new PlaceParameter(city);
        ParameterBuilder builder = new ParameterBuilder().withFilter("day").withLimit(1);

        ForecastsTask task = new ForecastsTask(getActivity(),
                new ForecastsTaskCallback() {

                    @Override
                    public void onForecastsFailed(AerisError error) {
                        System.out.println("ERROR");
                    }

                    @Override
                    public void onForecastsLoaded(List responses) {
                        renderHighLow(responses);
                    }

                });
        try {
            task.requestClosest(place,builder.build());
        }catch(Exception e){
            task.onFail(new AerisError());
        }
    }

    private void renderHighLow(List responses){
        ForecastsResponse obb = (ForecastsResponse)responses.get(0);
        ForecastPeriod ob = obb.getPeriod(0);
        highTemperatureField.setText(ob.maxTempF+" F");
        lowTemperatureField.setText(ob.minTempF+" F");
    }

    private void renderWeather(List responses){
        ObservationResponse obb = (ObservationResponse)responses.get(0);
        Observation ob = obb.getObservation();
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
        //highTemperatureField.setText(ob.maxTempF+" F");
        //lowTemperatureField.setText(ob.minTempF+" F");
        int id = getActivity().getResources().getIdentifier(ob.icon.split("\\.")[0],"drawable",getContext().getPackageName());
        if (id != 0) {
            Drawable d = getResources().getDrawable(id);
            weatherIconImg.setImageDrawable(d);
        }


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
