package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.EventLog;
import android.util.Log;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
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

import java.util.ArrayList;
import java.util.List;

public class FirebaseUpdate {

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String MY_ROUTES = "my_routes";
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
    }

    public void updateUserData(String user_id, String name, String photoUrl){
        databaseReferenceUsers.child(user_id).child(NAME).setValue(name);
        databaseReferenceUsers.child(user_id).child(PHOTO_URL).setValue(photoUrl);
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

    public void giveAnswerToInvitation(String userId, BikeEvent bikeEvent, String acceptance){

        // create idInvitation
        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        if(acceptance.equals(ACCEPTED)) {
            // Move invitation to "my_events"
            DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation);
            DatabaseReference databaseReferenceEvent = databaseReferenceUsers.child(userId).child(MY_EVENTS).child(bikeEvent.getId());
            databaseReferenceInvitation.child(STATUS).setValue(ACCEPTED);
            moveInvitationToMyEvents(databaseReferenceInvitation, databaseReferenceEvent);

            // update datas from organizer and other eventFriends
            updateAcceptanceEventFriend(userId, bikeEvent, bikeEvent.getOrganizerId(), true);

        } else {
            // Delete invitation if user rejected
            databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation).removeValue();
            databaseReferenceUsers.child(userId).child(MY_EVENTS).child(idInvitation).removeValue();

            // update datas from organizer and other eventFriends
            updateAcceptanceEventFriend(userId, bikeEvent, bikeEvent.getOrganizerId(), false);
        }
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

    public void updateAcceptanceEventFriend(String guestId, BikeEvent bikeEvent, String idEventFriend, Boolean accepted){

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        String acceptance;
        if(accepted)
            acceptance = ACCEPTED;
        else
            acceptance = REJECTED;

        // change status acceptance for event of the organizer
        databaseReferenceUsers.child(bikeEvent.getOrganizerId()).child(MY_EVENTS).child(bikeEvent.getId()).child(GUESTS)
                .child(idEventFriend).child(ACCEPTED).setValue(acceptance);

        // change status acceptance for invitations of each eventFriend
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!guestId.equals(eventFriends.getIdFriend()) && !bikeEvent.getOrganizerId().equals(eventFriends.getIdFriend())) {

                        if(eventFriends.getAccepted().equals(ONGOING)){ // if still an invitation,...
                            databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).child(GUESTS)
                                    .child(idEventFriend).child(ACCEPTED).setValue(acceptance);

                        } else if(eventFriends.getAccepted().equals(ACCEPTED)){ // if friend accepted,...
                            databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_EVENTS).child(idInvitation).child(GUESTS)
                                    .child(idEventFriend).child(ACCEPTED).setValue(acceptance);
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

    public void cancelMyBikeEvent(String user_id, List<EventFriends> listEventFriends, BikeEvent bikeEvent){

        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).child(STATUS).setValue(CANCELLED);

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        // Delete invitation for all guests
        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){

                    if(eventFriends.getAccepted().equals(ONGOING)) {
                        databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS)
                                .child(idInvitation).child(STATUS).setValue(CANCELLED);
                    }

                    if(eventFriends.getAccepted().equals(ACCEPTED)) {
                        databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_EVENTS)
                                .child(idInvitation).child(STATUS).setValue(CANCELLED);
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

    public void addNewFriend(Friend friend, Friend user){

        // Add friend to user Firebase "my_friends" with status "true" as accepted
        friend.setAccepted(true);
        updateFriendsFromUser(user.getId(), friend);

        // Add user in friend Firebase "my_friends" with status "null" as accepted
        sendFriendRequests(friend.getId(), user);
    }

    public void updateFriendsFromUser(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(friend.getId());
        databaseReferenceFriend.child(NAME).setValue(friend.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(friend.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(friend.getLogin());
        databaseReferenceFriend.child(ACCEPTED).setValue(true);
    }

    public void sendFriendRequests(String user_id, Friend requester){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(requester.getId());
        databaseReferenceFriend.child(NAME).setValue(requester.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(requester.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(requester.getLogin());
        databaseReferenceFriend.child(HAS_ACCEPTED).setValue(true);
    }

    public void acceptFriend(String user_id, String friendId){
        // Change status accepted from user
        DatabaseReference databaseReferenceUser = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceUser.child(friendId).child(ACCEPTED).setValue(true);

        // Change status has_accepted from requester
        DatabaseReference databaseReferenceRequester = databaseReferenceUsers.child(friendId).child(MY_FRIENDS);
        databaseReferenceRequester.child(user_id).child(HAS_ACCEPTED).setValue(true);
    }

    public void rejectFriend(String user_id, String friendId){

        // Delete requester from user
        deleteFriend(user_id,friendId);

        // Change status has_accepted from requester
        DatabaseReference databaseReferenceRequester = databaseReferenceUsers.child(friendId).child(MY_FRIENDS);
        databaseReferenceRequester.child(user_id).child(HAS_ACCEPTED).setValue(false);
    }

    public void deleteFriend(String user_id, String friendId){
        DatabaseReference databaseReferenceMyFriends = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceMyFriends.child(friendId).removeValue();

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(friendId).child(MY_FRIENDS);
        databaseReferenceFriend.child(user_id).child(HAS_ACCEPTED).setValue(false);
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

        Friend friend = new Friend("id1","id1","manu","photoUrl", true, null);

        FriendsHandler.insertNewFriend(context,friend,userId);

        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(LOGIN).setValue("id1");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(NAME).setValue("manu");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(ACCEPTED).setValue(true);
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id1").child(HAS_ACCEPTED).setValue(true);

        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(LOGIN).setValue("id2");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(NAME).setValue("malik");
        databaseReferenceUsers.child(userId).child(MY_FRIENDS).child("id2").child(HAS_ACCEPTED).setValue(true);


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
