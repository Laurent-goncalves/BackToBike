package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.content.Intent;

import com.g.laurent.backtobike.Utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;

        String content = remoteMessage.getData().get("body");

        if(content!=null){
            NotificationUtils.showNotificationMessage(getApplicationContext(), content);
            updateMyActivity(getApplicationContext(), "message");
        }
    }

    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }
}
