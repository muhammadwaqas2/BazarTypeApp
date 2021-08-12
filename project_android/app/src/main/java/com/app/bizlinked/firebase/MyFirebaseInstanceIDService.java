package com.app.bizlinked.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        //For registration of token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //To displaying token on logcat
        Log.d("TOKEN: ", refreshedToken);
        //Toast.makeText(this, "REFRESH-TOKEN = " + refreshedToken, Toast.LENGTH_SHORT).show();

    }

}
