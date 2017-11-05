package com.example.fabian.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    CountDownTimer count = null;
    TextView ch;
    Ringtone r = null;
    Long timeLeft = null;
    Switch sw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        ch = (TextView)findViewById(R.id.chronoText);
        sw = (Switch)findViewById(R.id.switch_alarm);
        sw.setChecked(false);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("Time Remaining")){
                createTimer((int)savedInstanceState.getLong("Time Remaining"));
            }
            return;
        }
        if (findViewById(R.id.fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new WeatherFragmentAeris()).commit();
        }

        PreferenceManager.setDefaultValues(this,R.xml.pref_general, false);
        setValues();
    }

    public void createTimer_1(View view){
        createTimer(60);
    }

    public void createTimer_8(View view) {
        createTimer(480);
    }

    public void createTimer_5(View view){
        createTimer(300);
    }

    public void createTimer_20(View view){
        createTimer(1200);
    }

    public void createTimer_10(View view){
        createTimer(600);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (count != null){
            count.cancel();
        }
        if (timeLeft != null){
            outState.putLong("Time Remaining", timeLeft);
        }else{
            outState.remove("Time Remaining");
        }
    }

    public void onStop(){
        if (count != null){
            count.cancel();
            count = null;
        }
        super.onStop();
    }

    public void createTimer(View view){
        int[] values = new ValuesPreference(this).getCValues();
        switch (view.getId()){
            case R.id.imageButtonA:
                createTimer(values[0]*60);
                break;
            case R.id.imageButtonB:
                createTimer(values[1]*60);
                break;
            case R.id.imageButtonC:
                createTimer(values[2]*60);
                break;
        }
    }

    public void createTimer(int seconds){
        if (r != null && r.isPlaying()){
            r.stop();
        }
        ch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count!= null){
                    count.cancel();
                    ch.setText("canceled");
                    return true;
                }
                return false;
            }
        });
        if (count != null){
            count.cancel();
        }
        count = new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                timeLeft = sec;
                ch.setText(String.format("%02d:%02d",sec/60,sec%60));
            }

            public void onFinish() {
                ch.setText("done!");
                timeLeft = null;
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                    new CountDownTimer(1000*(sw.isChecked() ? 120 : 60),1000) { //Stops the sound after 1 or 2 minutes (depending on sw).
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (r.isPlaying()){
                                r.stop();
                            }
                        }
                    }.start();
                    ch.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (r.isPlaying()) {
                                r.stop();
                            }
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_city:
                showInputDialog();
                return true;
            case R.id.change_values:
                Intent i = new Intent(this,TimersPrefs.class);
                startActivityForResult(i,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
                setValues();
        }
    }

    public void setValues(){
        int[] values = new ValuesPreference(this).getCValues();
        ((ImageButton) findViewById(R.id.imageButtonA)).setImageResource(getResources().getIdentifier("timer"+values[0],"drawable",getPackageName()));
        ((ImageButton) findViewById(R.id.imageButtonB)).setImageResource(getResources().getIdentifier("timer"+values[1],"drawable",getPackageName()));
        ((ImageButton) findViewById(R.id.imageButtonC)).setImageResource(getResources().getIdentifier("timer"+values[2],"drawable",getPackageName()));
        ((TextView) findViewById(R.id.textA)).setText(values[0] + " minutes");
        ((TextView) findViewById(R.id.textB)).setText(values[1] + " minutes");
        ((TextView) findViewById(R.id.textC)).setText(values[2] + " minutes");
    }

    public boolean onCreateOptionsMenu(Menu my_menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather, my_menu);

        return true;
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        String city = new CityPreference(this).getCity();
        input.setText(city);
        input.setSelection(0,city.length());
        builder.setView(input);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.show();
        input.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }
}
