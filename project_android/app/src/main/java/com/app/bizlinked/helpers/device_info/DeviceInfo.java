package com.app.bizlinked.helpers.device_info;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.util.HashMap;


public class DeviceInfo {



    public static HashMap<String, Object> getHardwareInfo(Context context){

        HashMap<String, Object> deviceHardwareInfo = new HashMap<>();

        deviceHardwareInfo.put("DEVICE", Build.DEVICE);
        deviceHardwareInfo.put("SERIAL", Build.SERIAL);
        deviceHardwareInfo.put("MODEL", Build.MODEL);
        deviceHardwareInfo.put("ID", Build.ID);
        deviceHardwareInfo.put("MANUFACTURE", Build.MANUFACTURER);
        deviceHardwareInfo.put("BRAND", Build.BRAND);
        deviceHardwareInfo.put("TYPE", Build.TYPE);
        deviceHardwareInfo.put("USER", Build.USER);
        deviceHardwareInfo.put("BASE", Build.VERSION_CODES.BASE);
        deviceHardwareInfo.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
        deviceHardwareInfo.put("SDK_INT", Build.VERSION.SDK_INT);
        deviceHardwareInfo.put("BOARD", Build.BOARD);
        deviceHardwareInfo.put("HOST", Build.HOST);
        deviceHardwareInfo.put("FINGERPRINT", Build.FINGERPRINT);
        deviceHardwareInfo.put("BASE_OS", Build.VERSION.BASE_OS);
        deviceHardwareInfo.put("RELEASE", Build.VERSION.RELEASE);


        return deviceHardwareInfo;
    }

    public static String getDeviceID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static long getDeviceInstalledDateTimeInMillis(Context context){
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static String getScreenSize(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String screenSizeString =  "N/A";
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                screenSizeString = "LARGE_SCREEN";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                screenSizeString = "NORMAL_SCREEN";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                screenSizeString = "SMALL_SCREEN";
                break;

        }

        return screenSizeString;

    }

    public static String getScreenResolution(Context context){

        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = null;
            if (wm != null) {
                display = wm.getDefaultDisplay();
            }
            DisplayMetrics metrics = new DisplayMetrics();
            if (display != null) {
                display.getMetrics(metrics);
            }
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            return "" + width + "," + height + "";

        }catch (Exception e){
            e.getMessage();
        }

        return "N/A";
    }

    public static long getDeviceUpdatedDateTimeInMillis(Context context){
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

}


