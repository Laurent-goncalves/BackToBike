package com.g.laurent.backtobike.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.AnswerListener;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Action {

    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- FRIEND -----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addNewFriend(Friend friend, Friend user, String userId, Context context){

        // Insert friend in database
        FriendsHandler.insertNewFriend(context, friend, userId);

        // Insert friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.addNewFriend(friend, user);
    }

    public static void updateFriend(Friend friend, String userId, Context context){

        // Update friend in database
        FriendsHandler.updateFriend(context, friend, userId);

        // Update friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateFriendsFromUser(userId, friend);
    }

    public static void deleteFriend(Friend friend, String userId, Context context){

        // Update friend in database
        FriendsHandler.deleteFriend(context, friend.getLogin(), userId);

        // Update friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.deleteFriend(userId, friend);
    }

    public static void acceptFriend(Friend friend, String userId, Context context){

        // Friend accepted in database
        friend.setAccepted(true);
        FriendsHandler.updateFriend(context, friend, userId);

        // Friend accepted in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.acceptFriend(userId, friend);
    }

    public static void rejectFriend(Friend friend, String userId, Context context){

        // Friend accepted in database
        friend.setAccepted(false);
        FriendsHandler.updateFriend(context, friend, userId);

        // Friend accepted in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.rejectFriend(userId,friend);
    }

    public static int getNumberFriendRequests(String userId, Context context){

        List<Friend> listFriendRequest = FriendsHandler.getListFriends(context,userId);
        int counter = 0;
        if(listFriendRequest.size()>0){
            for(Friend friend : listFriendRequest){
                if(friend.getAccepted()!=null){
                    if(!friend.getAccepted()){
                        counter++;
                    }
                } else
                    counter++;
            }
        }

        return counter;
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ROUTE ------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static int addNewRoute(Route route, String userId, Context context){

        // Insert route in database
        int idRoute = RouteHandler.insertNewRoute(context, route, userId);
        route.setId(idRoute);

        // Insert route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());

        return idRoute;
    }

    public static void updateRoute(Route route, String userId, Context context){

        // Update route in database
        RouteHandler.updateRoute(context, route, userId);

        // Update route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
    }

    public static void deleteRoute(Route route, String userId, Context context){

        route.setValid(false);

        // Delete route in database
        RouteHandler.updateRoute(context, route, userId);

        // Delete route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- BIKE EVENT -------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addBikeEvent(BikeEvent event, String userId, Context context){

        // Insert event in database
        String idEvent = UtilsApp.getIdEvent(event);
        event.setId(idEvent);
        BikeEventHandler.insertNewBikeEvent(context,event, userId);

        // Insert event in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyBikeEvent(userId, event);

        // Send invitations to guests
        firebaseUpdate.addInvitationGuests(event);
    }

    public static void cancelBikeEvent(BikeEvent event, String userId, Context context){

        // Delete event in database
        BikeEventHandler.deleteBikeEvent(context, event, userId);

        // Cancel event and invitations in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.cancelMyBikeEvent(userId, event.getListEventFriends(), event);
    }

    // ---------------------------------------------------------------------------------------------------------
    // ----------------------------------------- INVITATION ----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static int getNumberInvitations(String userId, Context context){
        List<BikeEvent> listInvitations = BikeEventHandler.getAllInvitations(context,userId);
        return listInvitations.size();
    }

    public static void acceptInvitation(BikeEvent invitation, String userId, Context context){

        invitation.setStatus(ACCEPTED);

        // Accept invitation in database
        BikeEventHandler.updateBikeEvent(context, invitation, userId);

        // Accept invitation in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.giveAnswerToInvitation(userId, invitation,ACCEPTED);
    }

    public static void rejectInvitation(BikeEvent invitation, String userId, Context context){

        invitation.setStatus(REJECTED);

        // Reject invitation in database
        BikeEventHandler.updateBikeEvent(context,invitation, userId);

        // Reject invitation in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.giveAnswerToInvitation(userId,invitation,REJECTED);
    }

    public static void addInvitRouteToMyRoutes(BikeEvent invitation, String userId, Context context){

        // Add route in database
        int idRoute = RouteHandler.insertNewRoute(context, invitation.getRoute(), userId);
        invitation.getRoute().setId(idRoute);

        // Add route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.acceptRoute(userId, invitation.getRoute(), invitation);
    }

    // ---------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- USER ----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void setLoginUser(String user_id, String name, String photoUrl, String login, Context context){
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateUserData(user_id,name,photoUrl,login);
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
                    Action.rejectInvitation(event,userId,context);
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
