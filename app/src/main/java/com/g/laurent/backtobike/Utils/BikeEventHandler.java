package com.g.laurent.backtobike.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.BikeEventContentProvider;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.EventFriendsContentProvider;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.RouteSegmentContentProvider;
import com.g.laurent.backtobike.Models.RoutesContentProvider;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;


public class BikeEventHandler {

    private static List<EventFriends> buildListEventFriends(int idEvent, List<Friend> listFriends){

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(listFriends!=null){
            if(listFriends.size()>0){ // if at least 1 friend
                for(Friend friend : listFriends){
                    listEventFriends.add(new EventFriends(0, idEvent, friend.getId()));
                }
            }
        }

        return listEventFriends;
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static int insertNewBikeEvent(Context context, String date, String time, int idRoute, String comments, List<Friend> listFriends, String status){

        BikeEvent bikeEvent = new BikeEvent(0, date, time, idRoute, comments, status);

        // Insert bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context);

        Uri uriInsert = bikeEventContentProvider.insert(BikeEventContentProvider.URI_ITEM, BikeEvent.createContentValuesFromBikeEventInsert(bikeEvent));
        int idEvent = (int) ContentUris.parseId(uriInsert);

        // Build list event Friends
        List<EventFriends> listEventFriends = buildListEventFriends(idEvent,listFriends);

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        // Add event friends to database
        if(listEventFriends.size()>0){
            for(EventFriends eventFriends : listEventFriends){
                eventFriends.setIdEvent(idEvent);
                eventFriendsContentProvider.insert(EventFriendsContentProvider.URI_ITEM, EventFriends.createContentValuesFromEventFriendsInsert(eventFriends));
            }
        }

        return idEvent;
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateBikeEvent(Context context, int idEvent, String date, String time, int idRoute, String comments, List<Friend> listFriends, String status){

        // Build bikeEvent
        BikeEvent bikeEvent = new BikeEvent(idEvent, date, time, idRoute, comments, status);

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context);

        Uri uriUpdate = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, idEvent);
        bikeEventContentProvider.update(uriUpdate,BikeEvent.createContentValuesFromBikeEventUpdate(bikeEvent),null,null);

        // Build list event friends
        List<EventFriends> listEventFriends = buildListEventFriends(idEvent,listFriends);

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        // Delete event friends related to this idEvent
        Uri uriDelete = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, idEvent);
        eventFriendsContentProvider.delete(uriDelete,null,null);

        // Add event friends to database
        if(listEventFriends.size()>0){
            for(EventFriends eventFriends : listEventFriends){
                eventFriendsContentProvider.insert(EventFriendsContentProvider.URI_ITEM, EventFriends.createContentValuesFromEventFriendsUpdate(eventFriends));
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static BikeEvent getBikeEvent(Context context, int idEvent){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, idEvent);
        final Cursor cursor = bikeEventContentProvider.query(uriQuery, null, null, null, null);

        return BikeEvent.getBikeEventFromCursor(cursor);
    }

    public static List<EventFriends> getEventFriends(Context context, int idEvent){

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, idEvent);
        final Cursor cursor = eventFriendsContentProvider.query(uriQuery, null, null, null, null);

        return EventFriends.getEventFriendsFromCursor(cursor);
    }
}
