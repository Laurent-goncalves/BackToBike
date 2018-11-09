package com.g.laurent.backtobike.Utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NotificationUtils {

    public static final String NEW_INVITATION = "new_invit";
    public static final String CANCEL_EVENT = "cancel_event";
    public static final String REJECT_EVENT = "reject_event";
    public static final String ACCEPT_INVITATION = "accept_invitation";
    public static final String REJECT_INVITATION = "reject_invitation";
    public static final String NEW_FRIEND_REQUEST = "new_friend_request";
    public static final String FRIEND_HAS_ACCEPTED = "friend_has_accepted";
    public static final String FRIEND_HAS_REJECTED = "friend_has_rejected";

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------------- SENDING ------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public static String buildContentNotification(String loginSender, String typeUpdate){
        return loginSender + "[]" + typeUpdate;
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static void post(Context context, String json, Callback callback) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","key=" + context.getResources().getString(R.string.server_key))
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void configureAndSendNotification(Context context, String idReceiver, String loginSender, String typeUpdate){

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverUserTokenDevice(idReceiver, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean datasOK, String token) {
                try {
                    // Prepare JSON object
                    JSONObject jsonObject = new JSONObject();
                    JSONObject param = new JSONObject();
                    jsonObject.put("data", param);
                    jsonObject.put("to", token);
                    jsonObject.put("priority", "high");
                    jsonObject.put("content_available", true);

                    JSONObject notification = new JSONObject();
                    notification.put("title", "new notification");
                    notification.put("body", buildContentNotification(loginSender, typeUpdate));
                    jsonObject.put("data", notification);

                    // send notification to Firebase FCM
                    sendNotification(context, jsonObject);

                } catch (JSONException ex) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_send_notification) + "\n" + ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private static void sendNotification(Context context, JSONObject jsonObject){

        post(context, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(context, context.getResources().getString(R.string.error_send_notification), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {}
        });
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------- NOTIFICATION SHOWING -----------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    private static String buildMessageNotification(Context context, String content){

        String message = null;
        String loginSender = content.substring(0, content.indexOf("[]"));
        String typeUpdate = content.substring(content.indexOf("[]") + 2 , content.length());

        switch(typeUpdate){

            case NEW_INVITATION:
                message = loginSender + " " + context.getResources().getString(R.string.notification_new_invit);
                break;
            case CANCEL_EVENT:
                message = loginSender + " " + context.getResources().getString(R.string.notification_cancel_event);
                break;
            case REJECT_EVENT:
                message = loginSender + " " + context.getResources().getString(R.string.notification_reject_event);
                break;
            case ACCEPT_INVITATION:
                message = loginSender + " " + context.getResources().getString(R.string.notification_accept_invit);
                break;
            case REJECT_INVITATION:
                message = loginSender + " " + context.getResources().getString(R.string.notification_reject_invit);
                break;
            case NEW_FRIEND_REQUEST:
                message = loginSender + " " + context.getResources().getString(R.string.notification_new_friend_request);
                break;
            case FRIEND_HAS_ACCEPTED:
                message = loginSender + " " + context.getResources().getString(R.string.notification_friend_has_accepted);
                break;
            case FRIEND_HAS_REJECTED:
                message = loginSender + " " + context.getResources().getString(R.string.notification_friend_has_rejected);
                break;
        }
        return message;
    }

    private static String buildTitleNotification(Context context, String content) {

        String title = null;
        String typeUpdate = content.substring(content.indexOf("[]") + 2 , content.length());

        switch(typeUpdate){

            case NEW_INVITATION:
                title = context.getResources().getString(R.string.notification_new_invit_title);
                break;
            case CANCEL_EVENT:
                title = context.getResources().getString(R.string.notification_cancel_event_title);
                break;
            case REJECT_EVENT:
                title = context.getResources().getString(R.string.notification_reject_event_title);
                break;
            case ACCEPT_INVITATION:
                title = context.getResources().getString(R.string.notification_accept_invit_title);
                break;
            case REJECT_INVITATION:
                title = context.getResources().getString(R.string.notification_reject_invit_title);
                break;
            case NEW_FRIEND_REQUEST:
                title = context.getResources().getString(R.string.notification_new_friend_request_title);
                break;
            case FRIEND_HAS_ACCEPTED:
                title = context.getResources().getString(R.string.notification_friend_has_accepted_title);
                break;
            case FRIEND_HAS_REJECTED:
                title = context.getResources().getString(R.string.notification_friend_has_rejected_title);
                break;
        }
        return title;
    }

    public static void showNotificationMessage(Context context, final String content) {
        // Check for empty push message
        if (TextUtils.isEmpty(content))
            return;

        // Define message to show
        String message = buildMessageNotification(context, content);

        // Define title notification
        String title = buildTitleNotification(context, content);

        // Configure notification and send
        String CHANNEL_ID = "my_channel";

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
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message);

        if (notificationManager != null)
            notificationManager.notify(1, builder.build());

    }
}

