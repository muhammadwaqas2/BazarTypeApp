package com.app.bizlinked.helpers.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

public class NetworkUtils {

//    public static boolean isOnline(Context context) {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo netInfo = cm.getActiveNetworkInfo();
//            //should check null because in airplane mode it will be null
//            return (netInfo != null && netInfo.isConnected());
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isOnline(Context context) {
        boolean isOnline = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());  // need ACCESS_NETWORK_STATE permission
            isOnline = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOnline;
    }


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if(manager == null)
            return false;

        // 3g-4g available
        boolean is3g = false;
        if(manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
            is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
        }
        // wifi available
        boolean isWifi = false;
        if(manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null){
            isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();

        }

        System.out.println(is3g + " net " + isWifi);


        return (is3g || isWifi);
    }
}
