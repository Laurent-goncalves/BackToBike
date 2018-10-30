package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.database.Cursor;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.BikeEventContentProvider;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.EventFriendsContentProvider;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;

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

    public static void insertNewBikeEvent(Context context, BikeEvent bikeEvent, String userId){

        // Insert bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getId(), userId);

        bikeEventContentProvider.insert(null, BikeEvent.createContentValuesFromBikeEventInsert(bikeEvent));

        // Build list event Friends
        List<EventFriends> listEventFriends = bikeEvent.getListEventFriends();

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, null, bikeEvent.getId(), userId);

        // Add event friends to database
        if(listEventFriends!=null) {
            if (listEventFriends.size() > 0) {
                for (EventFriends eventFriends : listEventFriends) {
                    eventFriends.setIdEvent(bikeEvent.getId());
                    eventFriendsContentProvider.insert(null, EventFriends.createContentValuesFromEventFriendsInsert(eventFriends));
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateBikeEvent(Context context, BikeEvent bikeEvent, String userId){

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getId(), userId);

        bikeEventContentProvider.update(null, BikeEvent.createContentValuesFromBikeEventUpdate(bikeEvent),null,null);

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, null, bikeEvent.getId(),userId);

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

    public static void deleteBikeEvent(Context context, BikeEvent bikeEvent, String userId){

        // Delete event friends related to this idEvent
        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context,null,bikeEvent.getId(), userId);
        eventFriendsContentProvider.delete(null,null,null);

        // Update bikeEvent in database
        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, bikeEvent.getId(), userId);
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
                    listEventFriends.add(new EventFriends(0, idEvent, friend.getId(),friend.getLogin(),ONGOING));
                }
            }
        }

        return listEventFriends;
    }

    public static BikeEvent getBikeEvent(Context context, String idEvent, String userId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context, TYPE_SINGLE_EVENT, idEvent, userId);
        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        BikeEvent bikeEvent = BikeEvent.getBikeEventFromCursor(cursor);

        List<EventFriends> listEventFriends = getEventFriends(context,bikeEvent.getId(), userId);
        bikeEvent.setListEventFriends(listEventFriends);

        return bikeEvent;
    }

    public static List<BikeEvent> getAllFutureBikeEvents(Context context, String userId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_EVENTS,null, userId);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        List<BikeEvent> listBikeEvent = BikeEvent.getListBikeEventsFromCursor(cursor);

        if(listBikeEvent.size()>0) {

            for(BikeEvent event : listBikeEvent){

                // Event friends
                List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,event.getId(),userId);
                event.setListEventFriends(listEventFriends);

                // Route and RouteSegments
                Route route = RouteHandler.getRoute(context,event.getIdRoute(),userId);
                List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,event.getIdRoute(),userId);
                route.setListRouteSegment(listSegments);
                event.setRoute(route);
            }
        }

        return listBikeEvent;
    }

    public static List<BikeEvent> getAllInvitations(Context context, String userId){

        BikeEventContentProvider bikeEventContentProvider = new BikeEventContentProvider();
        bikeEventContentProvider.setUtils(context,TYPE_MY_INVITS, null, userId);

        final Cursor cursor = bikeEventContentProvider.query(null, null, null, null, null);

        List<BikeEvent> listInvitations = BikeEvent.getListBikeEventsFromCursor(cursor);

        if(listInvitations.size()>0) {
            for(BikeEvent invit : listInvitations){

                // Event friends
                List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,invit.getId(),userId);
                invit.setListEventFriends(listEventFriends);

                // Route and RouteSegments
                Route route = RouteHandler.getRoute(context,invit.getIdRoute(),userId);
                List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,invit.getIdRoute(),userId);
                route.setListRouteSegment(listSegments);
                invit.setRoute(route);
            }
        }

        return listInvitations;
    }

    public static List<EventFriends> getEventFriends(Context context, String idEvent, String userId){

        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, null, idEvent, userId);

        final Cursor cursor = eventFriendsContentProvider.query(null, null, null, null, null);

        return EventFriends.getEventFriendsFromCursor(cursor);
    }
}
