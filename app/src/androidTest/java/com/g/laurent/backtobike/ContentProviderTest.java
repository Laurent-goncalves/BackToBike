package com.g.laurent.backtobike;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.BikeEventContentProvider;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.EventFriendsContentProvider;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.FriendContentProvider;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.RouteSegmentContentProvider;
import com.g.laurent.backtobike.Models.RoutesContentProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;



@RunWith(AndroidJUnit4.class)
public class ContentProviderTest {

    // FOR DATA
    private RoutesContentProvider routesContentProvider;
    private FriendContentProvider friendContentProvider;
    private BikeEventContentProvider bikeEventContentProvider;
    private RouteSegmentContentProvider routeSegmentContentProvider;
    private EventFriendsContentProvider eventFriendsContentProvider;
    private Route ROUTE_DEMO = new Route(0, "Trip around Paris", 48.819446, 2.344624, 48.897932, 2.343808, true);
    private Friend FRIEND_DEMO = new Friend(0,"Michel","photoURL");
    private BikeEvent BIKE_EVENT_DEMO = new BikeEvent(0,"05/08/2018","14:00",0,"Comments : take water","accepted");
    private RouteSegment ROUTE_SEG_DEMO = new RouteSegment(0,48.819446, 2.344624, 48.897932, 2.343808,0);
    private EventFriends EVENT_FRIENDS_DEMO = new EventFriends(0,0,0);

    @Before
    public void setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(InstrumentationRegistry.getTargetContext());

        friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(InstrumentationRegistry.getTargetContext());

        bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(InstrumentationRegistry.getTargetContext());

        routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(InstrumentationRegistry.getTargetContext());

        eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void addUpdateDeleteRouteInDatabase() {

        // Insert new route in database
        Uri uriInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(ROUTE_DEMO));

        // Recover the route just inserted
        Uri uriQuery = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = routesContentProvider.query(uriQuery, null, null, null, null);

        // check that name is the same as initially
        if (cursor != null) {
            while (cursor.moveToNext()) {
                assertThat(cursor.getString(cursor.getColumnIndexOrThrow("name")), is("Trip around Paris"));
            }
            cursor.close();
        }

        // Update the name for this route
        Uri uriUpdate = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        ROUTE_DEMO.setId((int) ContentUris.parseId(uriInsert));
        ROUTE_DEMO.setName("Trip around Madrid");
        ROUTE_DEMO.setValid(false);

        routesContentProvider.update(uriUpdate,Route.createContentValuesFromRouteUpdate(ROUTE_DEMO),null,null);

        // Check that the name and the validity of the route are well updated
        uriQuery = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor Newcursor = routesContentProvider.query(uriQuery, null, null, null, null);

        if (Newcursor != null) {
            while (Newcursor.moveToNext()) {
                assertThat(Newcursor.getString(Newcursor.getColumnIndexOrThrow("name")), is("Trip around Madrid"));
                assertThat(Newcursor.getInt(Newcursor.getColumnIndexOrThrow("valid")), is(0)); // 0 = false
            }
            Newcursor.close();
        }

