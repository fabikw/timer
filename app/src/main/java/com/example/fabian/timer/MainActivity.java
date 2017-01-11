package com.example.fabian.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.AlarmClock.ACTION_SET_TIMER;
import static android.provider.AlarmClock.EXTRA_LENGTH;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_SKIP_UI;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new WeatherFragment())
//                    .commit();
//        }
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Intent intent = new Intent(ACTION_SET_TIMER)
                .putExtra(EXTRA_MESSAGE, "Timer started at "+currentDateandTime)
                .putExtra(EXTRA_LENGTH, seconds)
                .putExtra(EXTRA_SKIP_UI, true);
        Context context = getApplicationContext();
        CharSequence text = "Timer started for "+(seconds/60)+(seconds == 60 ? " minute.": " minutes.");
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        startActivity(intent);
        toast.show();
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
