package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.g.laurent.backtobike.Controllers.Fragments.DisplayFragment;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackCounters;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnChildChecking;
import com.g.laurent.backtobike.Models.OnCompletedSynchronization;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.Models.OnRouteDataGetListener;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
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
    private static final String TOKEN_DEVICE = "token_device";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String EVENT_FRIEND = "event_friend";
    private static final String INVIT_FRIEND = "invit_friend";
    private static final String MY_ROUTES = "my_routes";
    private static final String GUESTS = "guests";
    private static final String ROUTE = "route";
    private static final String STATUS = "status";
    private static final String ONGOING = "ongoing";
    private static final String VALID = "valid";
    private static final String HAS_ACCEPTED = "has_accepted";
    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String PHOTO_URL = "photoUrl";
    private static final String LOGIN = "login";
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
    // -------------------------------- RECOVER USER DATAS ------------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverUserTokenDevice(String userId, final OnUserDataGetListener onUserDataGetListener){

        databaseReferenceUsers.child(userId).child(TOKEN_DEVICE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = null;
                if(dataSnapshot.getValue()!=null)
                    token = dataSnapshot.getValue().toString();

                if(token!=null)
                    onUserDataGetListener.onSuccess(true, token);
                else
                    onUserDataGetListener.onFailure(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure("Error to access data " + "\n" + databaseError);
            }
        });
    }

    public void recoverUserDatas(String user_id, final OnUserDataGetListener onUserDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean userIdOK = false;
                Boolean loginOK = false;
                String login = null;

                // check data
                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        if(datas.getKey()!=null){
                            if(datas.getKey().equals(user_id)){
                                userIdOK=true; // userId has been found

                                if(datas.child(LOGIN).getValue()!=null){
                                    if(datas.child(LOGIN).getValue().toString().length()>0){
                                        login = datas.child(LOGIN).getValue().toString();
                                        loginOK = true; // login is OK
                                    }
                                }
                                break;
                            }
                        }
                    }
                }

                if(userIdOK && loginOK)
                    onUserDataGetListener.onSuccess(true, login);
                else {
                    onUserDataGetListener.onSuccess(false, login);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure("Error to access data " + "\n" + databaseError);
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------- RECOVER ALL ROUTES FROM USER ---------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverRoutesUser(String user_id, OnRouteDataGetListener onRouteDataGetListener) throws InterruptedException {

        DatabaseReference databaseReferenceRoutes = databaseReferenceUsers.child(user_id).child(MY_ROUTES);

        databaseReferenceRoutes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Route> listRoutes = new ArrayList<>();

                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        listRoutes.add(buildListRoute(datas));
                    }
                }

                onRouteDataGetListener.onSuccess(listRoutes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onRouteDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------- RECOVER ALL BIKE EVENTS FROM USER ------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverBikeEventsUser(String user_id, OnBikeEventDataGetListener onBikeEventDataGetListener) throws InterruptedException {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_EVENTS);

        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<BikeEvent> listBikeEvents = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    listBikeEvents.add(buildBikeEvent(datas));
                }

                onBikeEventDataGetListener.onSuccess(listBikeEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void recoverSingleBikeEventsUser(String user_id, String idEvent, OnBikeEventDataGetListener onBikeEventDataGetListener) throws InterruptedException {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(idEvent);

        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BikeEvent bikeEvent = buildBikeEvent(dataSnapshot);
                onBikeEventDataGetListener.onSuccess(bikeEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void checkIfBikeEventExists(String typeChild, String user_id, String idEvent, String child, OnChildChecking onChildChecking){

        DatabaseReference databaseReference = null;

        switch(typeChild){
            case MY_EVENTS:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_EVENTS);
                break;
            case MY_INVITATIONS:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS);
                break;
            case MY_FRIENDS:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
                break;
            case EVENT_FRIEND:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(idEvent).child(GUESTS);
                break;
            case INVIT_FRIEND:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idEvent).child(GUESTS);
                break;
        }

        if(databaseReference!=null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(child))
                        onChildChecking.hasChild(true);
                    else
                        onChildChecking.hasChild(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onChildChecking.hasChild(false);
                }
            });

        } else {
            onChildChecking.hasChild(false);
        }
    }

    // ----------------------------------------------------------------------------------------------
    // -------------------------------- RECOVER FRIENDS ---------------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void isLoginNotAmongUserFriends(String login, String user_id, final OnFriendDataGetListener onFriendDataGetListener) {

        List<Boolean> answer = new ArrayList<>();
        answer.add(0,true);

        DatabaseReference databaseReferenceFriends = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);

        databaseReferenceFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // check data
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if(datas.child(LOGIN).getValue()!=null){
                        if(datas.child(LOGIN).getValue().toString().equals(login)){
                            answer.add(0,false);
                            break;
                        }
                    }
                }

                // next steps
                if(answer.get(0)){
                    isLoginOnFirebase(login,user_id, onFriendDataGetListener); // check if login exists on firebase
                } else {
                    onFriendDataGetListener.onFailure("friend already in your list");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure("Error to access data " + "\n" + databaseError);
            }
        });
    }

    public void isLoginOnFirebase(String login, String user_id, final OnFriendDataGetListener onFriendDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean answer = false;
                Friend friend = null;

                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        if(datas.child(LOGIN).getValue()!=null){
                            if(datas.child(LOGIN).getValue().toString().equals(login) && !datas.getKey().equals(user_id) ){
                                answer = true;
                                friend = buildFriend(datas); // create Friend
                                break;
                            }
                        }
                    }
                }

                // next steps
                if(answer){
                    onFriendDataGetListener.onSuccess(friend);
                } else {
                    onFriendDataGetListener.onFailure("login doesn't exists on Firebase. Please check login provided.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure("Error to access data " + "\n" + databaseError);
            }
        });
    }

    public void isLoginOnFirebase(String login, String user_id, final OnUserDataGetListener onUserDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean answer = false;

                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        if(datas.child(LOGIN).getValue()!=null){
                            if(datas.child(LOGIN).getValue().toString().equals(login) && !datas.getKey().equals(user_id) ){
                                answer = true;
                                break;
                            }
                        }
                    }
                }

                // next steps
                onUserDataGetListener.onSuccess(answer, login);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure("Error to access data " + "\n" + databaseError);
            }
        });
    }

    public void recoverFriendsUser(String user_id, OnFriendDataGetListener onFriendDataGetListener) {

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);

        databaseReferenceFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Friend> listFriend = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if(datas.getKey()!=null){
                        listFriend.add(buildFriend(datas));
                    }
                }

                onFriendDataGetListener.onSuccess(listFriend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void checkLogin(Context context, String user_id, String login, OnLoginChecked onLoginChecked) {

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        // Check if login is different than user's login, login is not among friends of the user and if the login exists on Firebase
        firebaseRecover.isLoginNotAmongUserFriends(login, user_id, new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {
                isLoginOnFirebase(login, user_id, new OnFriendDataGetListener() {
                    @Override
                    public void onSuccess(Friend friend) {
                        onLoginChecked.onSuccess(friend);
                    }

                    @Override
                    public void onSuccess(List<Friend> listFriend) {}

                    @Override
                    public void onFailure(String error) {
                        onLoginChecked.onFailed();
                        Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onSuccess(List<Friend> listFriend) {}

            @Override
            public void onFailure(String error) {
                onLoginChecked.onFailed();
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------ RECOVER ALL INVITATIONS FROM USER -----------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverInvitationsUser(String user_id, OnBikeEventDataGetListener onBikeEventDataGetListener) {

        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS);

        databaseReferenceInvitation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BikeEvent> listInvitations = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    listInvitations.add(buildBikeEvent(datas));
                }

                onBikeEventDataGetListener.onSuccess(listInvitations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public List<EventFriends> recoverEventFriends(String user_id, BikeEvent bikeEvent) throws InterruptedException {

        final List<EventFriends> listEventFriends = new ArrayList<>();
        final CountDownLatch writeSignal = new CountDownLatch(1);

        DatabaseReference databaseReferenceEventFriends;

        if(user_id.equals(bikeEvent.getOrganizerId())){
            databaseReferenceEventFriends = databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(String.valueOf(bikeEvent.getId())).child(GUESTS);
        } else {
            String idInvitation = UtilsApp.getIdEvent(bikeEvent);
            databaseReferenceEventFriends = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvitation).child(GUESTS);
        }

        databaseReferenceEventFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEventFriends.addAll(buildListEventFriends(dataSnapshot, bikeEvent.getId()));
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        return listEventFriends;
    }

    public void recoverDatasForCounters(String user_id, Context context, CallbackCounters callbackCounters) {

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id);

        databaseReferenceFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counterFriends = 0;
                int counterEvents;
                int counterInvits;
                List<String> fullListDifferences = new ArrayList<>();

                DataSnapshot datasFriends = dataSnapshot.child(MY_FRIENDS);
                DataSnapshot datasInvits = dataSnapshot.child(MY_INVITATIONS);
                DataSnapshot datasEvents = dataSnapshot.child(MY_EVENTS);

                // Get current list of events
                List<BikeEvent> oldListBikeEvent = BikeEventHandler.getAllFutureBikeEvents(context, user_id);
                List<BikeEvent> oldListInvitations = BikeEventHandler.getAllInvitations(context, user_id);
                List<Friend> oldListFriends = FriendsHandler.getListFriends(context, user_id);

                // Compare lists to get counters of modifications
                List<Difference> listDiffEvents = UtilsCounters.getListDifferencesBetweenListEvents(oldListBikeEvent,datasEvents,context);
                List<String> listDiffFriends = UtilsCounters.getListDifferencesForFriendRequests(context,datasFriends,oldListFriends);
                List<String> listDiffInvitations = UtilsCounters.getListDifferencesForInvitations(context, datasInvits, oldListInvitations);

                // Get counters
                counterEvents = listDiffEvents.size();
                counterFriends = listDiffFriends.size();
                counterInvits = listDiffInvitations.size();

                fullListDifferences.addAll(listDiffFriends);
                fullListDifferences.addAll(listDiffInvitations);

                callbackCounters.onCompleted(listDiffEvents, fullListDifferences, counterFriends, counterEvents, counterInvits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbackCounters.onFailure(databaseError.toString());
            }
        });
    }

    public void recoverSingleInvitationUser(String user_id, String idInvit, OnBikeEventDataGetListener onBikeEventDataGetListener) throws InterruptedException {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvit);

        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                BikeEvent bikeEvent = buildBikeEvent(dataSnapshot);
                onBikeEventDataGetListener.onSuccess(bikeEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------- UTILS ----------------------------------------------
    // ----------------------------------------------------------------------------------------------

    private static Route buildListRoute(DataSnapshot dataSnapshot){

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

    public static BikeEvent buildBikeEvent(DataSnapshot datas){

        String idRoute;
        if(datas.child(ID_ROUTE).getValue()!=null)
            idRoute = datas.child(ID_ROUTE).getValue().toString();
        else
            idRoute = "0";

        BikeEvent bikeEvent = new BikeEvent(datas.getKey(),(String) datas.child(ID_ORGANIZER).getValue(),
                (String) datas.child(DATE).getValue(),
                (String) datas.child(TIME).getValue(),
                Integer.parseInt(idRoute),
                (String) datas.child(COMMENTS).getValue(),
                (String) datas.child(STATUS).getValue(),
                buildListEventFriends(datas.child(GUESTS), datas.getKey()));

        if(datas.hasChild(ROUTE)){
            Route route = buildListRoute(datas.child(ROUTE));
            bikeEvent.setRoute(route);
        }

        return bikeEvent;
    }

    private static List<EventFriends> buildListEventFriends(DataSnapshot guests, String idEvent) {

        List<EventFriends> listEventFriends = new ArrayList<>();

        for(DataSnapshot datas : guests.getChildren()){
            listEventFriends.add(new EventFriends(0,
                    idEvent, (String) datas.child(ID_FRIEND).getValue(),
                    (String) datas.child(LOGIN).getValue(),
                    (String) datas.child(ACCEPTED).getValue()
            ));
        }

        return listEventFriends;
    }

    private static Friend buildFriend(DataSnapshot datas){

        return new Friend(datas.getKey(),
                (String) datas.child(LOGIN).getValue(),
                (String) datas.child(NAME).getValue(),
                (String) datas.child(PHOTO_URL).getValue(),
                (String) datas.child(ACCEPTED).getValue(),
                (String) datas.child(HAS_ACCEPTED).getValue());
    }
}
