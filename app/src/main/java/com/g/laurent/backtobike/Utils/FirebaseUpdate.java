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
        String idEventFriend = UtilsApp.getIdEventFriend(userId, bikeEvent);

        if(idEventFriend!=null){
            if(acceptance.equals(ACCEPTED)) {
                // Move invitation to "my_events"
                DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation);
                DatabaseReference databaseReferenceEvent = databaseReferenceUsers.child(userId).child(MY_EVENTS).child(bikeEvent.getId());
                moveInvitationToMyEvents(databaseReferenceInvitation, databaseReferenceEvent);

                // update datas from organizer and other eventFriends
                updateAcceptanceEventFriend(userId, bikeEvent, idEventFriend, true);
            } else {
                // Delete invitation if user rejected
                databaseReferenceUsers.child(userId).child(MY_INVITATIONS).child(idInvitation).removeValue();

                // update datas from organizer and other eventFriends
                updateAcceptanceEventFriend(userId, bikeEvent, idEventFriend, false);
            }
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

        // In "invitations", replace the route by the IdRoute
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(idInvitation).child(ID_ROUTE).setValue(route.getId());
    }

    public void cancelMyBikeEvent(String user_id, List<EventFriends> listEventFriends, BikeEvent bikeEvent){

        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).removeValue();

        String idInvitation = UtilsApp.getIdEvent(bikeEvent);

        // Delete invitation for all guests
        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).removeValue();
                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_EVENTS).child(idInvitation).removeValue();
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------- CREATE / UPDATE / DELETE FRIEND or USER ---------------------------------
    // ------------------------------------------------------------------------------------------------

    public void addNewFriend(Friend friend, Friend user){

        // Add friend to user Firebase "my_friends" with status "true" as accepted
        friend.setAccepted(true);
        updateFriend(user.getId(), friend, false);

        // Add user in friend Firebase "my_friends" with status "null" as accepted
        updateFriend(friend.getId(), user, true);
    }

    public void updateFriend(String user_id, Friend friend, Boolean isUser){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(friend.getId());
        databaseReferenceFriend.child(NAME).setValue(friend.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(friend.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(friend.getLogin());

        if(!isUser) {
            databaseReferenceFriend.child(ACCEPTED).setValue(true);
        } else {

        }
    }

    public void deleteFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceFriend.child(friend.getId()).removeValue();
    }

    public void acceptFriend(String user_id, Friend friend){
        // Change status has_accepted from user
        DatabaseReference databaseReferenceUser = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceUser.child(friend.getId()).child(ACCEPTED).setValue(true);

        // Change status accepted from friend
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(friend.getId()).child(MY_FRIENDS);
        databaseReferenceFriend.child(user_id).child(HAS_ACCEPTED).setValue(true);
    }

    public void rejectFriend(String user_id, Friend friend){

        // Change status has_accepted from user
        DatabaseReference databaseReferenceUser = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceUser.child(friend.getId()).child(ACCEPTED).setValue(false);

        // Change status accepted from friend
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(friend.getId()).child(MY_FRIENDS);
        databaseReferenceFriend.child(user_id).child(HAS_ACCEPTED).setValue(false);
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------------------------- UTILS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    private void setRoute(DatabaseReference databaseReference, Route route){
        databaseReference.child(NAME).setValue(route.getName());
        databaseReference.child(VALID).setValue(route.getValid());
    }

    private void setRouteSegment(DatabaseReference databaseReference, List<RouteSegment> routeSegmentList){

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

    private void setInvitation(DatabaseReference databaseReference, BikeEvent invitation){
        databaseReference.child(DATE).setValue(invitation.getDate());
        databaseReference.child(TIME).setValue(invitation.getTime());
        databaseReference.child(ID_ORGANIZER).setValue(invitation.getOrganizerId());
        databaseReference.child(COMMENTS).setValue(invitation.getComments());
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
                    }
                }
            }
        }

    }
}
