package com.example.fabian.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CountDownTimer count = null;
    TextView ch;
    Ringtone r = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
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
        ch = (TextView)findViewById(R.id.chronoText);
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

    public void createTimer_10(View view){
        createTimer(600);
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
                ch.setText(String.format("%02d:%02d",sec/60,sec%60));
            }

            public void onFinish() {
                ch.setText("done!");
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                    new CountDownTimer(1000*120,1000) { //Stops the sound after 2 minutes.
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
        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }
        return false;
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
