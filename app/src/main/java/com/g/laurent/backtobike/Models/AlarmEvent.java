package com.g.laurent.backtobike.Models;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.BikeEventHandler;


public class AlarmEvent extends BroadcastReceiver {

    private static final String EXTRA_USER_ID ="extra_user_id";
    private static final String EXTRA_TYPE_ALARM ="extra_type_alarm";
    private static final String ALARM_2_DAYS ="alarm_2_days";
    private static final String EXTRA_EVENT_ID ="extra_event_id";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

        if(intent!=null){
            if(intent.getExtras()!=null) {
                String idEvent = intent.getExtras().getString(EXTRA_EVENT_ID, null);
                String typeAlarm = intent.getExtras().getString(EXTRA_TYPE_ALARM, null);
                String userId = intent.getExtras().getString(EXTRA_USER_ID, null);
                createAndShowNotification(idEvent, typeAlarm, userId);
            }
        }
    }

    public void createAndShowNotification(String idEvent, String typeAlarm, String userId){

        // Define title notification
        String title_notif = context.getResources().getString(R.string.alarm_notif_title);

        // Define CHANNEL_ID
        String CHANNEL_ID = context.getResources().getString(R.string.channel_alarm_backtobike);

        // Define text notification
        String nameTrip = null;
        String timeTrip = null;
        String textNotif;

        BikeEvent event = BikeEventHandler.getBikeEvent(context, idEvent, userId);
        if(event!=null) {
            nameTrip = event.getRoute().getName();
            timeTrip = event.getTime();
        }

        if(typeAlarm.equals(ALARM_2_DAYS)){
            textNotif = context.getResources().getString(R.string.notification_title) + " \"" + nameTrip + "\" " +
                context.getResources().getString(R.string.notification_title2days);
        } else {
            textNotif = context.getResources().getString(R.string.notification_title) + " \"" + nameTrip + "\" " +
                    context.getResources().getString(R.string.notification_title4hours) + " " + timeTrip;
        }

        // Start notification configuration
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textNotif))
                .setContentTitle(title_notif)
                .setContentText(textNotif);

        if (notificationManager != null)
            notificationManager.notify(1, builder.build());
    }
}
