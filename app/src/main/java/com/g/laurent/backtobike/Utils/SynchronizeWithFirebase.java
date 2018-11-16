package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnRouteDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;

import java.util.List;


public class SynchronizeWithFirebase {

    private static final String SHAREDPREFERENCES_INIT = "database_init_sharedpreferences";

    public static void buildDatabaseWithDatasFromFirebase(String userId, SharedPreferences sharedPref, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd) {

        // Recover friends from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverFriendsUser(userId, new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriendFirebase) {
                if(listFriendFirebase!=null){
                    if(listFriendFirebase.size()>0){
                        for(Friend friend : listFriendFirebase){
                            FriendsHandler.insertNewFriend(context, friend, userId); // add new friend in database
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });

        firebaseRecover.recoverInvitationsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) { }

            @Override
            public void onSuccess(List<BikeEvent> bikeEvent) {
                if(bikeEvent!=null){
                    if(bikeEvent.size()>0){
                        for(BikeEvent event : bikeEvent){
                            BikeEventHandler.insertNewBikeEvent(context, event, userId); // add new event in database
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {}
        });

        firebaseRecover.recoverRoutesUser(userId, new OnRouteDataGetListener() {
            @Override
            public void onSuccess(List<Route> listRoutes) {
                if(listRoutes!=null){
                    if(listRoutes.size()>0){
                        for(Route route : listRoutes){
                            RouteHandler.insertNewRoute(context, route, userId); // add new route in database
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                callbackSynchronizeEnd.onFailure(error);
            }
        });

        // Save in sharedpreferences that database has been initialized
        sharedPref.edit().putBoolean(SHAREDPREFERENCES_INIT, true).apply();
    }

    public static void synchronizeFriends(String userId, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd){

        // Recover friends on database
        List<Friend> listFriendApp = FriendsHandler.getListFriends(context, userId);

        // Recover friends from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverFriendsUser(userId, new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriendFirebase) {

                if(listFriendFirebase!=null){
                    if(listFriendFirebase.size()>0){
                        for(Friend friend : listFriendFirebase){
                            if (UtilsApp.findFriendIndexInListFriends(friend, listFriendApp) != -1) // if friend in database
                                FriendsHandler.updateFriend(context, friend, userId); // update friend
                            else
                                FriendsHandler.insertNewFriend(context, friend, userId); // add new friend in database
                        }
                    }
                }
                callbackSynchronizeEnd.onCompleted();
            }

            @Override
            public void onFailure(String error) {
                callbackSynchronizeEnd.onFailure(error);
            }
        });
    }

    public static void synchronizeInvitations(String userId, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd) {

        // Recover invitations on database
        List<BikeEvent> listBikeEventApp = BikeEventHandler.getAllInvitations(context, userId);

        // Recover invitations from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverInvitationsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                if(listBikeEvent!=null){
                    if(listBikeEvent.size()>0){
                        for(BikeEvent invit : listBikeEvent){

                            if(UtilsApp.findIndexEventInList(invit.getId(), listBikeEventApp) != -1) // if invit in database
                                BikeEventHandler.updateBikeEvent(context, invit, userId); // update invit
                            else
                                BikeEventHandler.insertNewBikeEvent(context, invit, userId); // add new invit in database

                        }
                    }
                }
                callbackSynchronizeEnd.onCompleted();
            }

            @Override
            public void onFailure(String error) {
                callbackSynchronizeEnd.onFailure(error);
            }
        });
    }

    public static void synchronizeMyEvents(String userId, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd) {

        // Recover events on database
        List<BikeEvent> listBikeEventApp = BikeEventHandler.getAllFutureBikeEvents(context, userId);

        // Recover events from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverBikeEventsUser(userId, new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                if(listBikeEvent!=null){
                    if(listBikeEvent.size()>0){
                        for(BikeEvent bikeEvent : listBikeEvent){
                            if(UtilsApp.findIndexEventInList(bikeEvent.getId(), listBikeEventApp) != -1) // if event in database
                                BikeEventHandler.updateBikeEvent(context, bikeEvent, userId); // update event
                        }
                    }
                }
                if(callbackSynchronizeEnd!=null)
                    callbackSynchronizeEnd.onCompleted();
            }

            @Override
            public void onFailure(String error) {
                if(callbackSynchronizeEnd!=null)
                    callbackSynchronizeEnd.onFailure(error);
            }
        });
    }

    public static void synchronizeMyRoutes(String userId, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd) {

        // Recover routes on database
        List<Route> listRoutesApp = RouteHandler.getAllRoutesForSynchronization(context, userId);

        // Recover routes from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverRoutesUser(userId, new OnRouteDataGetListener() {

            @Override
            public void onSuccess(List<Route> listRoutesFirebase) {
                if(listRoutesFirebase!=null){
                    if(listRoutesFirebase.size()>0){
                        for(Route route : listRoutesFirebase){
                            if (UtilsApp.findRouteInList(route, listRoutesApp) == -1) // if route NOT in database
                                RouteHandler.insertNewRoute(context, route, userId); // add new route in database
                        }
                    }
                }
                callbackSynchronizeEnd.onCompleted();
            }

            @Override
            public void onFailure(String error) {
                callbackSynchronizeEnd.onFailure(error);
            }
        });
    }
}
