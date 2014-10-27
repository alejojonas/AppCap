package com.jonasalejo.appcap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class Timer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_edit);


        Button startbtn = (Button) findViewById(R.id.start_timer_btn);
        final DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
        final TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
        final String planName = getIntent().getExtras().getString("planName");

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int day = dp.getDayOfMonth();
                int month = dp.getMonth();
                int year = dp.getYear();
                int hour = tp.getCurrentHour();
                int min = tp.getCurrentMinute();
                c.set(year, month, day, hour, min, 0);

                Bundle bundle = new Bundle();
                bundle.putString("planName", planName);
                Intent myIntent1 = new Intent(getBaseContext(), BlockService.class);
                myIntent1.putExtras(bundle);
                getBaseContext().startService(myIntent1);

                Intent myIntent2 = new Intent(getBaseContext(), TimerReceiver.class);
                myIntent2.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent2, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);

                finish();
            }
        });
    }

}

