package com.example.fabian.timer;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CountDownTimer count = null;
    TextView ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new WeatherFragmentAeris())
//                    .commit();
//        }
        ch = (TextView)findViewById(R.id.chronoText);
    }

    public void createTimer_1(View view){
        createTimer(60);
    }

    public void createTimer_5(View view){
        createTimer(300);
    }

    public void createTimer_10(View view){
        createTimer(600);
    }

    public void createTimer(int seconds){
        ch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
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
        input.setText(new CityPreference(this).getCity());
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }
}
