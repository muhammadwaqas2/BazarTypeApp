package com.app.bizlinked.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.helpers.common.NetworkUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            android.os.Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    if (NetworkUtils.isOnline(context)) {
                        MainActivity.networkStatusText(context, true);
                        Log.e("keshav", "Online Connect Intenet ");
                    } else {
                        MainActivity.networkStatusText(context, false);
                        Log.e("keshav", "Conectivity Failure !!! ");
                    }
                }
            };
            handler.postDelayed(delayrunnable, 6000);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}