package com.g.laurent.backtobike;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.g.laurent.backtobike.Controllers.TraceActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.RouteHandler;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class RouteAndEventHandlerTest {

    @Rule
    public ActivityTestRule<TraceActivity> mActivityRule = new ActivityTestRule<>(TraceActivity.class);

    @Test
    public void test_RouteHandler(){

        Context context = mActivityRule.getActivity().getApplicationContext();

        // insertion
        int idRoute = RouteHandler.insertNewRoute(context, getListPoints(),"Trip around Paris",true);

        // update
        RouteHandler.updateRoute(context,idRoute,getUpdatedListPoints(),"Trip into Paris",true);

        // get
        Route route = RouteHandler.getRoute(context,idRoute);
        List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,idRoute);

        Assert.assertEquals("Trip into Paris", route.getName());
        Assert.assertEquals(6, listSegments.size());
        Assert.assertEquals(48.666666, listSegments.get(2).getLat(), 0.0);
        Assert.assertEquals(2.402589, listSegments.get(5).getLng(), 0.0);
    }

    @Test
    public void test_BikeEventHandler(){

        Context context = mActivityRule.getActivity().getApplicationContext();

        // add a route for test
        int idRoute = RouteHandler.insertNewRoute(context, getListPoints(),"Trip around Paris",true);

        // insert friends:
        FriendsHandler.insertNewFriend(context,"id1","Paul","photoUrl");
        FriendsHandler.insertNewFriend(context,"id2","Michel","photoUrl");
        FriendsHandler.insertNewFriend(context,"id3","José","photoUrl");
        FriendsHandler.insertNewFriend(context,"id4","Seb","photoUrl");
        FriendsHandler.insertNewFriend(context,"id5","Bob","photoUrl");

        // insertion
        int idEvent = BikeEventHandler.insertNewBikeEvent(context,"03/03/2018","14:00", idRoute,"Comments : take a hat",getListFriends(),"accepted");

        // update
        BikeEventHandler.updateBikeEvent(context,idEvent,"04/03/2018","14:00",idRoute,"Comments : don't take a hat" ,getUpdatedListFriends(),"cancelled");

        // get
        BikeEvent bikeEvent = BikeEventHandler.getBikeEvent(context,idEvent);
        List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,idEvent);

        Assert.assertEquals("04/03/2018", bikeEvent.getDate());
        Assert.assertEquals("cancelled", bikeEvent.getStatus());
        Assert.assertEquals(4, listEventFriends.size());
        Assert.assertEquals("id2", listEventFriends.get(0).getIdFriend());
        Assert.assertEquals("id5", listEventFriends.get(3).getIdFriend());
    }

    private List<LatLng> getListPoints(){
        LatLng point1 = new LatLng(48.858109, 2.339278);
        LatLng point2 = new LatLng(48.800981, 2.520347);
        LatLng point3 = new LatLng(48.615268, 2.473662);
        LatLng point4 = new LatLng(48.587223, 2.445282);
        LatLng point5 = new LatLng(48.512365, 2.412563);
        LatLng point6 = new LatLng(48.552147, 2.402589);

        List<LatLng> list = new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        list.add(point5);
        list.add(point6);

        return list;
    }

    private List<LatLng> getUpdatedListPoints(){
        LatLng point1 = new LatLng(48.888888, 2.339278);
        LatLng point2 = new LatLng(48.800000, 2.520347);
        LatLng point3 = new LatLng(48.666666, 2.473662);
        LatLng point4 = new LatLng(48.587223, 2.445282);
        LatLng point5 = new LatLng(48.512365, 2.412563);
        LatLng point6 = new LatLng(48.552147, 2.402589);

        List<LatLng> list = new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        list.add(point5);
        list.add(point6);

        return list;
    }

    private List<Friend> getListFriends(){

        Friend friend1 = new Friend("id1","Paul","photoUrl");
        Friend friend2 = new Friend("id2","Michel","photoUrl");
        Friend friend3 = new Friend("id3","José","photoUrl");

        List<Friend> listFriends = new ArrayList<>();
        listFriends.add(friend1);
        listFriends.add(friend2);
        listFriends.add(friend3);

        return listFriends;
    }

    private List<Friend> getUpdatedListFriends(){

        Friend friend2 = new Friend("id2","Michel","photoUrl");
        Friend friend3 = new Friend("id3","José","photoUrl");
        Friend friend4 = new Friend("id4","Seb","photoUrl");
        Friend friend5 = new Friend("id5","Bob","photoUrl");

        List<Friend> listFriends = new ArrayList<>();
        listFriends.add(friend2);
        listFriends.add(friend3);
        listFriends.add(friend4);
        listFriends.add(friend5);

        return listFriends;
    }

}
