package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.EventLog;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FirebaseRecover {

    private DatabaseReference databaseReferenceUsers;

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
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String COMMENTS = "comments";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";

    public FirebaseRecover(Context context) {
        FirebaseApp.initializeApp(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUsers= databaseReference.child(USERS);
    }

    public FirebaseRecover(DatabaseReference databaseReferenceUsers) {
        this.databaseReferenceUsers= databaseReferenceUsers;
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------- RECOVER ALL ROUTES FROM USER ---------------------------------------
    // ----------------------------------------------------------------------------------------------

    public List<Route> recoverRoutesUser(String user_id) throws InterruptedException {

        List<Route> listRoutes = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceRoutes = databaseReferenceUsers.child(user_id).child(MY_ROUTES);

        databaseReferenceRoutes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        listRoutes.add(buildListRoute(datas));
                    }
                }
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        return listRoutes;
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------- RECOVER ALL BIKE EVENTS FROM USER ------------------------------------
    // ----------------------------------------------------------------------------------------------

    public List<BikeEvent> recoverBikeEventsUser(String user_id) throws InterruptedException {

        List<BikeEvent> listBikeEvents = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_EVENTS);

        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        listBikeEvents.add(buildBikeEvent(datas));
                    }
                }
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);
        return listBikeEvents;
    }

    // ----------------------------------------------------------------------------------------------
    // -------------------------- RECOVER ALL FRIENDS FROM USER -------------------------------------
    // ----------------------------------------------------------------------------------------------

    public List<Friend> recoverFriendsUser(String user_id) throws InterruptedException {

        List<Friend> listFriend = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);

        databaseReferenceFriend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        listFriend.add(new Friend(datas.getKey(),
                                (String) datas.child(NAME).getValue(),
                                (String) datas.child(PHOTO_URL).getValue()));
                    }
                }
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        return listFriend;
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------ RECOVER ALL INVITATIONS FROM USER -----------------------------------
    // ----------------------------------------------------------------------------------------------

    public List<BikeEvent> recoverInvitationsUser(String user_id) throws InterruptedException {

        List<BikeEvent> listInvitations = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS);

        databaseReferenceInvitation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        listInvitations.add(buildBikeEvent(datas));
                    }
                }
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        return listInvitations;
    }

    public List<EventFriends> recoverEventFriends(String user_id, BikeEvent bikeEvent) throws InterruptedException {

        final List<EventFriends> listEventFriends = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceEventFriends;

        if(user_id.equals(bikeEvent.getOrganizerId())){
            databaseReferenceEventFriends = databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).child(GUESTS);
        } else {
            String idInvitation = UtilsApp.getIdInvitation(bikeEvent);
            databaseReferenceEventFriends = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvitation).child(GUESTS);
        }

        databaseReferenceEventFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEventFriends.addAll(buildListEventFriends(dataSnapshot));
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        return listEventFriends;
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------- UTILS ----------------------------------------------
    // ----------------------------------------------------------------------------------------------

    private Route buildListRoute(DataSnapshot dataSnapshot){

        List<RouteSegment> listRouteSegments = new ArrayList<>();

        for(DataSnapshot datas : dataSnapshot.child(POINTS).getChildren()){

            int idRoute = 0;
            if(datas.child(ID_ROUTE).getValue()!=null)
                idRoute=Integer.parseInt(datas.child(ID_ROUTE).getValue().toString());

            listRouteSegments.add(new RouteSegment(Integer.parseInt(datas.child(ID).getValue().toString()),
                    Integer.parseInt(datas.getKey()),
                    (Double) datas.child(LAT).getValue(),
                    (Double) datas.child(LNG).getValue(),
                    idRoute));
        }

        int idRoute = 0;

        if(!dataSnapshot.getKey().equals(ROUTE))
            idRoute = Integer.parseInt(dataSnapshot.getKey());

        return new Route(idRoute,(String) dataSnapshot.child(NAME).getValue(),
                (Boolean) dataSnapshot.child(VALID).getValue(),listRouteSegments);
    }

    private BikeEvent buildBikeEvent(DataSnapshot datas){

        String idRoute;
        if(datas.child(ID_ROUTE).getValue()!=null)
            idRoute = datas.child(ID_ROUTE).getValue().toString();
        else
            idRoute = "0";

        BikeEvent bikeEvent = new BikeEvent(0,(String) datas.child(ID_ORGANIZER).getValue(),
                (String) datas.child(DATE).getValue(),
                (String) datas.child(TIME).getValue(),
                Integer.parseInt(idRoute),
                (String) datas.child(COMMENTS).getValue(),
                (String) datas.child(STATUS).getValue(),
                buildListEventFriends(datas.child(GUESTS)));

        if(datas.hasChild(ROUTE)){
            Route route = buildListRoute(datas.child(ROUTE));
            bikeEvent.setRoute(route);
        }

        return bikeEvent;
    }

    private List<EventFriends> buildListEventFriends(DataSnapshot guests) {

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(guests.getChildren()!=null){
            for(DataSnapshot datas : guests.getChildren()){

                String idEvent;
                if(datas.child(ID_EVENT).getValue()!=null)
                    idEvent = datas.child(ID_EVENT).getValue().toString();
                else
                    idEvent = "0";

                listEventFriends.add(new EventFriends(Integer.parseInt(datas.getKey()),
                        Integer.parseInt(idEvent),
                        (String) datas.child(ID_FRIEND).getValue(),
                        (Boolean) datas.child(ACCEPTED).getValue()
                ));
            }
        }

        return listEventFriends;
    }
}
