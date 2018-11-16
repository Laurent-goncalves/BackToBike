package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnCompletedSynchronization;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.List;


public class SynchronizeWithDatabase {

    private static final String ONGOING = "ongoing";
    private static final String REJECTED = "rejected";
    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";
    private static final String LOGIN_SHARED ="login_shared";
    private final static String SHARED_LIST_LOGINS = "shared_list_logins";

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------- SYNCHRONIZE FRIENDS --------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static void synchronizeFriends(Context context, Friend user, OnCompletedSynchronization onCompletedSynchronization){

        List<Friend> listFriendsDB = FriendsHandler.getListFriends(context,user.getId());

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverFriendsUser(user.getId(), new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriend) {
                synchonizeFriendFirebase(context, user, listFriend, listFriendsDB,onCompletedSynchronization);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void synchonizeFriendFirebase(Context context, Friend user, List<Friend> listFriendsFB, List<Friend> listFriendsDB, OnCompletedSynchronization onCompletedSynchronization) {

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        if(listFriendsDB!=null){
            if(listFriendsDB.size()>0){
                for(Friend friend : listFriendsDB){

                    if(UtilsApp.findFriendIndexInListFriends(friend,listFriendsFB)!=-1){ // if friend DB on Firebase

                        if(friend.getAccepted()!=null){
                            if(friend.getAccepted().equals(ACCEPTED)) {
                                firebaseUpdate.acceptFriend(context, user.getId(), friend);
                                NotificationUtils.configureAndSendNotification(context, friend.getId(), user.getLogin(),NotificationUtils.FRIEND_HAS_ACCEPTED);
                            } else if(friend.getAccepted().equals(REJECTED)){
                                firebaseUpdate.rejectFriend(context, user.getId(), friend);
                                NotificationUtils.configureAndSendNotification(context, friend.getId(), user.getLogin(),NotificationUtils.FRIEND_HAS_REJECTED);
                            }
                        }
                    }
                }
            }
        }

        if(listFriendsFB!=null){
            if(listFriendsFB.size()>0){
                for(Friend friend : listFriendsFB){
                    if(UtilsApp.findFriendIndexInListFriends(friend,listFriendsDB)==-1) { // if friend FB not in Database
                        if (friend.getHasAgreed() != null) {
                            if (!friend.getAccepted().equals(ONGOING)) {
                                firebaseUpdate.rejectFriend(context, user.getId(), friend);
                            }
                        }
                    }
                }
            }
        }

        // Check if logins (friend requests) in sharedpref
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
        String serialized = sharedPref.getString(SHARED_LIST_LOGINS, null);

        if(serialized!=null) {
            List<String> list = Arrays.asList(TextUtils.split(serialized, ","));

            if(list.size()>0){
                for(String login : list){
                    checkLogin(context, sharedPref, login, user.getId());
                }
            }
        }

        sharedPref.edit().putString(SHARED_LIST_LOGINS, null).apply();

        synchronizeRoutes(context,user.getId(),onCompletedSynchronization);
    }

    private static void checkLogin(Context context, SharedPreferences sharedPref, String login, String userId){
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.checkLogin(context, userId, login, new OnLoginChecked() {
            @Override
            public void onSuccess(Friend friend) {
                // Add friend to database and Firebase
                friend.setHasAgreed(ONGOING);
                friend.setAccepted(ACCEPTED);
                Action.addNewFriend(friend, UtilsApp.getUserFromFirebaseUser(sharedPref.getString(LOGIN_SHARED,null),
                        FirebaseAuth.getInstance().getCurrentUser()), userId, context);
            }

            @Override
            public void onFailed() {}
        });
    }

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------- SYNCHRONIZE ROUTES ---------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static void synchronizeRoutes(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) {

        List<Route> listRouteDB = RouteHandler.getAllRoutesForSynchronization(context,userId);

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        if(listRouteDB!=null){
            if(listRouteDB.size()>0){
                for(Route route : listRouteDB){
                    firebaseUpdate.updateMyRoutes(userId, route, route.getListRouteSegment());
                }
            }
        }

        synchronizeEvents(context, userId,onCompletedSynchronization);
    }

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------- SYNCHRONIZE EVENTS ---------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static void synchronizeEvents(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) {

        List<BikeEvent> listEventDB = BikeEventHandler.getAllBikeEvents(context,userId);

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverBikeEventsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEventFB) {
                synchonizeEventsFirebase(context, userId, listBikeEventFB, listEventDB,onCompletedSynchronization);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void synchonizeEventsFirebase(Context context, String userId, List<BikeEvent> listBikeEventFB, List<BikeEvent> listBikeEventDB, OnCompletedSynchronization onCompletedSynchronization) {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
        String myLogin = sharedPref.getString(LOGIN_SHARED, null);

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        if(listBikeEventDB!=null){
            if(listBikeEventDB.size()>0){
                for(BikeEvent event : listBikeEventDB){

                    int index = UtilsApp.findIndexEventInList(event.getId(), listBikeEventFB);

                    if(index ==-1){ // if event NOT on Firebase

                        if(!event.getOrganizerId().equals(userId)){ // if user is NOT the organizer

                            if(event.getStatus()!=null){
                                switch (event.getStatus()) {
                                    case ACCEPTED:  // if user has accepted invitation
                                        firebaseUpdate.giveAnswerToInvitation(context, userId, event, ACCEPTED);
                                        NotificationUtils.configureAndSendNotification(context, event.getOrganizerId(),myLogin,
                                                NotificationUtils.ACCEPT_INVITATION);
                                        break;
                                    case REJECTED:  // if user has rejected invitation
                                        firebaseUpdate.giveAnswerToInvitation(context, userId, event, REJECTED);
                                        NotificationUtils.configureAndSendNotification(context, event.getOrganizerId(), myLogin,
                                                NotificationUtils.REJECT_INVITATION);
                                        break;
                                    default:
                                        Action.deleteBikeEvent(event, userId, context);
                                        break;
                                }
                            }

                        } else { // if user is the organizer
                            if(event.getStatus().equals(ACCEPTED)){
                                // new event to send
                                firebaseUpdate.updateMyBikeEvent(userId, event);
                                // Send invitations to guests
                                firebaseUpdate.addInvitationGuests(event);
                                // Send notifications to guests
                                if(event.getListEventFriends()!=null){
                                    for(EventFriends eventFriends : event.getListEventFriends()){
                                        NotificationUtils.configureAndSendNotification(context, eventFriends.getIdFriend(),
                                                myLogin,NotificationUtils.NEW_INVITATION);
                                    }
                                }

                            } else {
                                // event finished, to be deleted in Firebase and Database
                                Action.deleteBikeEvent(event, userId, context);
                            }
                        }
                    } else { // if event on Firebase

                        if(!event.getStatus().equals(listBikeEventFB.get(index).getStatus())){ // if status are different

                            if(event.getOrganizerId().equals(userId)) { // if user is the organizer
                                if(event.getStatus().equals(CANCELLED)) {
                                    firebaseUpdate.cancelMyBikeEvent(context, userId, event.getListEventFriends(), event);

                                    // Send notifications to guests
                                    if(event.getListEventFriends()!=null){
                                        for(EventFriends eventFriends : event.getListEventFriends()){
                                            NotificationUtils.configureAndSendNotification(context, eventFriends.getIdFriend(),
                                                    myLogin,NotificationUtils.CANCEL_EVENT);
                                        }
                                    }
                                } else
                                    Action.deleteBikeEvent(event, userId, context);
                            } else { // if user is NOT the organizer
                                if(event.getStatus().equals(REJECTED)) {

                                    firebaseUpdate.rejectEvent(context, userId, event);

                                    // Send notifications to guests
                                    if(event.getListEventFriends()!=null){
                                        for(EventFriends eventFriends : event.getListEventFriends()){
                                            NotificationUtils.configureAndSendNotification(context, eventFriends.getIdFriend(),
                                                    myLogin,NotificationUtils.REJECT_EVENT);
                                        }
                                    }

                                } else
                                    Action.deleteBikeEvent(event, userId, context);
                            }
                        }
                    }
                }
            }
        }

        synchronizeInvits(context,userId,onCompletedSynchronization);
    }

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------- SYNCHRONIZE INVITS ---------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static void synchronizeInvits(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) {

        List<BikeEvent> listInvitDB = BikeEventHandler.getAllInvitations(context,userId);

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverInvitationsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent Invit) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitsFB) {
                if(listInvitsFB!=null){
                    if(listInvitsFB.size()>0){
                        for(BikeEvent invit : listInvitsFB){

                            if(UtilsApp.findIndexEventInList(invit.getId(), listInvitDB) != -1) // if invit in database
                                BikeEventHandler.updateBikeEvent(context, invit, userId); // update invit
                            else
                                BikeEventHandler.insertNewBikeEvent(context, invit, userId); // add new invit in database

                        }
                    }
                }

                onCompletedSynchronization.onCompleted();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });
    }
}