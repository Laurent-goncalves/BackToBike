package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnCompletedSynchronization;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import java.util.List;


public class SynchronizeWithDatabase {


    private static final String REJECTED = "rejected";
    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";

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
                try {
                    synchonizeFriendFirebase(context, user, listFriend, listFriendsDB,onCompletedSynchronization);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void synchonizeFriendFirebase(Context context, Friend user, List<Friend> listFriendsFB, List<Friend> listFriendsDB, OnCompletedSynchronization onCompletedSynchronization) throws InterruptedException {

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        if(listFriendsDB!=null){
            if(listFriendsDB.size()>0){
                for(Friend friend : listFriendsDB){

                    if(UtilsApp.findFriendIndexInListFriends(friend,listFriendsFB)!=-1){ // if friend DB on Firebase

                        if(friend.getAccepted()!=null){
                            if(friend.getAccepted())
                                firebaseUpdate.acceptFriend(user.getId(), friend.getId());
                            else
                                firebaseUpdate.rejectFriend(user.getId(), friend.getId());
                        }
                    } else {
                        if(friend.getAccepted()!=null){
                            if(friend.getAccepted()) // if new friend request to be sent
                                firebaseUpdate.addNewFriend(friend, user);
                            else // delete friend from Firebase
                                FriendsHandler.deleteFriend(context, friend.getId(), user.getId());
                        }
                    }
                }
            }
        }

        synchronizeRoutes(context,user.getId(),onCompletedSynchronization);
    }

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------- SYNCHRONIZE ROUTES ---------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static void synchronizeRoutes(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) throws InterruptedException {

        List<Route> listRouteDB = RouteHandler.getAllRoutes(context,userId);

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

    public static void synchronizeEvents(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) throws InterruptedException {

        List<BikeEvent> listEventDB = BikeEventHandler.getAllBikeEvents(context,userId);

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverBikeEventsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEventFB) {
                try {
                    synchonizeEventsFirebase(context, userId, listBikeEventFB, listEventDB,onCompletedSynchronization);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void synchonizeEventsFirebase(Context context, String userId, List<BikeEvent> listBikeEventFB, List<BikeEvent> listBikeEventDB, OnCompletedSynchronization onCompletedSynchronization) throws InterruptedException {

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        if(listBikeEventDB!=null){
            if(listBikeEventDB.size()>0){
                for(BikeEvent event : listBikeEventDB){

                    int index = UtilsApp.findIndexEventInList(event.getId(), listBikeEventFB);

                    if(index ==-1){ // if event NOT on Firebase

                        if(!event.getOrganizerId().equals(userId)){ // if user is NOT the organizer

                            if(event.getStatus()!=null){
                                if(event.getStatus().equals(ACCEPTED)) // if user has accepted invitation
                                    firebaseUpdate.giveAnswerToInvitation(userId, event, ACCEPTED);
                                else if(event.getStatus().equals(REJECTED)) // if user has rejected invitation
                                    firebaseUpdate.giveAnswerToInvitation(userId, event, REJECTED);
                                else
                                    Action.deleteBikeEvent(event, userId, context);
                            }

                        } else { // if user is the organizer
                            if(event.getStatus().equals(ACCEPTED)){
                                // new event to send
                                firebaseUpdate.updateMyBikeEvent(userId, event);
                                // Send invitations to guests
                                firebaseUpdate.addInvitationGuests(event);
                            } else {
                                // event finished, to be deleted in Firebase and Database
                                Action.deleteBikeEvent(event, userId, context);
                            }
                        }
                    } else { // if event on Firebase

                        if(!event.getStatus().equals(listBikeEventFB.get(index).getStatus())){ // if status are different

                            if(event.getOrganizerId().equals(userId)) { // if user is the organizer
                                if(event.getStatus().equals(CANCELLED))
                                    firebaseUpdate.cancelMyBikeEvent(userId, event.getListEventFriends(), event);
                                else
                                    Action.deleteBikeEvent(event, userId, context);
                            } else {
                                if(event.getStatus().equals(REJECTED))
                                    firebaseUpdate.giveAnswerToInvitation(userId, event, REJECTED);
                                else
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

    public static void synchronizeInvits(Context context, String userId, OnCompletedSynchronization onCompletedSynchronization) throws InterruptedException {

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




/*private static void synchonizeFriendDatabase(Context context, Friend user, List<Friend> listFriendsFB){

        List<Friend> listFriendsUpdatedDB = FriendsHandler.getListFriends(context,user.getId());

        if(listFriendsFB!=null){
            if(listFriendsFB.size()>0){
                for(Friend friend : listFriendsFB){
                    if (UtilsApp.findFriendIndexInListFriends(friend, listFriendsUpdatedDB) != -1) // if friend in database
                        FriendsHandler.updateFriend(context, friend, user.getId()); // update friend
                    else
                        FriendsHandler.insertNewFriend(context, friend, user.getId()); // add new friend in database
                }
            }
        }
    }

    private static void performSynchronizationFriendsOnFirebase(Context context, Friend user, List<Friend> listFriendsFB, List<Friend> listFriendsDB){

        // SYNCHRONIZE FIREBASE
        synchonizeFriendFirebase(context, user, listFriendsFB, listFriendsDB);

        // SYNCHRONIZE DATABASE
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverFriendsUser(user.getId(), new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriend) {
                // SYNCHRONIZE DATABASE
                synchonizeFriendDatabase(context, user, listFriend);
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }*/


        /*FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.recoverBikeEventsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEventUpdatedFB) {
                try {
                    synchonizeEventsDatabase(context, userId, listBikeEventUpdatedFB);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.error_synchronization),Toast.LENGTH_LONG).show();
            }
        });



            private static void synchonizeEventsDatabase(Context context, String userId, List<BikeEvent> listBikeEventFB) throws InterruptedException {

        List<BikeEvent> listBikeEventDB = BikeEventHandler.getAllFutureBikeEvents(context, userId);

        if(listBikeEventFB!=null){
            if(listBikeEventFB.size()>0){
                for(BikeEvent bikeEvent : listBikeEventFB){
                    if(UtilsApp.findIndexEventInList(bikeEvent.getId(), listBikeEventDB) != -1) // if event in database
                        BikeEventHandler.updateBikeEvent(context, bikeEvent, userId); // update event
                }
            }
        }
    }

        */