package com.app.bizlinked.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppInstallUnInstallReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("BroadcastReceiver", "onReceive called " +  intent.getAction());

        this.context = context;

        try {
            // when package removed
             if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                Log.v("BroadcastReceiver", "onReceive called " + " PACKAGE_REMOVED ");
                Toast.makeText(context, " onReceive !!!! PACKAGE_REMOVED", Toast.LENGTH_LONG).show();
                appInstallAndUnInstallInfoSendToServer(context, "un-install");
            }
            // when package changes
            else if (intent.getAction().equals("android.intent.action.PACKAGE_CHANGED")) {

                Log.v("BroadcastReceiver", "onReceive called " + "PACKAGE_CHANGED");
                Toast.makeText(context, " onReceive !!!!." + "PACKAGE_CHANGED",
                        Toast.LENGTH_LONG).show();

                appInstallAndUnInstallInfoSendToServer(context, "install");
            }
            // when package installed
            else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

                Log.v("BroadcastReceiver", "onReceive called " + "PACKAGE_ADDED");
                Toast.makeText(context, " onReceive !!!!." + "PACKAGE_ADDED",
                        Toast.LENGTH_LONG).show();

                appInstallAndUnInstallInfoSendToServer(context, "install");
            }
            // when package Data Cleared
            else if (intent.getAction().equals("android.intent.action.PACKAGE_DATA_CLEARED")) {

                Log.v("BroadcastReceiver", "onReceive called " + "PACKAGE_DATA_CLEARED");
                Toast.makeText(context, " onReceive !!!!." + "PACKAGE_DATA_CLEARED",Toast.LENGTH_LONG).show();
                appInstallAndUnInstallInfoSendToServer(context, "data-clear");
            }
            // when package Data Cleared
            else if (intent.getAction().equals("android.intent.action.ACTION_PACKAGE_INSTALL")) {

                Log.v("BroadcastReceiver", "onReceive called " + "ACTION_PACKAGE_INSTALL");
                Toast.makeText(context, " onReceive !!!!." + "ACTION_PACKAGE_INSTALL",Toast.LENGTH_LONG).show();
                appInstallAndUnInstallInfoSendToServer(context, "package-install");
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.v("BroadcastReceiver", "onReceive called " + e.getMessage());
            Toast.makeText(context, " onReceive !!!! " + e.getMessage(), Toast.LENGTH_LONG).show();
            //Sentry.captureException(e);
        }
    }


    private void appInstallAndUnInstallInfoSendToServer(Context context, String status) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

            SimpleDateFormat sdf =  new SimpleDateFormat("MM/dd/yyyy");
            String installDate = sdf.format(new Date(pi.firstInstallTime));
            String updateDate = sdf.format(new Date(pi.lastUpdateTime));


            // Get installer package name for current app
            String ipn = pm.getInstallerPackageName(context.getPackageName());



            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            //String androidUUID = UUID.;

            Toast.makeText(context, "Installed Date : " + installDate, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Update Time : " + updateDate, Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, "IPN : " + ipn, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "ID : " + androidId, Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Status : " + status, Toast.LENGTH_LONG).show();
            Log.d("ID : " ,androidId);
//            Toast.makeText(context, "UUID : " + androidUUID, Toast.LENGTH_LONG).show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.v("BroadcastReceiver", "appInstallAndUnInstallInfoSendToServer " + e.getMessage());
            Toast.makeText(context, " appInstallAndUnInstallInfoSendToServer !!!! " + e.getMessage(), Toast.LENGTH_LONG).show();
//            Sentry.captureException(e);
        }
    }

}
