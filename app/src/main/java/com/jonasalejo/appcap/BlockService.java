package com.jonasalejo.appcap;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlockService extends Service {
    private Database dbHelper;
    static ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

    public BlockService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new Database(getBaseContext());
        dbHelper.open();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        final String planName = intent.getExtras().getString("planName");


// This schedule a runnable task every 10 seconds
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Gson gson = new Gson();
                String[] checkedApps = gson.fromJson(dbHelper.fetchCheckedApps(planName), String[].class );

                ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                List l = am.getRunningAppProcesses();
                Iterator i = l.iterator();
                PackageManager pm = getPackageManager();

                while(i.hasNext()) {
                    ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
                    try {
                        CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                        if(Arrays.asList(checkedApps).contains(c.toString())){
                            am.restartPackage(info.processName);
                        }
                    }catch(Exception e) {
                        //Name Not Found Exception
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
