package com.g.laurent.backtobike.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;


public class Action {

    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String NEED_SYNCHRONIZATION = "need_synchronization";
    private static final String LOGIN_SHARED ="login_shared";

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- FRIEND -----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addNewFriend(Friend friend, Friend user, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Insert friend in database
        FriendsHandler.insertNewFriend(context, friend, userId);

        // Insert friend in Firebase
        if(UtilsApp.isInternetAvailable(context)) {

            // Add new friend on Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.addNewFriend(friend, user);

            // Send notification to user
            NotificationUtils.configureAndSendNotification(context, friend.getId(), user.getLogin(),NotificationUtils.NEW_FRIEND_REQUEST);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void updateFriend(Friend friend, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Update friend in database
        FriendsHandler.updateFriend(context, friend, userId);

        // Update friend in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateFriendsFromUser(userId, friend);
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void deleteFriend(Friend friend, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Update friend in database
        FriendsHandler.deleteFriend(context, friend.getLogin(), userId);

        // Update friend in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.deleteFriend(userId, friend.getId());
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void acceptFriend(Friend friend, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Friend accepted in database
        friend.setAccepted(true);
        FriendsHandler.updateFriend(context, friend, userId);

        if(UtilsApp.isInternetAvailable(context)) {

            // Friend accepted in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.acceptFriend(userId, friend.getId());

            // Send notification to user
            NotificationUtils.configureAndSendNotification(context, friend.getId(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.FRIEND_HAS_ACCEPTED);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void rejectFriend(Friend friend, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Friend accepted in database
        friend.setAccepted(false);
        FriendsHandler.updateFriend(context, friend, userId);

        if(UtilsApp.isInternetAvailable(context)) {

            // Friend accepted in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.rejectFriend(userId, friend.getId());

            // Send notification to user
            NotificationUtils.configureAndSendNotification(context, friend.getId(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.FRIEND_HAS_REJECTED);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ROUTE ------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static int addNewRoute(Route route, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Insert route in database
        int idRoute = RouteHandler.insertNewRoute(context, route, userId);
        route.setId(idRoute);

        // Insert route in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }

        return idRoute;
    }

    public static void updateRoute(Route route, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Update route in database
        RouteHandler.updateRoute(context, route, userId);

        // Update route in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void deleteRoute(Route route, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        route.setValid(false);

        // Delete route in database
        RouteHandler.updateRoute(context, route, userId);

        // Delete route in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- BIKE EVENT -------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addBikeEvent(BikeEvent event, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Insert event in database
        String idEvent = UtilsApp.getIdEvent(event);
        event.setId(idEvent);
        BikeEventHandler.insertNewBikeEvent(context,event, userId);

        if(UtilsApp.isInternetAvailable(context)) {
            // Insert event in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateMyBikeEvent(userId, event);

            // Send invitations to guests
            firebaseUpdate.addInvitationGuests(event);

            // Send notification to each guest
            if(event.getListEventFriends()!=null){
                for(EventFriends eventFriends : event.getListEventFriends()){
                    NotificationUtils.configureAndSendNotification(context, eventFriends.getIdFriend(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.NEW_INVITATION);
                }
            }

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void cancelBikeEvent(BikeEvent event, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Delete event in database
        BikeEventHandler.deleteBikeEvent(context, event, userId);

        // Cancel event and invitations in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.cancelMyBikeEvent(userId, event.getListEventFriends(), event);

            // Send notification of cancellation to each guest
            if(event.getListEventFriends()!=null){
                for(EventFriends eventFriends : event.getListEventFriends()){
                    NotificationUtils.configureAndSendNotification(context, eventFriends.getIdFriend(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.CANCEL_EVENT);
                }
            }
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void deleteBikeEvent(BikeEvent event, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Delete event in database
        BikeEventHandler.deleteBikeEvent(context, event, userId);

        // Cancel event and invitations in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.deleteEvent(userId, event);
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // ----------------------------------------- INVITATION ----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void acceptInvitation(BikeEvent invitation, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        invitation.setStatus(ACCEPTED);

        // Accept invitation in database
        BikeEventHandler.updateBikeEvent(context, invitation, userId);

        if(UtilsApp.isInternetAvailable(context)) {

            // Accept invitation in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.giveAnswerToInvitation(userId, invitation, ACCEPTED);

            // Send notification to organizer
            NotificationUtils.configureAndSendNotification(context, invitation.getOrganizerId(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.ACCEPT_INVITATION);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void rejectInvitation(BikeEvent invitation, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Reject invitation in database
        BikeEventHandler.deleteBikeEvent(context,invitation, userId);

        if(UtilsApp.isInternetAvailable(context)) {

            // Reject invitation in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.giveAnswerToInvitation(userId, invitation, REJECTED);

            // Send notification to organizer
            NotificationUtils.configureAndSendNotification(context, invitation.getOrganizerId(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.REJECT_INVITATION);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void rejectEvent(BikeEvent invitation, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Reject invitation in database
        BikeEventHandler.deleteBikeEvent(context,invitation, userId);

        if(UtilsApp.isInternetAvailable(context)) {

            // Reject invitation in Firebase
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.rejectEvent(userId, invitation);

            // Send notification to organizer
            NotificationUtils.configureAndSendNotification(context, invitation.getOrganizerId(), sharedPref.getString(LOGIN_SHARED, null),NotificationUtils.REJECT_EVENT);

        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    public static void addInvitRouteToMyRoutes(BikeEvent invitation, String userId, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Add route in database
        int idRoute = RouteHandler.insertNewRoute(context, invitation.getRoute(), userId);
        invitation.getRoute().setId(idRoute);

        // Add route in Firebase
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.acceptRoute(userId, invitation.getRoute(), invitation);
        } else {
            sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // ------------------------------------------- ALERT DIALOG ------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void showAlertDialogCancelBikeEvent(BikeEvent event, int position, String userId, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(displayActivity);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.warning_title));
        builder.setMessage(context.getResources().getString(R.string.confirm_cancel_trip));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm), (dialog, id) -> {
                    Action.cancelBikeEvent(event,userId,context);
                    String message = context.getResources().getString(R.string.bike_event_cancelled);
                    displayActivity.removeItemListEvent(position, message);
                }
        )
                .setNegativeButton(R.string.cancel, (dialog, id) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showAlertDialogDeleteRoute(Route route, int position, String userId, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(displayActivity);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.warning_title));
        builder.setMessage(context.getResources().getString(R.string.confirm_delete_route));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm), (dialog, id) -> {
                    Action.deleteRoute(route,userId,context);
                    String message = context.getResources().getString(R.string.delete_route);
                    displayActivity.removeItemListRoutes(position, message);
                }
        )
                .setNegativeButton(R.string.cancel, (dialog, id) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showAlertDialogRejectEvent(BikeEvent event, int position, String userId, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(displayActivity);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.warning_title));
        builder.setMessage(context.getResources().getString(R.string.confirm_reject_event));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm), (dialog, id) -> {
                    Action.rejectEvent(event,userId,context);
                    String message = context.getResources().getString(R.string.reject_event);
                    displayActivity.removeItemListEvent(position, message);
                }
        )
                .setNegativeButton(R.string.cancel, (dialog, id) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showAlertDialogRejectInvitation(BikeEvent invit, int position, String userId, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(displayActivity);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.warning_title));
        builder.setMessage(context.getResources().getString(R.string.confirm_reject_invit));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm), (dialog, id) -> {
                    Action.rejectInvitation(invit,userId,context);
                    String message = context.getResources().getString(R.string.reject_invitation);
                    displayActivity.removeItemListInvits(position, message);
                }
        )
                .setNegativeButton(R.string.cancel, (dialog, id) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
