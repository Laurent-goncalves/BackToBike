package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.List;


public class FirebaseUpdate {

    private static final String USERS = "users";
    private static final String TOKEN_DEVICE = "token_device";
    private static final String NAME = "name";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String MY_ROUTES = "my_routes";
    private static final String GUESTS = "guests";
    private static final String ROUTE = "route";
    private static final String STATUS = "status";
    private static final String ONGOING = "ongoing";
    private static final String HAS_ACCEPTED = "has_accepted";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String CANCELLED = "cancelled";
    private static final String PHOTO_URL = "photoUrl";
    private static final String ID_ROUTE = "id_route";
    private static final String LOGIN = "login";
    private static final String NEED_SYNCHRONIZATION = "need_synchronization";
    private DatabaseReference databaseReferenceUsers;

    public FirebaseUpdate(Context context) {
        FirebaseApp.initializeApp(context);
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
        UtilsFirebase.setRoute(databaseReferenceRoute, route);

        // set routeSegment data
        UtilsFirebase.setRouteSegment(databaseReferenceRoute, listRouteSegment);
    }

    public void deleteRoute(String user_id, String idRoute){
        DatabaseReference databaseReferenceRoute = databaseReferenceUsers.child(user_id).child(MY_ROUTES).child(idRoute);
        databaseReferenceRoute.removeValue();
    }

    public void updateMyBikeEvent(String user_id, BikeEvent bikeEvent){

        // set route data
        DatabaseReference databaseReferenceBikeEvent =
                databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId()));

        UtilsFirebase.setBikeEvent(databaseReferenceBikeEvent, bikeEvent);

        // set event friends data
        UtilsFirebase.setEventFriends(databaseReferenceBikeEvent, user_id, bikeEvent.getListEventFriends());
    }

    public void setIdRouteForInvitation(int idRoute, String idEvent, String userId){
        databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idEvent).child(ID_ROUTE)
                .setValue(idRoute);
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
                        UtilsFirebase.setInvitation(databaseReference, bikeEvent);

                        // set Event Friends
                        UtilsFirebase.setEventFriends(databaseReference, eventFriends.getIdFriend(), bikeEvent.getListEventFriends());

                        // set route
                        UtilsFirebase.setRoute(databaseReference.child(ROUTE), bikeEvent.getRoute());

                        // set Route Segments
                        UtilsFirebase.setRouteSegment(databaseReference.child(ROUTE), bikeEvent.getRoute().getListRouteSegment());
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
        databaseReferenceUsers.child(userId).child(MY_EVENTS).child(idEvent).removeValue();

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

                            firebaseRecover.checkIfBikeEventExists(MY_INVITATIONS, eventFriends.getIdFriend(), idInvitation, hasChild -> {
                                if(hasChild) {
                                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).child(GUESTS)
                                            .child(guestId).child(ACCEPTED).setValue(acceptance);
                                }
                            });

                        } else { // if friend accepted or rejected,...
                            firebaseRecover.checkIfBikeEventExists(MY_EVENTS, eventFriends.getIdFriend(), idInvitation, hasChild -> {
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
                        firebaseRecover.checkIfBikeEventExists(MY_INVITATIONS, eventFriends.getIdFriend(), bikeEvent.getId(), hasChild -> {
                            if(hasChild) {
                                databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS)
                                        .child(idInvitation).child(STATUS).setValue(CANCELLED);
                            }
                        });
                    }

                    if(eventFriends.getAccepted().equals(ACCEPTED)) {
                        firebaseRecover.checkIfBikeEventExists(MY_EVENTS, eventFriends.getIdFriend(), bikeEvent.getId(), hasChild -> {
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
                public void onSuccess(Friend friend) {}

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
}
