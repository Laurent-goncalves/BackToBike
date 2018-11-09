package com.g.laurent.backtobike.Models;

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
        }
    }
}
