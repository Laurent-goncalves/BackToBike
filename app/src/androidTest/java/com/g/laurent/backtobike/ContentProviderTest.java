package com.g.laurent.backtobike;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.FriendContentProvider;
import com.g.laurent.backtobike.Models.Route;
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
    private Route ROUTE_DEMO = new Route(0, "Trip around Paris", 48.819446, 2.344624, 48.897932, 2.343808, true);
    private Friend FRIEND_DEMO = new Friend(0,"Michel","photoURL");


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

        // Insert new route in database
        Uri uriInsert = friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriendInsert(FRIEND_DEMO));

        // Recover the route just inserted
        Uri uriQuery = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor cursor = friendContentProvider.query(uriQuery, null, null, null, null);

        // check that name is the same as initially
        if (cursor != null) {
            while (cursor.moveToNext()) {
                assertThat(cursor.getString(cursor.getColumnIndexOrThrow("name")), is("Michel"));
            }
            cursor.close();
        }

        // Update the name for this route
        Uri uriUpdate = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));

        FRIEND_DEMO.setId((int) ContentUris.parseId(uriInsert));
        FRIEND_DEMO.setName("Paul");

        friendContentProvider.update(uriUpdate,Friend.createContentValuesFromFriendUpdate(FRIEND_DEMO),null,null);

        // Check that the name and the validity of the route are well updated
        uriQuery = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        final Cursor Newcursor = friendContentProvider.query(uriQuery, null, null, null, null);

        if (Newcursor != null) {
            while (Newcursor.moveToNext()) {
                assertThat(Newcursor.getString(Newcursor.getColumnIndexOrThrow("name")), is("Paul"));
            }
            Newcursor.close();
        }

        // Delete ROUTE_DEMO
        Uri uriDelete = ContentUris.withAppendedId(FriendContentProvider.URI_ITEM, ContentUris.parseId(uriInsert));
        friendContentProvider.delete(uriDelete,null,null);
    }
}
