package com.jonasalejo.appcap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerReceiver extends BroadcastReceiver {

    public TimerReceiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BlockService.class);
        context.stopService(i);
        BlockService.scheduleTaskExecutor.shutdown();

    }

}
