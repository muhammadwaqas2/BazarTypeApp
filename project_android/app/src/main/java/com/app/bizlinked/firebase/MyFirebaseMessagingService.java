package com.app.bizlinked.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.helpers.common.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null) {
            if (remoteMessage.getNotification() != null) {
                Log.d("PUSH_MESSAGE_NTFCN", remoteMessage.getNotification().toString());
            }

            if (remoteMessage.getData() != null) {
                Log.d("PUSH_MESSAGE_DATA", remoteMessage.getData().toString());
            }


            //For Silent Notification
            if(remoteMessage.getNotification() != null && !Utils.isEmptyOrNull(remoteMessage.getNotification().getTitle())
                                                       && !Utils.isEmptyOrNull(remoteMessage.getNotification().getBody())){
                createNotification(remoteMessage);
            }else if(remoteMessage.getData() != null && !Utils.isEmptyOrNull(remoteMessage.getData().get("title"))
                                                     && (!Utils.isEmptyOrNull(remoteMessage.getData().get("body"))
                                                     || !Utils.isEmptyOrNull(remoteMessage.getData().get("message")))) {
                createNotification(remoteMessage);
            }
        }

        sendBroadcastToActivity(remoteMessage, null);
    }

    private void createNotification(RemoteMessage remoteMessage) {

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "bizlinked-channel";// The user-visible name of the channel.
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if(remoteMessage.getData() != null){
            //Data bundle send
            intent.putExtra(AppConstant.PUSH_CONFIG.PUSH_DATA_BODY, remoteMessage.getData().toString());
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500,500,500,500,500};
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        String title = "Bizlinked";
        String bodyText = "";

        //For Title
        if(remoteMessage.getNotification() != null && !Utils.isEmptyOrNull(remoteMessage.getNotification().getTitle())){
            title = remoteMessage.getNotification().getTitle();
        }else if(remoteMessage.getData() != null && !Utils.isEmptyOrNull(remoteMessage.getData().get("title"))) {
            title = remoteMessage.getData().get("title");
        }

        //For Body
        if(remoteMessage.getNotification() != null && !Utils.isEmptyOrNull(remoteMessage.getNotification().getBody())){
            bodyText = remoteMessage.getNotification().getBody();
        }else if(remoteMessage.getData() != null && !Utils.isEmptyOrNull(remoteMessage.getData().get("body"))) {
            bodyText = remoteMessage.getData().get("body");
        }else if(remoteMessage.getData() != null && !Utils.isEmptyOrNull(remoteMessage.getData().get("message"))) {
            bodyText = remoteMessage.getData().get("message");
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setBadgeIconType(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(bodyText)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
        // Issue the notification.
        if (mNotificationManager != null) {
            mNotificationManager.notify(1 , notification.build());
        }


    }


    @Override
    public void onNewToken(String token) {

        sendBroadcastToActivity(null, token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    private void sendBroadcastToActivity(RemoteMessage message, String token) {

        Log.d("PUSH_MESSAGE", "sendBroadcastToActivity");
        Intent intent = new Intent(AppConstant.PUSH_CONFIG.PUSH_SERVICE_FILTER);

        if (message != null) {
            if (message.getData() != null) {
                intent.putExtra(AppConstant.PUSH_CONFIG.PUSH_DATA_BODY, message.getData().toString());
            }
        } else if (token != null) {
            intent.putExtra(AppConstant.PUSH_CONFIG.PUSH_TOKEN_KEY, token);
        }

        //Fire Broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