        // Delete ROUTE_DEMO
        Uri uriDelete = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        routesContentProvider.delete(uriDelete,null,null);
    }

    @Test
    public void addUpdateDeleteFriendInDatabase() {

        // Insert new friend in database
        Uri uriInsert = friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriendInsert(FRIEND_DEMO));

        // Recover the friend details just inserted
        Uri uriQuery = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = friendContentProvider.query(uriQuery, null, null, null, null);

        // check that name is the same as initially
        if (cursor != null) {
            while (cursor.moveToNext()) {
                assertThat(cursor.getString(cursor.getColumnIndexOrThrow("name")), is("Michel"));
            }
            cursor.close();
        }

        // Update the name for this friend
        Uri uriUpdate = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        FRIEND_DEMO.setId((int) ContentUris.parseId(uriInsert));
        FRIEND_DEMO.setName("Paul");

        friendContentProvider.update(uriUpdate,Friend.createContentValuesFromFriendUpdate(FRIEND_DEMO),null,null);

        // Check that the name of the friend is well updated
        uriQuery = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor Newcursor = friendContentProvider.query(uriQuery, null, null, null, null);

        if (Newcursor != null) {
            while (Newcursor.moveToNext()) {
                assertThat(Newcursor.getString(Newcursor.getColumnIndexOrThrow("name")), is("Paul"));
            }
            Newcursor.close();
        }

        // Delete friend_demo
        Uri uriDelete = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        friendContentProvider.delete(uriDelete,null,null);
    }

    @Test
    public void addUpdateDeleteBikeEventInDatabase() {

        // Insert new route in database and add idRoute to BIKE_EVENT_DEMO
        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(ROUTE_DEMO));
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);
        BIKE_EVENT_DEMO.setIdRoute(idRoute);

        // Insert new bikeEvent in database
        Uri uriInsert = bikeEventContentProvider.insert(BikeEventContentProvider.URI_ITEM, BikeEvent.createContentValuesFromBikeEventInsert(BIKE_EVENT_DEMO));

        // Recover the route just inserted
        Uri uriQuery = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = bikeEventContentProvider.query(uriQuery, null, null, null, null);

        // check that name is the same as initially
        if (cursor != null) {
            while (cursor.moveToNext()) {
                assertThat(cursor.getString(cursor.getColumnIndexOrThrow("comments")), is("Comments : take water"));
            }
            cursor.close();
        }

        // Update the name for this route
        Uri uriUpdate = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        BIKE_EVENT_DEMO.setId((int) ContentUris.parseId(uriInsert));
        BIKE_EVENT_DEMO.setTime("15:00");
        BIKE_EVENT_DEMO.setComments("Comments : don't take water");

        bikeEventContentProvider.update(uriUpdate,BikeEvent.createContentValuesFromBikeEventUpdate(BIKE_EVENT_DEMO),null,null);

        // Check that the name and the validity of the route are well updated
        uriQuery = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor Newcursor = bikeEventContentProvider.query(uriQuery, null, null, null, null);

        if (Newcursor != null) {
            while (Newcursor.moveToNext()) {
                assertThat(Newcursor.getString(Newcursor.getColumnIndexOrThrow("time")), is("15:00"));
                assertThat(Newcursor.getString(Newcursor.getColumnIndexOrThrow("comments")), is("Comments : don't take water"));
            }
            Newcursor.close();
        }

        // Delete BIKE_EVENT_DEMO
        Uri uriDelete = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        bikeEventContentProvider.delete(uriDelete,null,null);

        // Delete ROUTE_DEMO
        Uri uriRouteDelete = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriRouteInsert));
        routesContentProvider.delete(uriRouteDelete,null,null);
    }

    @Test
    public void addUpdateDeleteRouteSegmentInDatabase() {

        // Insert new route in database and add idRoute to ROUTE_SEG1_DEMO and ROUTE_SEG2_DEMO
        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(ROUTE_DEMO));
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);
        ROUTE_SEG_DEMO.setIdRoute(idRoute);

        // Insert new route segments in database
        Uri uriInsert = routeSegmentContentProvider.insert(RouteSegmentContentProvider.URI_ITEM, RouteSegment.createContentValuesFromRouteSegmentInsert(ROUTE_SEG_DEMO));

        // Recover the route segments just inserted
        Uri uriQuery = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = routeSegmentContentProvider.query(uriQuery, null, null, null, null);

        RouteSegment routeSegment = RouteSegment.getRouteSegmentFromCursor(cursor);

        assertThat(routeSegment.getStartPointlat(), is(48.819446));

        // Update the values for these route segments
        Uri uriUpdate1 = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        ROUTE_SEG_DEMO.setId((int) ContentUris.parseId(uriInsert));
        ROUTE_SEG_DEMO.setStartPointlat(48.888888);

        routeSegmentContentProvider.update(uriUpdate1,RouteSegment.createContentValuesFromRouteSegmentUpdate(ROUTE_SEG_DEMO),null,null);

        // Check that the values of the routes segment are well updated
        uriQuery = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        final Cursor Newcursor = routeSegmentContentProvider.query(uriQuery, null, null, null, null);

        routeSegment = RouteSegment.getRouteSegmentFromCursor(Newcursor);
        assertThat(routeSegment.getStartPointlat(), is(48.888888));

        // Delete ROUTE_SEG1_DEMO and ROUTE_SEG2_DEMO
        Uri uriDelete = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        routeSegmentContentProvider.delete(uriDelete,null,null);

        // Delete ROUTE_DEMO
        Uri uriRouteDelete = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriRouteInsert));
        routesContentProvider.delete(uriRouteDelete,null,null);
    }

    @Test
    public void addUpdateDeleteEventFriendsInDatabase() {

        // Insert new route, new friend and new event in database
        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(ROUTE_DEMO));
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);

        Uri uriFriendInsert = friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriendInsert(FRIEND_DEMO));
        int idFriend = (int) ContentUris.parseId(uriFriendInsert);

        BIKE_EVENT_DEMO.setIdRoute(idRoute);
        Uri uriEventInsert = bikeEventContentProvider.insert(BikeEventContentProvider.URI_ITEM, BikeEvent.createContentValuesFromBikeEventInsert(BIKE_EVENT_DEMO));
        int idEvent = (int) ContentUris.parseId(uriEventInsert);

        EVENT_FRIENDS_DEMO.setIdFriend(idFriend);
        EVENT_FRIENDS_DEMO.setIdEvent(idEvent);

        // Insert new bikeEvent in database
        Uri uriInsert = eventFriendsContentProvider.insert(EventFriendsContentProvider.URI_ITEM, EventFriends.createContentValuesFromEventFriendsInsert(EVENT_FRIENDS_DEMO));

        // Recover the route just inserted
        Uri uriQuery = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = eventFriendsContentProvider.query(uriQuery, null, null, null, null);

        EventFriends eventFriends = EventFriends.getEventFriendsFromCursor(cursor);
        assertThat(eventFriends.getIdEvent(), is(idEvent));
        assertThat(eventFriends.getIdFriend(), is(idFriend));

        // Delete EVENT_FRIENDS_DEMO
        Uri uriDelete = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        eventFriendsContentProvider.delete(uriDelete,null,null);

        // Delete BIKE_EVENT_DEMO
        Uri uriBikeEventDelete = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, ContentUris.parseId(uriEventInsert));
        bikeEventContentProvider.delete(uriBikeEventDelete,null,null);

        // Delete FRIEND_DEMO
        Uri uriFriendDelete = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriFriendInsert));
        friendContentProvider.delete(uriFriendDelete,null,null);

        // Delete ROUTE_DEMO
        Uri uriRouteDelete = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, ContentUris.parseId(uriRouteInsert));
        routesContentProvider.delete(uriRouteDelete,null,null);
    }

}