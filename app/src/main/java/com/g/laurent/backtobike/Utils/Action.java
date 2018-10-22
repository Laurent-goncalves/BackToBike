package com.g.laurent.backtobike.Utils;

import android.content.Context;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.google.firebase.auth.FirebaseUser;

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
        FriendsHandler.deleteFriend(context, friend.getId(), userId);

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
}
