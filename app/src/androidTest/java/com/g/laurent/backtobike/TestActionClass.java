package com.g.laurent.backtobike;

import android.content.Context;
import android.database.Cursor;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnRouteDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestActionClass extends AndroidTestCase {

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;

    @Override
    public void setUp() throws InterruptedException {
        authSignal = new CountDownLatch(30);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("develop.lgon2@gmail.com", "ABC123").addOnCompleteListener(
                    task -> {
                        final AuthResult result = task.getResult();
                        final FirebaseUser user = result.getUser();
                        authSignal.countDown();
                    });
        } else {
            authSignal.countDown();
        }
        authSignal.await(60, TimeUnit.SECONDS);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(auth != null) {
            auth.signOut();
            auth = null;
        }
    }

    @Test
    public void test_my_routes() throws InterruptedException {

        Context context = getInstrumentation().getTargetContext();
        String userId = FirebaseAuth.getInstance().getUid();
        Route route = new Route(0,"trip to Lille",true);
        route.setListRouteSegment(getListRouteSegments());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        //databaseReference.child("id1").child("login").child("111...///");

        databaseReference.child(userId).child("my_routes").removeValue();

        // ------------------------------------------------ INSERT route

        // insert route in Database and Firebase
        int idRoute = Action.addNewRoute(route,userId,context);

        // Check that route is well added to Database
        Route routeTest = getRouteFromDatabase(idRoute, userId);

        Assert.assertEquals("trip to Lille", routeTest.getName());
        Assert.assertEquals(4, routeTest.getListRouteSegment().size());

        // Check that route is well added to Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(userId, new OnRouteDataGetListener() {
            @Override
            public void onSuccess(List<Route> listRoutesFirebase) {
                Assert.assertEquals(1, listRoutesFirebase.size());
            }

            @Override
            public void onFailure(String error) {

            }
        });

        readSignal.await(10, TimeUnit.SECONDS);

        // ----------------------------------------------------- UPDATE route
        route.setName("Trip to Marseille");

        // update route in database
        Action.updateRoute(route, userId, context);

        // Check that route is well updated in Database
        routeTest = getRouteFromDatabase(idRoute,userId);
        Assert.assertEquals("Trip to Marseille", routeTest.getName());

        // Check that route is well updated in Firebase

        readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(userId, new OnRouteDataGetListener() {
            @Override
            public void onSuccess(List<Route> listRoutesFirebase) {
                Assert.assertEquals("Trip to Marseille", listRoutesFirebase.get(0).getName());
            }

            @Override
            public void onFailure(String error) {

            }
        });
        readSignal.await(10, TimeUnit.SECONDS);

        // ----------------------------------------------------- DELETE route
        Action.deleteRoute(route,userId, context);

        // Check that route is well "deleted" in Database
        routeTest = getRouteFromDatabase(idRoute, userId);
        Assert.assertFalse(routeTest.getValid());

        // Check that route is well "deleted" in Firebase

        readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(userId, new OnRouteDataGetListener() {
            @Override
            public void onSuccess(List<Route> listRoutesFirebase) {
                Assert.assertFalse(listRoutesFirebase.get(0).getValid());
            }

            @Override
            public void onFailure(String error) {

            }
        });
        readSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void test_my_friends() {

        Context context = getInstrumentation().getTargetContext();

        // ------------------------------------------------ INSERT friend

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        Friend friend = new Friend("id1","id1","Michel","photo_url","ongoing","ongoing");
        Friend user = new Friend("id2","id2","Seb","photoUrl","accepted","ongoing");

        Action.addNewFriend(friend, user, "id2",context);

        // Check that the friend is well added to database
        Friend friendTest = FriendsHandler.getFriend(context,"id1", "id2");

        Assert.assertEquals("Michel", friendTest.getName());
        Assert.assertEquals("id1", friendTest.getLogin());

        // Check that the friend is well added to Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        firebaseRecover.recoverFriendsUser("id2", new OnFriendDataGetListener() {

            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriend) {
                Assert.assertEquals(friend.getName(), listFriend.get(0).getName());
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // ------------------------------------------------ UPDATE friend

        friend.setName("Sean");
        Action.updateFriend(friend, "id2", context);

        // Check that the friend is well updated in database
        friendTest = FriendsHandler.getFriend(context,"id1", "id2");
        Assert.assertEquals("Sean", friendTest.getName());

        // Check that the friend is well updated in Firebase

        firebaseRecover.recoverFriendsUser("id2", new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriend) {
                Assert.assertEquals("Sean", listFriend.get(0).getName());
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // ------------------------------------------------ DELETE friend

        Action.deleteFriend(friend,"id2",context);
        waiting_time(2000);

        // Check that the friend is well deleted in database
        friendTest = FriendsHandler.getFriend(context,"id1", "id2");
        Assert.assertNull(friendTest.getId());

        // Check that the friend is well deleted in Firebase

        firebaseRecover.recoverFriendsUser("id2", new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriend) {
                Assert.assertEquals(0, listFriend.size());
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);
    }

    @Test
    public void test_my_events() throws InterruptedException {

        String userId = FirebaseAuth.getInstance().getUid();
        Context context = getInstrumentation().getTargetContext();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child("id1").child("my_events").removeValue();
        databaseReference.child("id2").child("my_invitations").removeValue();
        databaseReference.child("id3").child("my_invitations").removeValue();

        setFriendsDatabase(context, userId);

        BikeEvent bikeEvent = new BikeEvent("id1_01_01_2000_14:00", "id1", "01/01/2000", "14:00", 999, "Comments : take good shoes", "accepted");
        Route route = new Route();
        route.setListRouteSegment(getListRouteSegments());
        bikeEvent.setRoute(route);

        bikeEvent.setListEventFriends(getListEventFriends());

        // ----------------------------------------------------- INSERT BIKE EVENT

        Action.addBikeEvent(bikeEvent,"id1",context);

        // Check that bikeEvent has been added to database
        BikeEvent eventTest = BikeEventHandler.getBikeEvent(context,"id1_01_01_2000_14:00","id1");

        Assert.assertEquals("01/01/2000",eventTest.getDate());
        Assert.assertEquals("14:00",eventTest.getTime());

        // Check that bikeEvent has been added to Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);
        firebaseRecover.recoverBikeEventsUser("id1", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                Assert.assertEquals(1, listBikeEvent.size());
                Assert.assertEquals("01/01/2000", listBikeEvent.get(0).getDate());
            }

            @Override
            public void onFailure(String error) {}
        });
        firebaseRecover.recoverInvitationsUser("id2", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(1, listInvitations.size());
                Assert.assertEquals("01/01/2000", listInvitations.get(0).getDate());
            }

            @Override
            public void onFailure(String error) {}
        });
        firebaseRecover.recoverInvitationsUser("id3", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(1, listInvitations.size());
                Assert.assertEquals("01/01/2000", listInvitations.get(0).getDate());
            }

            @Override
            public void onFailure(String error) {}
        });

        waiting_time(6000);

        // ----------------------------------------------------- CANCEL BIKE EVENT

        Action.cancelBikeEvent(bikeEvent,"id1",context);

        // Check that bikeEvent has been deleted in database
        eventTest = BikeEventHandler.getBikeEvent(context, bikeEvent.getId(),"id1");
        Assert.assertNull(eventTest.getComments());

        waiting_time(5000);

        // Check that bikeEvent and invitations has been deleted in Firebase
        firebaseRecover.recoverBikeEventsUser("id1", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                Assert.assertEquals(1,listBikeEvent.size());
                Assert.assertEquals("cancelled",listBikeEvent.get(0).getStatus());
            }

            @Override
            public void onFailure(String error) {}
        });
        firebaseRecover.recoverInvitationsUser("id2", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(1,listInvitations.size());
                Assert.assertEquals("cancelled",listInvitations.get(0).getStatus());
            }

            @Override
            public void onFailure(String error) {}
        });
        firebaseRecover.recoverInvitationsUser("id3", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(1,listInvitations.size());
                Assert.assertEquals("cancelled",listInvitations.get(0).getStatus());
            }

            @Override
            public void onFailure(String error) {}
        });

        waiting_time(10000);
    }

    @Test
    public void test_accept_reject_invitation() throws InterruptedException {

        String userId = FirebaseAuth.getInstance().getUid();
        Context context = getInstrumentation().getTargetContext();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(userId).child("my_events").removeValue();

        setFriendsDatabase(context, userId);

        BikeEvent bikeEvent = new BikeEvent("id1_01_01_2000_14:00", "id1", "01/01/2000", "14:00", 999, "Comments : take good shoes", "accepted");
        Route route = new Route(999,"Trip to Paris",true);
        route.setListRouteSegment(getListRouteSegments());
        bikeEvent.setRoute(route);

        bikeEvent.setListEventFriends(getListEventFriends());

        // ----------------------------------------------------- INSERT BIKE EVENT

        Action.addBikeEvent(bikeEvent,"id1",context);
        waiting_time(1000);

        // --------------------------------------------------------  id1 accept the invitation
        Action.acceptInvitation(bikeEvent, "id2", context);
        bikeEvent.getListEventFriends().get(0).setAccepted("accepted");
        waiting_time(5000);

        // Check that the invitation is accepted on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);
        firebaseRecover.recoverBikeEventsUser("id1", new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                int index = findIndexBikeEvent("id1_01_01_2000_14:00",listBikeEvent);
                List<EventFriends> listEventFriends = listBikeEvent.get(index).getListEventFriends();
                int position = UtilsApp.findFriendIndexInListEventFriends("id2",listEventFriends);
                // check that id2 accepted
                Assert.assertEquals("accepted", listEventFriends.get(position).getAccepted() );
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // Check that the invitation doesn't exists anymore for id2
        firebaseRecover.recoverInvitationsUser("id2", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(-1, findIndexBikeEvent("id1_01_01_2000_14:00",listInvitations));
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // Check that the invitation has become an event in "my_events" for id2
        firebaseRecover.recoverInvitationsUser("id2", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertTrue(findIndexBikeEvent("id1_01_01_2000_14:00",listInvitations) == -1);
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // --------------------------------------------------------  id3 rejected the invitation

        Action.rejectInvitation(bikeEvent, "id3", context);
        waiting_time(3000);

        // Check that the invitation is rejected on Firebase
        firebaseRecover.recoverBikeEventsUser("id1", new OnBikeEventDataGetListener() {
            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listBikeEvent) {
                int index = findIndexBikeEvent("id1_01_01_2000_14:00",listBikeEvent);
                List<EventFriends> listEventFriends = listBikeEvent.get(index).getListEventFriends();
                int position = UtilsApp.findFriendIndexInListEventFriends("id3",listEventFriends);
                // check that id3 rejected
                Assert.assertEquals("rejected", listEventFriends.get(position).getAccepted() );
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // Check that the invitation doesn't exists anymore for id3
        firebaseRecover.recoverInvitationsUser("id3", new OnBikeEventDataGetListener() {

            @Override
            public void onSuccess(BikeEvent bikeEvent) {}

            @Override
            public void onSuccess(List<BikeEvent> listInvitations) {
                Assert.assertEquals(-1, findIndexBikeEvent("id1_01_01_2000_14:00",listInvitations));
            }

            @Override
            public void onFailure(String error) {}
        });
        waiting_time(3000);

        // ----------------------------------------------------- DELETE BIKE EVENT
        Action.cancelBikeEvent(bikeEvent,"id1",context);
        waiting_time(3000);
    }



    // -------------------------------------------- UTILS ---------------------------------------------------------

    private int findIndexRouteFromName(String name, List<Route> listRoutes){

        int index = -1;

        for(int i = 0; i<listRoutes.size();i++){
            if(listRoutes.get(i).getName().equals(name)){
                index = i;
                break;
            }
        }

        return index;
    }

    private BikeEvent getBikeEventDEMO(){

        BikeEvent bikeEvent = new BikeEvent("id1_01_01_2000_14:00", "id1", "01/01/2000", "14:00", 999, "Comments : take good shoes", "accepted");
        Route route = new Route(999,"Trip to Paris",true);
        route.setListRouteSegment(getListRouteSegments());
        bikeEvent.setRoute(route);

        bikeEvent.setListEventFriends(getListEventFriends());

        return bikeEvent;
    }

    private int findIndexBikeEvent(String idEvent, List<BikeEvent> listBikeEvent){

        int index = -1;

        if(listBikeEvent!=null){
            if(listBikeEvent.size()>0){
                for(int i = 0; i < listBikeEvent.size();i++){
                    if(listBikeEvent.get(i).getId().equals(idEvent))
                        index = i;
                }
            }
        }

        return index;
    }

    private void waiting_time(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Route getRouteFromDatabase(int idRoute, String userId){

        Context context = getInstrumentation().getTargetContext();

        Cursor cursor = AppDatabase.getInstance(context, userId).routesDao().getRoute(idRoute);
        Route route = Route.getRouteFromCursor(cursor);

        Cursor cursorSegments = AppDatabase.getInstance(context, userId).routeSegmentDao().getRouteSegment(idRoute);
        List<RouteSegment> listRoutesSegments = RouteSegment.getRouteSegmentFromCursor(cursorSegments);

        route.setListRouteSegment(listRoutesSegments);

        return route;
    }

    private List<RouteSegment> getListRouteSegments(){

        List<RouteSegment> listRouteSegments = new ArrayList<>();
        RouteSegment ROUTE_SEG1_DEMO = new RouteSegment(0,1,48.819446, 2.344624,999);
        RouteSegment ROUTE_SEG2_DEMO = new RouteSegment(0,2,48.885412, 2.336589,999);
        RouteSegment ROUTE_SEG3_DEMO = new RouteSegment(0,3,48.874563, 2.312778,999);
        RouteSegment ROUTE_SEG4_DEMO = new RouteSegment(0,4,48.858933, 2.321511,999);

        listRouteSegments.add(ROUTE_SEG1_DEMO);
        listRouteSegments.add(ROUTE_SEG2_DEMO);
        listRouteSegments.add(ROUTE_SEG3_DEMO);
        listRouteSegments.add(ROUTE_SEG4_DEMO);

        return listRouteSegments;
    }

    private List<EventFriends> getListEventFriends(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"id1_01_01_2000_14:00","id2","id2","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(0,"id1_01_01_2000_14:00","id3","id3","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);

        return listEventFriends;
    }

    private void setFriendsDatabase(Context context, String userId){

        Friend friend1 = new Friend("id1","id1","Mat","photoUrl","accepted", "ongoing");
        Friend friend2 = new Friend("id2","id2","Seb","photoUrl","accepted","ongoing");
        Friend friend3 = new Friend("id3","id3","Camille","photoUrl","rejected","ongoing");

        FriendsHandler.insertNewFriend(context,friend1, userId);
        FriendsHandler.insertNewFriend(context,friend2, userId);
        FriendsHandler.insertNewFriend(context,friend3, userId);
    }
}


    /*@Test
    public void testFirebase_AcceptRoute() throws InterruptedException {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child("id2").child("my_routes").removeValue();
        databaseReference.child("id2").child("my_events").removeValue();

        setFriendsDatabase(getContext(), auth.getUid());
        BikeEvent bikeEvent = getBikeEventDEMO();

        // ----------------------------- INSERT BIKE EVENT and ID2 ACCEPT INVITATION

        Action.addBikeEvent(bikeEvent,"id1",getContext());
        waiting_time(2000);

        Action.acceptInvitation(bikeEvent, "id2", getContext());
        waiting_time(2000);

        Action.addInvitRouteToMyRoutes(bikeEvent,"id2",getContext());
        waiting_time(2000);

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        firebaseRecover.recoverRoutesUser("id2", new OnRouteDataGetListener() {
            @Override
            public void onSuccess(List<Route> listRoutes) {
                // Check if the route is present in listRoutes
                int index = findIndexRouteFromName("Trip to Paris",listRoutes);
                Assert.assertTrue(index!=-1);

                firebaseRecover.recoverBikeEventsUser("id2", new OnBikeEventDataGetListener() {
                    @Override
                    public void onSuccess(BikeEvent bikeEvent) {}

                    @Override
                    public void onSuccess(List<BikeEvent> bikeEvent) {
                        Assert.assertNull(bikeEvent.get(0).getRoute());
                    }

                    @Override
                    public void onFailure(String error) {}
                });
                waiting_time(10000);
            }

            @Override
            public void onFailure(String error) {
            }
        });

        waiting_time(20000);
    }*/
/*
    public void test_accept_reject_friend() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child("id2").child("my_friends").removeValue();
        databaseReference.child("id3").child("my_friends").removeValue();

        Friend friend1 = new Friend("id1","id1","Mat","photoUrl","accepted", "ongoing");
        Friend friend3 = new Friend("id3","id3","Camille","photoUrl","ongoing","ongoing");
        Friend user = new Friend("id2","id2","Seb","photoUrl","accepted","ongoing");

        // Add id3 and id1 as friend
        Action.addNewFriend(friend3, user, user.getId(), getContext());
        Action.addNewFriend(friend1, user,  user.getId(), getContext());

        waiting_time(5000);

        // id3 accepted id2 as friend
        Action.acceptFriend(user,"id3", getContext());

        // id1 rejected id2 as friend
        Action.rejectFriend(user,"id1", getContext());

        waiting_time(5000);

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);
        firebaseRecover.recoverFriendsUser("id1", new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {

            }

            @Override
            public void onSuccess(List<Friend> listFriend) {
                int index = UtilsApp.findFriendIndexInListFriends("id2", listFriend);
                Assert.assertEquals(-1, index);
            }

            @Override
            public void onFailure(String error) {

            }
        });

        waiting_time(5000);

        firebaseRecover.recoverFriendsUser("id2", new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {

            }

            @Override
            public void onSuccess(List<Friend> listFriend) {

                if(listFriend.get(0).getId().equals("id1")){
                    Assert.assertEquals("rejected", listFriend.get(0).getHasAgreed());
                    Assert.assertEquals("accepted", listFriend.get(1).getHasAgreed());
                } else {
                    Assert.assertEquals("accepted", listFriend.get(0).getHasAgreed());
                    Assert.assertEquals("rejected", listFriend.get(1).getHasAgreed());
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

        waiting_time(5000);

        firebaseRecover.recoverFriendsUser("id3", new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {

            }

            @Override
            public void onSuccess(List<Friend> listFriend) {
                Assert.assertTrue(listFriend.get(0).getAccepted().equals("accepted"));
            }

            @Override
            public void onFailure(String error) {

            }
        });

        waiting_time(5000);
    }*/
