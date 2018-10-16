package com.g.laurent.backtobike;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.g.laurent.backtobike.Controllers.Activities.TraceActivity;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class RouteAndEventHandlerTest {

    @Rule
    public ActivityTestRule<TraceActivity> mActivityRule = new ActivityTestRule<>(TraceActivity.class);

    @Test
    public void test_RouteHandler(){

        Context context = mActivityRule.getActivity().getApplicationContext();

        Route route = new Route(0,"Trip around Paris",true);
        route.setListRouteSegment(getListRouteSegments());

        // insertion
        int idRoute = RouteHandler.insertNewRoute(context, route);

        route.setId(idRoute);
        route.setListRouteSegment(getUpdatedListRouteSegments());

        // update
        RouteHandler.updateRoute(context, route);

        // get
        Route routeToCheck = RouteHandler.getRoute(context,idRoute);
        List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,idRoute);

        Assert.assertEquals("Trip into Paris", routeToCheck.getName());
        Assert.assertEquals(5, listSegments.size());
        Assert.assertEquals(48.863363, listSegments.get(2).getLat(), 0.0);
        Assert.assertEquals(2.324222, listSegments.get(4).getLng(), 0.0);
    }

    @Test
    public void test_BikeEventHandler(){

        Context context = mActivityRule.getActivity().getApplicationContext();

        Route route = new Route(0,"Trip around Paris",true);
        route.setListRouteSegment(getListRouteSegments());

        // add a route for test
        int idRoute = RouteHandler.insertNewRoute(context, route);

        // insert friends:
        List<Friend> listFriends = getListFriends();
        FriendsHandler.insertNewFriend(context,listFriends.get(0));
        FriendsHandler.insertNewFriend(context,listFriends.get(1));
        FriendsHandler.insertNewFriend(context,listFriends.get(2));
        FriendsHandler.insertNewFriend(context,listFriends.get(3));
        FriendsHandler.insertNewFriend(context,listFriends.get(4));

        // insertion
        // TODO int idEvent = BikeEventHandler.insertNewBikeEvent(context,"organizerId","03/03/2018","14:00", idRoute,"Comments : take a hat",getListFriends(),"accepted");

        // update
        // TODO BikeEventHandler.updateBikeEvent(context,idEvent,"organizerId","04/03/2018","14:00",idRoute,"Comments : don't take a hat" ,getUpdatedListFriends(),"cancelled");

        // get
        // TODO BikeEvent bikeEvent = BikeEventHandler.getBikeEvent(context,idEvent);
        // TODO List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,idEvent);

        // TODO Assert.assertEquals("04/03/2018", bikeEvent.getDate());
        // TODO Assert.assertEquals("cancelled", bikeEvent.getStatus());
        // TODO Assert.assertEquals(4, listEventFriends.size());
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


    private List<RouteSegment> getUpdatedListRouteSegments(){

        List<RouteSegment> listRouteSegments = new ArrayList<>();
        RouteSegment ROUTE_SEG1_DEMO = new RouteSegment(0,1,48.819446, 2.344624,999);
        RouteSegment ROUTE_SEG2_DEMO = new RouteSegment(0,2,48.885457, 2.336589,999);
        RouteSegment ROUTE_SEG3_DEMO = new RouteSegment(0,3,48.863363, 2.317778,999);
        RouteSegment ROUTE_SEG4_DEMO = new RouteSegment(0,4,48.855911, 2.328511,999);
        RouteSegment ROUTE_SEG5_DEMO = new RouteSegment(0,5,48.844444, 2.324222,999);

        listRouteSegments.add(ROUTE_SEG1_DEMO);
        listRouteSegments.add(ROUTE_SEG2_DEMO);
        listRouteSegments.add(ROUTE_SEG3_DEMO);
        listRouteSegments.add(ROUTE_SEG4_DEMO);
        listRouteSegments.add(ROUTE_SEG5_DEMO);

        return listRouteSegments;
    }

    private List<Friend> getListFriends(){

        Friend friend1 = new Friend("id1","paul88","Paul","photoUrl",true);
        Friend friend2 = new Friend("id2","michel88","Michel","photoUrl",false);
        Friend friend3 = new Friend("id3","joseeee88","José","photoUrl",true);

        List<Friend> listFriends = new ArrayList<>();
        listFriends.add(friend1);
        listFriends.add(friend2);
        listFriends.add(friend3);

        return listFriends;
    }

    private List<Friend> getUpdatedListFriends(){

        Friend friend2 = new Friend("id2","michel88","Michel","photoUrl",false);
        Friend friend3 = new Friend("id3","joseeee88","José","photoUrl",true);
        Friend friend4 = new Friend("id4","seb990","Seb","photoUrl",false);
        Friend friend5 = new Friend("id5","bob99","Bob","photoUrl",true);

        List<Friend> listFriends = new ArrayList<>();
        listFriends.add(friend2);
        listFriends.add(friend3);
        listFriends.add(friend4);
        listFriends.add(friend5);

        return listFriends;
    }
}
