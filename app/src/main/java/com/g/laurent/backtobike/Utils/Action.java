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

    public static void addNewFriend(Friend friend, String login, FirebaseUser firebaseUser, Context context){

        // Insert friend in database
        FriendsHandler.insertNewFriend(context, friend);

        // Insert friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.addNewFriend(friend, login, firebaseUser);
    }

    public static void updateFriend(Friend friend, String userId, Context context){

        // Update friend in database
        FriendsHandler.updateFriend(context, friend);

        // Update friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateFriend(userId, friend);
    }

    public static void deleteFriend(Friend friend, String userId, Context context){

        // Update friend in database
        FriendsHandler.deleteFriend(context, friend.getId());

        // Update friend in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.deleteFriend(userId, friend);
    }

    public static void acceptFriend(Friend friend, String userId, Context context){

        // Friend accepted in database
        friend.setAccepted(true);
        FriendsHandler.updateFriend(context, friend);

        // Friend accepted in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.acceptFriend(userId,friend);
    }

    public static void rejectFriend(Friend friend, String userId, Context context){

        // Friend accepted in database
        friend.setAccepted(false);
        FriendsHandler.updateFriend(context, friend);

        // Friend accepted in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.rejectFriend(userId,friend);
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ROUTE ------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addNewRoute(Route route, String userId, Context context){

        // Insert route in database
        route.setId(RouteHandler.insertNewRoute(context, route));

        // Insert route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
    }

    public static void updateRoute(Route route, String userId, Context context){

        // Update route in database
        RouteHandler.updateRoute(context, route);

        // Update route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
    }

    public static void deleteRoute(Route route, String userId, Context context){

        route.setValid(false);

        // Delete route in database
        RouteHandler.updateRoute(context, route);

        // Delete route in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
    }

    // ---------------------------------------------------------------------------------------------------------
    // -------------------------------------------- BIKE EVENT -------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void addBikeEvent(BikeEvent event, String userId, Context context){

        // Insert event in database
        event.setId(BikeEventHandler.insertNewBikeEvent(context,event));

        // Insert event in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateMyBikeEvent(userId, event);

        // Send invitations to guests
        firebaseUpdate.addInvitationGuests(event);
    }

    public static void cancelBikeEvent(BikeEvent event, String userId, Context context){

        // Delete event in database
        BikeEventHandler.deleteBikeEvent(context, event);

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
        BikeEventHandler.updateBikeEvent(context,invitation);

        // Accept invitation in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.giveAnswerToInvitation(userId, invitation,ACCEPTED);
    }

    public static void rejectInvitation(BikeEvent invitation, String userId, Context context){

        invitation.setStatus(REJECTED);

        // Reject invitation in database
        BikeEventHandler.updateBikeEvent(context,invitation);

        // Reject invitation in Firebase
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.giveAnswerToInvitation(userId,invitation,REJECTED);
    }

    // ---------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- USER ----------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public static void setLoginUser(String user_id, String name, String photoUrl, String login, Context context){
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
        firebaseUpdate.updateUserData(user_id,name,photoUrl,login);
    }
}
