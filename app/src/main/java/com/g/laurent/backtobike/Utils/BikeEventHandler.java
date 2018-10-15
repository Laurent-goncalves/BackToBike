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

    private static final String TYPE_MY_EVENTS ="type_my_events";
    private static final String TYPE_MY_INVITS ="type_my_invits";
    private static final String TYPE_SINGLE_EVENT ="type_single_event";



    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static int insertNewBikeEvent(Context context, BikeEvent bikeEvent){

        // Insert bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId());

        Uri uriInsert = bikeEventContentProvider.insert(BikeEventContentProvider.URI_ITEM, BikeEvent.createContentValuesFromBikeEventInsert(bikeEvent));
        int idEvent = (int) ContentUris.parseId(uriInsert);

        // Build list event Friends
        List<EventFriends> listEventFriends = bikeEvent.getListEventFriends();

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

    public static void updateBikeEvent(Context context, BikeEvent bikeEvent){

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId());

        Uri uriUpdate = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, bikeEvent.getId());
        bikeEventContentProvider.update(uriUpdate,BikeEvent.createContentValuesFromBikeEventUpdate(bikeEvent),null,null);

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        // Delete event friends related to this idEvent
        Uri uriDelete = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, bikeEvent.getId());
        eventFriendsContentProvider.delete(uriDelete,null,null);

        // Add event friends to database
        if(bikeEvent.getListEventFriends().size()>0){
            for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                eventFriendsContentProvider.insert(EventFriendsContentProvider.URI_ITEM, EventFriends.createContentValuesFromEventFriendsUpdate(eventFriends));
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DELETE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void deleteBikeEvent(Context context, BikeEvent bikeEvent){

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId());

        Uri uriDeleteEvent = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, bikeEvent.getId());
        bikeEventContentProvider.delete(uriDeleteEvent,null,null);

        // Delete event friends related to this idEvent
        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        Uri uriDeleteFriends = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, bikeEvent.getId());
        eventFriendsContentProvider.delete(uriDeleteFriends,null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- UTILS --------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    private static List<EventFriends> buildListEventFriends(int idEvent, List<Friend> listFriends){

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(listFriends!=null){
            if(listFriends.size()>0){ // if at least 1 friend
                for(Friend friend : listFriends){
                    listEventFriends.add(new EventFriends(0, idEvent, friend.getId(),true));
                }
            }
        }

        return listEventFriends;
    }

    public static BikeEvent getBikeEvent(Context context, int idEvent, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, organizerId);

        Uri uriQuery = ContentUris.withAppendedId(BikeEventContentProvider.URI_ITEM, idEvent);
        final Cursor cursor = bikeEventContentProvider.query(uriQuery, null, null, null, null);

        return BikeEvent.getBikeEventFromCursor(cursor);
    }

    public static List<BikeEvent> getAllFutureBikeEvents(Context context, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_EVENTS,organizerId);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        return BikeEvent.getListBikeEventsFromCursor(cursor);
    }

    public static List<BikeEvent> getAllInvitiations(Context context, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_INVITS,organizerId);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        return BikeEvent.getListBikeEventsFromCursor(cursor);
    }

    public static List<EventFriends> getEventFriends(Context context, int idEvent){

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(EventFriendsContentProvider.URI_ITEM, idEvent);
        final Cursor cursor = eventFriendsContentProvider.query(uriQuery, null, null, null, null);

        return EventFriends.getEventFriendsFromCursor(cursor);
    }
}
