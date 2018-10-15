package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.util.EventLog;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void updateAcceptanceEventFriend(String guestId, BikeEvent bikeEvent, String idEventFriend, Boolean accepted){

        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);

        // change status acceptance for event of the organizer
        databaseReferenceUsers.child(bikeEvent.getOrganizerId()).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).child(GUESTS)
                .child(idEventFriend).child(ACCEPTED).setValue(accepted);


        // change status acceptance for invitations of each eventFriend
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!guestId.equals(eventFriends.getIdFriend()) && !bikeEvent.getOrganizerId().equals(eventFriends.getIdFriend())) {
                        databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).child(GUESTS)
                                .child(idEventFriend).child(ACCEPTED).setValue(accepted);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ------------------------------ SET INVITATION TO FRIEND ----------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void addInvitationGuests(BikeEvent bikeEvent){

        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);

        // set invitation
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!eventFriends.getIdFriend().equals(bikeEvent.getOrganizerId())){

                        DatabaseReference databaseReference = databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation);

                        // set bike event
                        setBikeEvent(databaseReference, bikeEvent);

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

    public void changeInvitationGuests(BikeEvent bikeEvent, String oldIdEvent){

        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);

        // set invitation
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){
                for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                    if(!eventFriends.getIdFriend().equals(bikeEvent.getOrganizerId())){

                        // Delete invitation
                        databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(oldIdEvent).removeValue();

                        DatabaseReference databaseReference = databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation);

                        // set bike event
                        setBikeEvent(databaseReference, bikeEvent);

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

    public void giveAnswerToInvitation(String guestId, BikeEvent bikeEvent, String acceptance){

        // create idInvitation
        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);
        int idEventFriend = UtilsApp.getIdEventFriend(guestId, bikeEvent);

        if(idEventFriend!=-1){

            DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(guestId).child(MY_INVITATIONS).child(idInvitation);

            // update datas from organizer and other eventFriends
            updateAcceptanceEventFriend(guestId, bikeEvent, String.valueOf(idEventFriend), acceptance.equals(ACCEPTED));

            // update user_id datas
            if(acceptance.equals(ACCEPTED)){ // change status acceptance if invitation accepted
                databaseReferenceInvitation.child(STATUS).setValue(ACCEPTED);
            } else { // remove invitation if not accepted
                databaseReferenceInvitation.removeValue();
            }
        }
    }

    public void acceptRoute(String user_id, Route route, BikeEvent bikeEvent){

        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);

        // Update my_routes
        updateMyRoutes(user_id, route, route.getListRouteSegment());

        // In "invitations", delete the route
        databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvitation).child(ROUTE).removeValue();

        // In "invitations", replace the route by the IdRoute
        databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvitation).child(ID_ROUTE).setValue(route.getId());
    }

    public void cancelMyBikeEvent(String user_id, List<EventFriends> listEventFriends, BikeEvent bikeEvent){

        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).removeValue();

        String idInvitation = UtilsApp.getIdInvitation(bikeEvent);

        // Delete invitation for all guests
        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child(MY_INVITATIONS).child(idInvitation).removeValue();
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------- CREATE / UPDATE / DELETE FRIEND or USER ---------------------------------
    // ------------------------------------------------------------------------------------------------

    public void addNewFriend(Friend friend, String login, FirebaseUser firebaseUser){

        // Add friend to user Firebase "my_friends" with status "true" as accepted
        friend.setAccepted(true);
        updateFriend(firebaseUser.getUid(), friend);

        // Add user in friend Firebase "my_friends" with status "null" as accepted
        Friend user = new Friend(firebaseUser.getUid(), login, firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString(),null);
        updateFriend(friend.getId(),user);
    }

    public void updateFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS).child(friend.getId());
        databaseReferenceFriend.child(NAME).setValue(friend.getName());
        databaseReferenceFriend.child(PHOTO_URL).setValue(friend.getPhotoUrl());
        databaseReferenceFriend.child(LOGIN).setValue(friend.getLogin());
        databaseReferenceFriend.child(ACCEPTED).setValue(friend.getAccepted());
    }

    public void deleteFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceFriend.child(friend.getId()).removeValue();
    }

    public void acceptFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceFriend.child(friend.getId()).child(ACCEPTED).setValue(true);
    }

    public void rejectFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceFriend.child(friend.getId()).child(ACCEPTED).setValue(false);
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------------------------- UTILS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    private void setRoute(DatabaseReference databaseReference, Route route){
        databaseReference.child(NAME).setValue(route.getName());
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

    private void setEventFriends(DatabaseReference databaseReference, String guests_id, List<EventFriends> listEventFriends){

        DatabaseReference databaseReferenceEventFriends = databaseReference.child(GUESTS);

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    if(!eventFriends.getIdFriend().equals(guests_id)){
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(ACCEPTED).setValue(eventFriends.getAccepted());
                    }
                }
            }
        }

    }
}
