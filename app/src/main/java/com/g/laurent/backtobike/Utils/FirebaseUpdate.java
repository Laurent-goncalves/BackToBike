package com.g.laurent.backtobike.Utils;

import android.content.Context;

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

    private DatabaseReference databaseReferenceUsers;

    public FirebaseUpdate(Context context) {
        FirebaseApp.initializeApp(context);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReferenceUsers = databaseReference.child("users");
    }

    public void updateUserData(String user_id, String name, String photoUrl){
        databaseReferenceUsers.child(user_id).child("name").setValue(name);
        databaseReferenceUsers.child(user_id).child("photoUrl").setValue(photoUrl);
    }

    // ------------------------------------------------------------------------------------------------
    // -------------------------------- UPDATE DATA FROM USER -----------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void updateMyRoutes(String user_id, Route route, List<RouteSegment> listRouteSegment){

        DatabaseReference databaseReferenceRoute = databaseReferenceUsers.child(user_id).child("my_routes");

        // set route data
        setRoute(databaseReferenceRoute, String.valueOf(route.getId()), route);

        // set routeSegment data
        setRouteSegment(databaseReferenceRoute.child(String.valueOf(route.getId())), listRouteSegment);
    }

    public void updateMyBikeEvent(String user_id, BikeEvent bikeEvent, List<EventFriends> listEventFriends){

        // set route data
        DatabaseReference databaseReferenceBikeEvent =
                databaseReferenceUsers.child(user_id).child("my_events");

        setBikeEvent(databaseReferenceBikeEvent, String.valueOf(bikeEvent.getId()), bikeEvent);

        // set event friends data
        setEventFriends(databaseReferenceBikeEvent.child(String.valueOf(bikeEvent.getId())),listEventFriends);
    }

    public void updateAcceptanceEventFriend(String user_id, EventFriends eventFriends, Boolean accepted){

        String idEvent = String.valueOf(eventFriends.getIdEvent());
        String idEventFriend = String.valueOf(eventFriends.getId());
        databaseReferenceUsers.child(user_id).child("my_events").child(idEvent).child("guests")
                .child(idEventFriend).child("accepted").setValue(accepted);

    }

    // ------------------------------------------------------------------------------------------------
    // ------------------------------ SET INVITATION TO FRIEND ----------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void setInvitationToGuests(String user_id, Route route, List<RouteSegment> routeSegmentList, BikeEvent bikeEvent, List<Friend> listFriends){

        if(listFriends!=null){
            if(listFriends.size()>0){
                for(Friend friend : listFriends){

                    String idInvitation = user_id + bikeEvent.getDate() + bikeEvent.getTime();

                    // set invitation
                    DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(friend.getId()).child("invitations");
                    setBikeEvent(databaseReferenceInvitation, idInvitation, bikeEvent);
                    databaseReferenceInvitation.child(idInvitation).child("status").setValue("ongoing");

                    // set route
                    DatabaseReference databaseReferenceRoute = databaseReferenceUsers.child(friend.getId()).child("invitations").child(idInvitation);
                    setRoute(databaseReferenceRoute,"route", route);

                    // set Route Segments
                    setRouteSegment(databaseReferenceRoute.child("route"), routeSegmentList);
                }
            }
        }
    }

    public void rejectInvitation(String user_id,BikeEvent bikeEvent, EventFriends eventFriends){

        // create idInvitation
        String idInvitation = user_id + bikeEvent.getDate() + bikeEvent.getTime();
        String idFriend = eventFriends.getIdFriend();

        // Change status of invitation
        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(idFriend).child("invitations");
        databaseReferenceInvitation.child(idInvitation).child("status").setValue("rejected");

        // for eventFriend, change status accepted to true
        updateAcceptanceEventFriend(user_id,eventFriends,false);
    }

    public void acceptInvitation(String user_id, BikeEvent bikeEvent, EventFriends eventFriends){

        // create idInvitation
        String idInvitation = user_id + bikeEvent.getDate() + bikeEvent.getTime();
        String idFriend = eventFriends.getIdFriend();

        // Change status of invitation
        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(idFriend).child("invitations");
        databaseReferenceInvitation.child(idInvitation).child("status").setValue("accepted");

        // for eventFriend, change status accepted to false
        updateAcceptanceEventFriend(user_id,eventFriends,true);
    }

    public void acceptRoute(String user_id, String idRoute, Route route, List<RouteSegment> listRouteSegments, BikeEvent bikeEvent){

        String idInvitation = user_id + bikeEvent.getDate() + bikeEvent.getTime();

        // Update my_routes
        updateMyRoutes(user_id,route,listRouteSegments);

        // In "invitations", replace the route by the IdRoute
        databaseReferenceUsers.child(user_id).child("invitations").child(idInvitation).child("route").setValue(idRoute);
    }

    public void cancelMyBikeEvent(String user_id, List<EventFriends> listEventFriends, BikeEvent bikeEvent){

        // Delete BikeEvent from my_events
        databaseReferenceUsers.child(user_id).child("my_events").child(String.valueOf(bikeEvent.getId())).removeValue();

        String idInvitation = user_id + bikeEvent.getDate() + bikeEvent.getTime();

        // Delete invitation for all guests
        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    databaseReferenceUsers.child(eventFriends.getIdFriend()).child("invitations").child(idInvitation).removeValue();
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // -------------------------- CREATE / UPDATE / DELETE FRIEND -------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void updateFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child("my_friends").child(friend.getId());
        databaseReferenceFriend.child("name").setValue(friend.getName());
        databaseReferenceFriend.child("photoUrl").setValue(friend.getPhotoUrl());
    }

    public void deleteFriend(String user_id, Friend friend){
        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child("my_friends");
        databaseReferenceFriend.child(friend.getId()).removeValue();
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------------------------- UTILS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    private void setRoute(DatabaseReference databaseReference, String idRoute, Route route){

        DatabaseReference databaseReferenceRoute = databaseReference.child(idRoute);
        databaseReferenceRoute.child("name").setValue(route.getName());
        databaseReferenceRoute.child("valid").setValue(route.getValid());
    }

    private void setRouteSegment(DatabaseReference databaseReference, List<RouteSegment> routeSegmentList){

        if(routeSegmentList!=null){
            if(routeSegmentList.size() > 0 ){
                DatabaseReference databaseReferenceRoute = databaseReference.child("points");

                for(RouteSegment segment : routeSegmentList){
                    String number = String.valueOf(segment.getNumber());
                    databaseReferenceRoute.child(number).child("id").setValue(segment.getId());
                    databaseReferenceRoute.child(number).child("lat").setValue(segment.getLat());
                    databaseReferenceRoute.child(number).child("lng").setValue(segment.getLng());
                    databaseReferenceRoute.child(number).child("id_route").setValue(segment.getIdRoute());
                }
            }
        }
    }

    private void setBikeEvent(DatabaseReference databaseReference, String idEvent, BikeEvent bikeEvent){

        DatabaseReference databaseReferenceBikeEvent = databaseReference.child(idEvent);

        databaseReferenceBikeEvent.child("date").setValue(bikeEvent.getDate());
        databaseReferenceBikeEvent.child("time").setValue(bikeEvent.getTime());
        databaseReferenceBikeEvent.child("id_route").setValue(bikeEvent.getIdRoute());
        databaseReferenceBikeEvent.child("comments").setValue(bikeEvent.getComments());
        databaseReferenceBikeEvent.child("status").setValue(bikeEvent.getStatus());
    }

    private void setEventFriends(DatabaseReference databaseReference, List<EventFriends> listEventFriends){

        DatabaseReference databaseReferenceEventFriends = databaseReference.child("guests");

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){

                for(EventFriends eventFriends : listEventFriends){
                    String id = String.valueOf(eventFriends.getId());
                    databaseReferenceEventFriends.child(id).child("id_event").setValue(eventFriends.getIdEvent());
                    databaseReferenceEventFriends.child(id).child("id_friend").setValue(eventFriends.getIdFriend());
                    databaseReferenceEventFriends.child(id).child("accepted").setValue(eventFriends.getAccepted());
                }
            }
        }

    }
}
