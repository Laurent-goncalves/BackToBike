package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.database.Cursor;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.BikeEventContentProvider;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.EventFriendsContentProvider;
import com.g.laurent.backtobike.Models.Friend;
import java.util.ArrayList;
import java.util.List;


public class BikeEventHandler {

    private static final String TYPE_MY_EVENTS ="type_my_events";
    private static final String TYPE_MY_INVITS ="type_my_invits";
    private static final String TYPE_SINGLE_EVENT ="type_single_event";
    private static final String ONGOING = "ongoing";


    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void insertNewBikeEvent(Context context, BikeEvent bikeEvent){

        // Insert bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId(), bikeEvent.getId());

        bikeEventContentProvider.insert(null, BikeEvent.createContentValuesFromBikeEventInsert(bikeEvent));

        // Build list event Friends
        List<EventFriends> listEventFriends = bikeEvent.getListEventFriends();

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, bikeEvent.getId());

        // Add event friends to database
        if(listEventFriends.size()>0){
            for(EventFriends eventFriends : listEventFriends){
                eventFriends.setIdEvent(bikeEvent.getId());
                eventFriendsContentProvider.insert(null, EventFriends.createContentValuesFromEventFriendsInsert(eventFriends));
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateBikeEvent(Context context, BikeEvent bikeEvent){

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId(),bikeEvent.getId());

        bikeEventContentProvider.update(null, BikeEvent.createContentValuesFromBikeEventUpdate(bikeEvent),null,null);

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, bikeEvent.getId());

        // Delete event friends related to this idEvent
        eventFriendsContentProvider.delete(null,null,null);

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

        // Delete event friends related to this idEvent
        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context,bikeEvent.getId());
        eventFriendsContentProvider.delete(null,null,null);

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getOrganizerId(),bikeEvent.getId());
        bikeEventContentProvider.delete(null,null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- UTILS --------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    private static List<EventFriends> buildListEventFriends(String idEvent, List<Friend> listFriends){

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(listFriends!=null){
            if(listFriends.size()>0){ // if at least 1 friend
                for(Friend friend : listFriends){
                    listEventFriends.add(new EventFriends(0, idEvent, friend.getId(),ONGOING));
                }
            }
        }

        return listEventFriends;
    }

    public static BikeEvent getBikeEvent(Context context, String idEvent, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, organizerId, idEvent);
        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        return BikeEvent.getBikeEventFromCursor(cursor);
    }

    public static List<BikeEvent> getAllFutureBikeEvents(Context context, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_EVENTS,organizerId,null);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        return BikeEvent.getListBikeEventsFromCursor(cursor);
    }

    public static List<BikeEvent> getAllInvitiations(Context context, String organizerId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_INVITS,organizerId,null);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        return BikeEvent.getListBikeEventsFromCursor(cursor);
    }

    public static List<EventFriends> getEventFriends(Context context, String idEvent){

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, idEvent);

        final Cursor cursor = eventFriendsContentProvider.query(null, null, null, null, null);

        return EventFriends.getEventFriendsFromCursor(cursor);
    }
}
