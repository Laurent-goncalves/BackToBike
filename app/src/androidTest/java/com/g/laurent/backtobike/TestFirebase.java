package com.g.laurent.backtobike;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnRouteDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import junit.framework.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class TestFirebase extends AndroidTestCase {

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;

    @Override
    public void setUp() throws InterruptedException {
        authSignal = new CountDownLatch(10);

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
        authSignal.await(20, TimeUnit.SECONDS);
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

        Route route = new Route(0,"trip to Lille",true);
        route.setListRouteSegment(getListRouteSegments());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(auth.getUid()).child("my_routes").removeValue();

        // ------------------------------------------------ INSERT route

        // insert route in Database and Firebase
        int idRoute = Action.addNewRoute(route,auth.getUid(),getContext());

        // Check that route is well added to Database
        Route routeTest = getRouteFromDatabase(idRoute);

        Assert.assertEquals("trip to Lille", routeTest.getName());
        Assert.assertEquals(4, routeTest.getListRouteSegment().size());

        // Check that route is well added to Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(auth.getUid(), new OnRouteDataGetListener() {
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
        Action.updateRoute(route, auth.getUid(), getContext());

        // Check that route is well updated in Database
        routeTest = getRouteFromDatabase(idRoute);
        Assert.assertEquals("Trip to Marseille", routeTest.getName());

        // Check that route is well updated in Firebase

        readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(auth.getUid(), new OnRouteDataGetListener() {
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
        Action.deleteRoute(route, auth.getUid(), getContext());

        // Check that route is well "deleted" in Database
        routeTest = getRouteFromDatabase(idRoute);
        Assert.assertFalse(routeTest.getValid());

        // Check that route is well "deleted" in Firebase

        readSignal = new CountDownLatch(5);
        readSignal.countDown();

        firebaseRecover.recoverRoutesUser(auth.getUid(), new OnRouteDataGetListener() {
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
    public void test_my_friends() throws InterruptedException {

        // ------------------------------------------------ INSERT friend

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        Friend friend = new Friend("id0","id0_login","Seb","photo_url",true);

        Action.addNewFriend(friend, "login_user", auth.getCurrentUser(), getContext());

        // Check that the friend is well added to database
        Friend friendTest = FriendsHandler.getFriend(getContext(),"id0");

        Assert.assertEquals("Seb", friendTest.getName());
        Assert.assertEquals("id0_login", friendTest.getLogin());

        // Check that the friend is well added to Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        firebaseRecover.recoverFriendsUser(auth.getUid(), new OnFriendDataGetListener() {

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

        Action.updateFriend(friend, auth.getUid(), getContext());

        // Check that the friend is well updated in database
        friendTest = FriendsHandler.getFriend(getContext(),"id0");
        Assert.assertEquals("Sean", friendTest.getName());

        // Check that the friend is well updated in Firebase

        firebaseRecover.recoverFriendsUser(auth.getUid(), new OnFriendDataGetListener() {
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

        Action.deleteFriend(friend,auth.getUid(),getContext());

        waiting_time(2000);

        // Check that the friend is well deleted in database
        friendTest = FriendsHandler.getFriend(getContext(),"id0");
        Assert.assertNull(friendTest.getId());

        // Check that the friend is well deleted in Firebase

        firebaseRecover.recoverFriendsUser(auth.getUid(), new OnFriendDataGetListener() {
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
    public void test_my_invitations() throws InterruptedException {


    }





    @Test
    public void testFirebase_BikeEvents() throws InterruptedException {

        // WRITE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(databaseReference);

        BikeEvent bikeEvent = new BikeEvent(9999, "id2", "01/01/2000", "14:00", 999, "Comments : take good shoes", "accepted");
        // TODO firebaseUpdate.updateMyBikeEvent("id2", bikeEvent, getListEventFriends());

        // READ
        final CountDownLatch readSignal = new CountDownLatch(3);
        readSignal.countDown();

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        List<BikeEvent> listBikeEvents = firebaseRecover.recoverBikeEventsUser("id2");

        Assert.assertEquals(1, listBikeEvents.size());
        Assert.assertEquals("id3", listBikeEvents.get(0).getListEventFriends().get(1).getIdFriend());

        readSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFirebase_Invitations() throws InterruptedException {

        // WRITE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(databaseReference);

        BikeEvent bikeEvent = new BikeEvent(9999, "id2", "01/01/2000", "14:00", 999, "Comments : take good shoes", "null",getListEventFriends());
        Route route = new Route(998,"Trip to Madrid",true, getListRouteSegments());

        // TODO firebaseUpdate.updateMyBikeEvent("id2", bikeEvent, getListEventFriends());
        firebaseUpdate.addInvitationGuests(bikeEvent);

        // READ
        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        List<BikeEvent> listBikeEvents = firebaseRecover.recoverInvitationsUser("id1");

        Assert.assertEquals(1, listBikeEvents.size());

        // CHANGE GUESTS ACCEPTANCE

        firebaseUpdate.giveAnswerToInvitation("id1", bikeEvent,"rejected");
        firebaseUpdate.giveAnswerToInvitation("id3", bikeEvent,"accepted");

        // READ
        List<EventFriends> listEventFriends = firebaseRecover.recoverEventFriends("id2", bikeEvent);

        Assert.assertFalse(getAcceptanceStatus("id1",listEventFriends));

        Assert.assertTrue(getAcceptanceStatus("id3",listEventFriends));

        readSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFirebase_AcceptRoute() throws InterruptedException {

        // WRITE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(databaseReference);

        BikeEvent bikeEvent = new BikeEvent(9999, "id2", "01/01/2000", "14:00", 999, "Comments : take good shoes", "null",getListEventFriends());
        Route route = new Route(998,"Trip to Madrid",true, getListRouteSegments());

        // TODO firebaseUpdate.updateMyBikeEvent("id2", bikeEvent, getListEventFriends());
        firebaseUpdate.addInvitationGuests(bikeEvent);

        // id1 accept route
        firebaseUpdate.acceptRoute("id1", route, bikeEvent);

        // READ
        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);
        //List<Route> listRoutes = firebaseRecover.recoverRoutesUser("id1");

        //Assert.assertTrue(checkIfRouteExistsAmongMyRoutes(route.getId(),listRoutes));

        readSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFirebase_CancelBikeEvent() throws InterruptedException {

        // WRITE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(databaseReference);

        BikeEvent bikeEvent = new BikeEvent(9999, "id2", "01/01/2000", "14:00", 999, "Comments : take good shoes", "null",getListEventFriends());
        Route route = new Route(998,"Trip to Madrid",true, getListRouteSegments());

        // TODO firebaseUpdate.updateMyBikeEvent("id2", bikeEvent, getListEventFriends());
        firebaseUpdate.addInvitationGuests(bikeEvent);

        // cancel bikeEvent
        firebaseUpdate.cancelMyBikeEvent(bikeEvent.getOrganizerId(),getListEventFriends(),bikeEvent);

        // READ
        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        List<BikeEvent> listBikeEvent = firebaseRecover.recoverBikeEventsUser(bikeEvent.getOrganizerId());
        List<BikeEvent> listInvitations = firebaseRecover.recoverInvitationsUser("id1");

        Assert.assertFalse(checkIfBikeEventExistsAmongMyEvents(bikeEvent.getId(), listBikeEvent));
        Assert.assertFalse(checkIfBikeEventExistsAmongMyEvents(bikeEvent.getId(), listInvitations));

        readSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFirebase_LoginValidity() throws InterruptedException {

        // WRITE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        databaseReference.child("id1").child("login").setValue("id1");
        databaseReference.child("id2").child("login").setValue("id2");
        databaseReference.child("id2").child("my_friends").child("id1").child("login").setValue("id1");
        databaseReference.child("id3").child("login").setValue("id3");

        // READ
        CountDownLatch readSignal = new CountDownLatch(5);
        readSignal.countDown();

        FirebaseRecover firebaseRecover = new FirebaseRecover(databaseReference);

        String login1 = "id1";
        String login2 = "id4";
        String login3 = "id1";

        // TODO Boolean isLogin1OK = firebaseRecover.isLoginNotAmongUserFriends(login1,"id3") && firebaseRecover.isLoginOnFirebase(login1);
        // TODO Boolean isLogin2NOK = firebaseRecover.isLoginNotAmongUserFriends(login2,"id3") && firebaseRecover.isLoginOnFirebase(login2);
        // TODO Boolean isLogin3NOK = firebaseRecover.isLoginNotAmongUserFriends(login3,"id2") && firebaseRecover.isLoginOnFirebase(login3);

        // TODO Assert.assertTrue(isLogin1OK);
        // TODO Assert.assertFalse(isLogin2NOK);
        // TODO Assert.assertFalse(isLogin3NOK);

        readSignal.await(10, TimeUnit.SECONDS);
    }


    // -------------------------------------------- UTILS ---------------------------------------------------------

    private void waiting_time(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Route getRouteFromDatabase(int idRoute){

        Cursor cursor = AppDatabase.getInstance(getContext()).routesDao().getRoute(idRoute);
        Route route = Route.getRouteFromCursor(cursor);

        Cursor cursorSegments = AppDatabase.getInstance(getContext()).routeSegmentDao().getRouteSegment(idRoute);
        List<RouteSegment> listRoutesSegments = RouteSegment.getRouteSegmentFromCursor(cursorSegments);

        route.setListRouteSegment(listRoutesSegments);

        return route;
    }


    private Boolean checkIfBikeEventExistsAmongMyEvents(int idEvent, List<BikeEvent> listBikeEvent){

        Boolean answer = false;

        if(listBikeEvent!=null){
            if(listBikeEvent.size()>0){
                for(BikeEvent bikeEvent : listBikeEvent){
                    if(bikeEvent.getId()==idEvent){
                        answer = true;
                        break;
                    }
                }
            }
        }

        return answer;
    }

    private Boolean checkIfRouteExistsAmongMyRoutes(int idRoute, List<Route> listRoutes){

        Boolean answer = false;

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(Route route : listRoutes){
                    if(route.getId()==idRoute){
                        answer = true;
                        break;
                    }
                }
            }
        }

        return answer;
    }

    private Boolean getAcceptanceStatus(String idFriend, List<EventFriends> listEventFriends){

        Boolean answer = false;

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    if(eventFriends.getIdFriend()!=null){
                        if(eventFriends.getIdFriend().equals(idFriend)){
                            answer = eventFriends.getAccepted();
                            break;
                        }
                    }
                }
            }
        }

        return answer;
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
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,9999,"id1",null);
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(1,9999,"id2",null);
        EventFriends EVENT_FRIENDS_DEMO_3 = new EventFriends(2,9999,"id3",null);

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        listEventFriends.add(EVENT_FRIENDS_DEMO_3);

        return listEventFriends;
    }

    private List<Friend> getListFriend(){

        List<Friend> listFriends = new ArrayList<>();

        Friend friend1 = new Friend("xs87z5d68gyf87tr1ff1","michel77","Michel","photoUrl",true);
        Friend friend2 = new Friend("dr87e46f13dssfsd321g","seb_77","Seb","photoUrl",true);
        Friend friend3 = new Friend("rtj178ojc23fdqf5456g","mat_91","Mathieu","photoUrl",false);
        Friend friend4 = new Friend("rer6484txd21h8ioil45","cyril1988","Cyril","photoUrl",true);
        Friend friend5 = new Friend("mioe16547ez15cft84z1","luke99","Luc","photoUrl",false);

        listFriends.add(friend1);
        listFriends.add(friend2);
        listFriends.add(friend3);
        listFriends.add(friend4);
        listFriends.add(friend5);

        return listFriends;
    }
}