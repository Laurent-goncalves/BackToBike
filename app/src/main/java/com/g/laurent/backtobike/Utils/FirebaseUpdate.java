package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.EventLog;
import android.util.Log;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnChildChecking;
import com.g.laurent.backtobike.Models.OnCompletedSynchronization;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUpdate {

    private static final String USERS = "users";
    private static final String TOKEN_DEVICE = "token_device";
    private static final String NAME = "name";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String MY_ROUTES = "my_routes";
    private static final String EVENT_FRIEND = "event_friend";
    private static final String INVIT_FRIEND = "invit_friend";
    private static final String GUESTS = "guests";
    private static final String ROUTE = "route";
    private static final String STATUS = "status";
    private static final String ONGOING = "ongoing";
    private static final String VALID = "valid";
    private static final String HAS_ACCEPTED = "has_accepted";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String CANCELLED = "cancelled";
    private static final String PHOTO_URL = "photoUrl";
    private static final String ID_ROUTE = "id_route";
    private static final String ID_EVENT = "id_event";
    private static final String ID_FRIEND = "id_friend";
    private static final String ID_ORGANIZER = "id_organizer";
    private static final String LOGIN = "login";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String COMMENTS = "comments";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";
    private static final String NEED_SYNCHRONIZATION = "need_synchronization";
    private DatabaseReference databaseReferenceUsers;

    public FirebaseUpdate(Context context) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReferenceUsers = databaseReference.child(USERS);
    }

    public FirebaseUpdate(DatabaseReference databaseReferenceUsers) {
        this.databaseReferenceUsers = databaseReferenceUsers;
    }

    public void updateUserData(String user_id, String name, String photoUrl, String login){
        databaseReferenceUsers.child(user_id).child(NAME).setValue(name);
        databaseReferenceUsers.child(user_id).child(PHOTO_URL).setValue(photoUrl);
        databaseReferenceUsers.child(user_id).child(LOGIN).setValue(login);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        return;

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    databaseReferenceUsers.child(user_id).child(TOKEN_DEVICE).setValue(token);
                });
    }

    public void updateUserData(String user_id, String name, String photoUrl){
        databaseReferenceUsers.child(user_id).child(NAME).setValue(name);
        databaseReferenceUsers.child(user_id).child(PHOTO_URL).setValue(photoUrl);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        return;

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    databaseReferenceUsers.child(user_id).child(TOKEN_DEVICE).setValue(token);
                });
    }

    // ------------------------------------------------------------------------------------------------
    // -------------------------------- UPDATE DATA FROM USER -----------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void updateMyRoutes(String user_id, Route route, List<RouteSegment> listRouteSegment){

        DatabaseReference databaseReferenceRoute = databaseReferenceUsers.child(user_id).child(MY_ROUTES).child(String.valueOf(route.getId()));

        // set route data
        setRoute(databaseReferenceRoute, route);

        // set routeSegment data
        setRouteSegment(databaseReferenceRoute, listRouteSegment);
    }

    public void updateMyBikeEvent(String user_id, BikeEvent bikeEvent){

        // set route data
        DatabaseReference databaseReferenceBikeEvent =
                databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId()));

        setBikeEvent(databaseReferenceBikeEvent, bikeEvent);

        // set event friends data
        setEventFriends(databaseReferenceBikeEvent, user_id, bikeEvent.getListEventFriends());
    }

    // ------------------------------------------------------------------------------------------------
    // ------------------------------ SET INVITATION TO FRIEND ----------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void addInvitationGuests(BikeEvent bikeEvent){

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        // set invitation
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!eventFriends.getIdFriend().equals(bikeEvent.getOrganizerId())){

                        DatabaseReference databaseReference = databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation);

                        // set invitation
                        setInvitation(databaseReference, bikeEvent);

                        // set Event Friends
                        setEventFriends(databaseReference, eventFriends.getIdFriend(), bikeEvent.getListEventFriends());

                        // set route
                        setRoute(databaseReference.child(ROUTE), bikeEvent.getRoute());

                        // set Route Segments
                        setRouteSegment(databaseReference.child(ROUTE), bikeEvent.getRoute().getListRouteSegment());
                    }
                }
            }
        }
    }

    public void giveAnswerToInvitation(Context context, String userId, BikeEvent bikeEvent, String acceptance){

        // create idInvitation
        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        if(acceptance.equals(ACCEPTED)) {
            // Move invitation to "my_events"
            DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation);
            DatabaseReference databaseReferenceEvent = databaseReferenceUsers.child(userId).child(MY_EVENTS).child(bikeEvent.getId());
            databaseReferenceInvitation.child(STATUS).setValue(ACCEPTED);
            moveInvitationToMyEvents(databaseReferenceInvitation, databaseReferenceEvent);

            // update datas from organizer and other eventFriends
            updateAcceptanceEventFriend(context, userId, bikeEvent, true);

        } else {
            // Delete invitation if user rejected
            databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation).removeValue();
            databaseReferenceUsers.child(userId).child(MY_EVENTS).child(idInvitation).removeValue();

            // update datas from organizer and other eventFriends
            updateAcceptanceEventFriend(context, userId, bikeEvent, false);
        }
    }

    public void rejectEvent(Context context, String userId, BikeEvent bikeEvent){

        // create idEvent
        String idEvent = UtilsApp.getIdEvent(bikeEvent);

        // Delete event
        databaseReferenceUsers.child(userId).child(MY_EVENTS).child(idEvent).child(STATUS).child(REJECTED);

        // update datas from organizer and other eventFriends
        updateAcceptanceEventFriend(context, userId, bikeEvent, false);
    }

    private void moveInvitationToMyEvents(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(task -> {
                    fromPath.removeEventListener(this);
                    fromPath.removeValue();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void updateAcceptanceEventFriend(Context context, String guestId, BikeEvent bikeEvent, Boolean accepted){

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        String acceptance;
        if(accepted)
            acceptance = ACCEPTED;
        else
            acceptance = REJECTED;

        // change status acceptance for event of the organizer
        databaseReferenceUsers.child(bikeEvent.getOrganizerId()).child(MY_EVENTS).child(bikeEvent.getId()).child(GUESTS)
                .child(guestId).child(ACCEPTED).setValue(acceptance);

        // change status acceptance for invitations of each eventFriend
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!guestId.equals(eventFriends.getIdFriend()) && !bikeEvent.getOrganizerId().equals(eventFriends.getIdFriend())) {

                        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

                        if(eventFriends.getAccepted().equals(ONGOING)){ // if still an invitation,...

                            firebaseRecover.checkIfBikeEventExists(MY_INVITATIONS, eventFriends.getIdFriend(), null, idInvitation, hasChild -> {
                                if(hasChild) {
                                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).child(GUESTS)
                                            .child(guestId).child(ACCEPTED).setValue(acceptance);
                                }
                            });

                        } else { // if friend accepted or rejected,...
                            firebaseRecover.checkIfBikeEventExists(MY_EVENTS, eventFriends.getIdFriend(), null, idInvitation, hasChild -> {
                                if(hasChild) {
                                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_EVENTS).child(idInvitation).child(GUESTS)
                                            .child(guestId).child(ACCEPTED).setValue(acceptance);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public void acceptRoute(String user_id, Route route, BikeEvent bikeEvent){

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        // Update my_routes
        updateMyRoutes(user_id, route, route.getListRouteSegment());

        // In "invitations", delete the route
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(idInvitation).child(ROUTE).removeValue();
    }

    public void cancelMyBikeEvent(Context context, String user_id, List<EventFriends> listEventFriends, BikeEvent bikeEvent){

        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).child(STATUS).setValue(CANCELLED);

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        // Delete invitation for all guests
        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){

                    FirebaseRecover firebaseRecover = new FirebaseRecover(context);

                    if(eventFriends.getAccepted().equals(ONGOING)) {
                        firebaseRecover.checkIfBikeEventExists(MY_INVITATIONS, user_id, null, bikeEvent.getId(), hasChild -> {
                            if(hasChild) {
                                databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS)
                                        .child(idInvitation).child(STATUS).setValue(CANCELLED);
                            }
                        });
                    }

                    if(eventFriends.getAccepted().equals(ACCEPTED)) {
                        firebaseRecover.checkIfBikeEventExists(MY_INVITATIONS, user_id, null, bikeEvent.getId(), hasChild -> {
                            if(hasChild) {
                                databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_EVENTS)
                                        .child(idInvitation).child(STATUS).setValue(CANCELLED);
                            }
                        });
                    }
                }
            }
        }
    }

    public void deleteEvent(String userId, BikeEvent event) {
        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(userId).child(MY_EVENTS).child(String.valueOf(event.getId())).removeValue();
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------- CREATE / UPDATE / DELETE FRIEND or USER ---------------------------------
    // ------------------------------------------------------------------------------------------------

    public void addNewFriend(Context context, Friend friend, Friend user){

        // Check data compliance
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);
        firebaseRecover.checkLogin(context, user.getId(), friend.getLogin(), new OnLoginChecked() {
            @Override
            public void onSuccess(Friend friend) {
                // Add friend to user Firebase "my_friends" with status accepted
                friend.setAccepted(ACCEPTED);
                friend.setHasAgreed(ONGOING);
                updateFriendsFromUser(user.getId(), friend);

                // Add user in friend Firebase "my_friends" with status ongoing
                sendFriendRequests(friend.getId(), user);
            }

            @Override
            public void onFailed() {}
        });
    }

    public void updateFriendsFromUser(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(friend.getId());
        databaseReferenceFriend.child(NAME).setValue(friend.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(friend.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(friend.getLogin());
        databaseReferenceFriend.child(ACCEPTED).setValue(friend.getAccepted());
        databaseReferenceFriend.child(HAS_ACCEPTED).setValue(friend.getHasAgreed());
    }

    public void sendFriendRequests(String user_id, Friend requester){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(requester.getId());
        databaseReferenceFriend.child(NAME).setValue(requester.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(requester.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(requester.getLogin());
        databaseReferenceFriend.child(ACCEPTED).setValue(ONGOING);
        databaseReferenceFriend.child(HAS_ACCEPTED).setValue(ACCEPTED);
    }

    public void acceptFriend(Context context, String user_id, Friend friend){
        // Change status accepted from user
        DatabaseReference databaseReferenceUser = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceUser.child(friend.getId()).child(ACCEPTED).setValue(ACCEPTED);

        // Change status has_accepted from requester if user is among friend
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseRecover firebaseRecover = new FirebaseRecover(context);
            firebaseRecover.recoverFriendsUser(friend.getId(), new OnFriendDataGetListener() {
                @Override
                public void onSuccess(Friend friend) {
                }

                @Override
                public void onSuccess(List<Friend> listFriend) {
                    if (UtilsApp.findFriendIndexInListFriends(user_id, listFriend) != -1) {
                        DatabaseReference databaseReferenceRequester = databaseReferenceUsers.child(friend.getId()).child(MY_FRIENDS);
                        databaseReferenceRequester.child(user_id).child(HAS_ACCEPTED).setValue(ACCEPTED);
                    }
                }

                @Override
                public void onFailure(String error) {
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
                    sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
                }
            });
        }
    }

    public void rejectFriend(Context context, String user_id, Friend friend){

        // Delete requester from user
        DatabaseReference databaseReferenceMyFriends = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceMyFriends.child(friend.getId()).removeValue();

        // Change status has_accepted from requester
        if(UtilsApp.isInternetAvailable(context)) {
            FirebaseRecover firebaseRecover = new FirebaseRecover(context);
            firebaseRecover.recoverFriendsUser(friend.getId(), new OnFriendDataGetListener() {
                @Override
                public void onSuccess(Friend friend) {
                }

                @Override
                public void onSuccess(List<Friend> listFriend) {
                    if (UtilsApp.findFriendIndexInListFriends(user_id, listFriend) != -1) {
                        DatabaseReference databaseReferenceRequester = databaseReferenceUsers.child(friend.getId()).child(MY_FRIENDS);
                        databaseReferenceRequester.child(user_id).child(HAS_ACCEPTED).setValue(REJECTED);
                    }
                }

                @Override
                public void onFailure(String error) {
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
                    sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, true).apply();
                }
            });
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------------------------- UTILS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void setRoute(DatabaseReference databaseReference, Route route){
        databaseReference.child(NAME).setValue(route.getName());
        databaseReference.child(VALID).setValue(route.getValid());
    }

    public void setIdRouteForInvitation(int idRoute, String idEvent, String userId){
        databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idEvent).child(ID_ROUTE)
                .setValue(idRoute);
    }

    public void setRouteSegment(DatabaseReference databaseReference, List<RouteSegment> routeSegmentList){

        if(routeSegmentList!=null){
            if(routeSegmentList.size() > 0 ){
                DatabaseReference databaseReferenceRoute = databaseReference.child(POINTS);

                for(RouteSegment segment : routeSegmentList){
                    String number = String.valueOf(segment.getNumber());
                    databaseReferenceRoute.child(number).child(ID).setValue(segment.getId());
                    databaseReferenceRoute.child(number).child(LAT).setValue(segment.getLat());
                    databaseReferenceRoute.child(number).child(LNG).setValue(segment.getLng());
                    databaseReferenceRoute.child(number).child(ID_ROUTE).setValue(segment.getIdRoute());
                }
            }
        }
    }

    private void setBikeEvent(DatabaseReference databaseReference, BikeEvent bikeEvent){

        databaseReference.child(DATE).setValue(bikeEvent.getDate());
        databaseReference.child(TIME).setValue(bikeEvent.getTime());
        databaseReference.child(ID_ORGANIZER).setValue(bikeEvent.getOrganizerId());
        databaseReference.child(ID_ROUTE).setValue(bikeEvent.getIdRoute());
        databaseReference.child(COMMENTS).setValue(bikeEvent.getComments());
        databaseReference.child(STATUS).setValue(bikeEvent.getStatus());
    }

    public void setInvitation(DatabaseReference databaseReference, BikeEvent bikeEvent){

        databaseReference.child(DATE).setValue(bikeEvent.getDate());
        databaseReference.child(TIME).setValue(bikeEvent.getTime());
        databaseReference.child(ID_ORGANIZER).setValue(bikeEvent.getOrganizerId());
        databaseReference.child(COMMENTS).setValue(bikeEvent.getComments());
        databaseReference.child(STATUS).setValue(ONGOING);
    }

    private void setEventFriends(DatabaseReference databaseReference, String guests_id, List<EventFriends> listEventFriends){

        DatabaseReference databaseReferenceEventFriends = databaseReference.child(GUESTS);

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    if(!eventFriends.getIdFriend().equals(guests_id)){
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(ID_FRIEND).setValue(eventFriends.getIdFriend());
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(ACCEPTED).setValue(eventFriends.getAccepted());
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(LOGIN).setValue(eventFriends.getLogin());
                    }
                }
            }
        }
    }

    public void setTestData(Context context, String userId){

        databaseReferenceUsers.child(userId).child(MY_FRIENDS).removeValue();
        databaseReferenceUsers.child(userId).child(MY_EVENTS).removeValue();
        databaseReferenceUsers.child(userId).child(MY_INVITATIONS).removeValue();

        // manu has accepted user friend request
        // malik has sent a new friend request

        Friend friend = new Friend("id1","id1","manu","photoUrl", REJECTED, ONGOING);

        FriendsHandler.insertNewFriend(context,friend,userId);

        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(LOGIN).setValue("id1");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(NAME).setValue("manu");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(ACCEPTED).setValue(ACCEPTED);
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(HAS_ACCEPTED).setValue(ACCEPTED);

        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(LOGIN).setValue("id2");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(NAME).setValue("malik");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(HAS_ACCEPTED).setValue(ACCEPTED);


        // Response received from EventFriends
        BikeEvent event = new BikeEvent(userId + "_16_02_2018_14:00",userId,"16/02/2018","14:00",0,"comments","accepted");
        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,userId + "_16_02_2018_14:00","id1","id1","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(0,userId + "_16_02_2018_14:00","id2","id2","ongoing");
        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        event.setListEventFriends(listEventFriends);
        BikeEventHandler.insertNewBikeEvent(context, event, userId);

        EVENT_FRIENDS_DEMO_1.setAccepted("accepted");
        EVENT_FRIENDS_DEMO_2.setAccepted("rejected");

        setBikeEvent(databaseReferenceUsers.child(userId).child(MY_EVENTS).child(userId + "_16_02_2018_14:00"),event);
        setEventFriends(databaseReferenceUsers.child(userId).child(MY_EVENTS).child(userId + "_16_02_2018_14:00"), userId, listEventFriends);

        // Invitation waiting for user response
        BikeEvent invitation1 = new BikeEvent("id1_14_02_2018_14:00","id1","14/02/2018","14:00",0,"comments","ongoing");
        BikeEventHandler.insertNewBikeEvent(context, invitation1, userId);
        setBikeEvent(databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child("id1_14_02_2018_14:00"),invitation1);

        // Receive a new invitation from id2
        BikeEvent invitation2 = new BikeEvent("id2_15_02_2018_15:00","id2","15/02/2018","15:00",0,"comments","ongoing");
        setBikeEvent(databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child("id2_15_02_2018_15:00"),invitation2);

    }


}
