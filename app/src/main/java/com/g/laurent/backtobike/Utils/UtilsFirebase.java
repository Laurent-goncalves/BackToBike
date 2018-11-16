package com.g.laurent.backtobike.Utils;

import android.content.Context;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;


public class UtilsFirebase {

    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String NAME = "name";
    private static final String GUESTS = "guests";
    private static final String ROUTE = "route";
    private static final String STATUS = "status";
    private static final String VALID = "valid";
    private static final String HAS_ACCEPTED = "has_accepted";
    private static final String ACCEPTED = "accepted";
    private static final String PHOTO_URL = "photoUrl";
    private static final String LOGIN = "login";
    private static final String ID_ROUTE = "id_route";
    private static final String ID_FRIEND = "id_friend";
    private static final String ID_ORGANIZER = "id_organizer";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String COMMENTS = "comments";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";
    private static final String ONGOING = "ongoing";

    // --------------------------------------------------------------------------------------------------
    // --------------------------------------- BUILDERS -------------------------------------------------
    // --------------------------------------------------------------------------------------------------

    public static Route buildListRoute(DataSnapshot dataSnapshot){

        List<RouteSegment> listRouteSegments = new ArrayList<>();

        for(DataSnapshot datas : dataSnapshot.child(POINTS).getChildren()){

            int idRoute = 0;
            if(datas.child(ID_ROUTE).getValue()!=null)
                idRoute=Integer.parseInt(datas.child(ID_ROUTE).getValue().toString());

            listRouteSegments.add(new RouteSegment(Integer.parseInt(datas.child(ID).getValue().toString()),
                    Integer.parseInt(datas.getKey()),
                    (Double) datas.child(LAT).getValue(),
                    (Double) datas.child(LNG).getValue(),
                    idRoute));
        }

        int idRoute = 0;

        if(!dataSnapshot.getKey().equals(ROUTE))
            idRoute = Integer.parseInt(dataSnapshot.getKey());

        return new Route(idRoute,(String) dataSnapshot.child(NAME).getValue(),
                (Boolean) dataSnapshot.child(VALID).getValue(),listRouteSegments);
    }

    public static BikeEvent buildBikeEvent(DataSnapshot datas){

        String idRoute;
        if(datas.child(ID_ROUTE).getValue()!=null)
            idRoute = datas.child(ID_ROUTE).getValue().toString();
        else
            idRoute = "0";

        BikeEvent bikeEvent = new BikeEvent(datas.getKey(),(String) datas.child(ID_ORGANIZER).getValue(),
                (String) datas.child(DATE).getValue(),
                (String) datas.child(TIME).getValue(),
                Integer.parseInt(idRoute),
                (String) datas.child(COMMENTS).getValue(),
                (String) datas.child(STATUS).getValue(),
                buildListEventFriends(datas.child(GUESTS), datas.getKey()));

        if(datas.hasChild(ROUTE)){
            Route route = buildListRoute(datas.child(ROUTE));
            bikeEvent.setRoute(route);
        }

        return bikeEvent;
    }

    public static List<EventFriends> buildListEventFriends(DataSnapshot guests, String idEvent) {

        List<EventFriends> listEventFriends = new ArrayList<>();

        for(DataSnapshot datas : guests.getChildren()){
            listEventFriends.add(new EventFriends(0,
                    idEvent, (String) datas.child(ID_FRIEND).getValue(),
                    (String) datas.child(LOGIN).getValue(),
                    (String) datas.child(ACCEPTED).getValue()
            ));
        }

        return listEventFriends;
    }

    public static Friend buildFriend(DataSnapshot datas){

        return new Friend(datas.getKey(),
                (String) datas.child(LOGIN).getValue(),
                (String) datas.child(NAME).getValue(),
                (String) datas.child(PHOTO_URL).getValue(),
                (String) datas.child(ACCEPTED).getValue(),
                (String) datas.child(HAS_ACCEPTED).getValue());
    }

    // --------------------------------------------------------------------------------------------------
    // ----------------------------------- UTILS FOR COUNTERS -------------------------------------------
    // --------------------------------------------------------------------------------------------------

    public static int getCounterFriends(Context context, String userId, DataSnapshot dataSnapshot){

        DataSnapshot datasFriends = dataSnapshot.child(MY_FRIENDS);

        // Get current old list
        List<Friend> oldListFriends = FriendsHandler.getListFriends(context, userId);

        // Compare lists to get counters of modifications
        List<String> listDiffFriends = UtilsCounters.getListDifferencesForFriendRequests(context,datasFriends,oldListFriends);

        // Get counters
        return listDiffFriends.size();
    }

    public static int getCounterEvents(Context context, String userId, DataSnapshot dataSnapshot){

        DataSnapshot datasEvents = dataSnapshot.child(MY_EVENTS);

        // Get current list of events
        List<BikeEvent> oldListBikeEvent = BikeEventHandler.getAllFutureBikeEvents(context, userId);

        // Compare lists to get counters of modifications
        List<Difference> listDiffEvents = UtilsCounters.getListDifferencesBetweenListEvents(oldListBikeEvent,datasEvents,context);

        // Get counters
        return listDiffEvents.size();
    }

    public static int getCounterInvits(Context context, String userId, DataSnapshot dataSnapshot){

        DataSnapshot datasInvits = dataSnapshot.child(MY_INVITATIONS);

        // Get current list of invits
        List<BikeEvent> oldListInvitations = BikeEventHandler.getAllInvitations(context, userId);

        // Compare lists to get counters of modifications
        List<String> listDiffInvitations = UtilsCounters.getListDifferencesForInvitations(context, datasInvits, oldListInvitations);

        // Get counters
        return listDiffInvitations.size();
    }

    public static List<Difference> getListDifferencesEvents(Context context, String userId, DataSnapshot dataSnapshot){

        DataSnapshot datasEvents = dataSnapshot.child(MY_EVENTS);

        // Get current list of events
        List<BikeEvent> oldListBikeEvent = BikeEventHandler.getAllFutureBikeEvents(context, userId);

        // Compare lists to get counters of modifications
        return UtilsCounters.getListDifferencesBetweenListEvents(oldListBikeEvent,datasEvents,context);
    }

    public static List<String> getFullListDifferences(Context context, String userId, DataSnapshot dataSnapshot){

        List<String> fullListDifferences = new ArrayList<>();

        DataSnapshot datasFriends = dataSnapshot.child(MY_FRIENDS);
        DataSnapshot datasInvits = dataSnapshot.child(MY_INVITATIONS);

        // Get current list of events
        List<BikeEvent> oldListInvitations = BikeEventHandler.getAllInvitations(context, userId);
        List<Friend> oldListFriends = FriendsHandler.getListFriends(context, userId);

        // Compare lists to get counters of modifications
        List<String> listDiffFriends = UtilsCounters.getListDifferencesForFriendRequests(context,datasFriends,oldListFriends);
        List<String> listDiffInvitations = UtilsCounters.getListDifferencesForInvitations(context, datasInvits, oldListInvitations);

        fullListDifferences.addAll(listDiffFriends);
        fullListDifferences.addAll(listDiffInvitations);

        return fullListDifferences;
    }

    // --------------------------------------------------------------------------------------------------
    // --------------------------------------- LOGINS ---------------------------------------------------
    // --------------------------------------------------------------------------------------------------

    public static Boolean isLoginAmongDatas(String login, String userId, DataSnapshot dataSnapshot){

        Boolean answer = false;

        if(dataSnapshot.getChildren()!=null){
            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                if(datas.child(LOGIN).getValue()!=null){
                    if(datas.child(LOGIN).getValue().toString().equals(login) && !datas.getKey().equals(userId) ){
                        answer = true;
                        break;
                    }
                }
            }
        }

        return answer;
    }

    public static Friend getFriendWithLoginOnFirebase(String login, String userId, DataSnapshot dataSnapshot){

        Friend friend = null;

        if(dataSnapshot.getChildren()!=null){
            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                if(datas.child(LOGIN).getValue()!=null){
                    if(datas.child(LOGIN).getValue().toString().equals(login) && !datas.getKey().equals(userId) ){
                        friend = buildFriend(datas); // create Friend
                        break;
                    }
                }
            }
        }

        return friend;
    }

    public static Boolean doesUserIdExists(String userId, DataSnapshot dataSnapshot){

        Boolean answer = false;

        if(dataSnapshot.getChildren()!=null){
            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                if(datas.getKey()!=null){
                    if(datas.getKey().equals(userId)){
                        answer = true; // userId has been found
                        break;
                    }
                }
            }
        }

        return answer;
    }

    public static Boolean isLoginOK(String userId, DataSnapshot dataSnapshot){

        Boolean answer = false;

        if(dataSnapshot.getChildren()!=null){
            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                if(datas.getKey()!=null){
                    if(datas.getKey().equals(userId)){

                        if(datas.child(LOGIN).getValue()!=null){
                            if(datas.child(LOGIN).getValue().toString().length()>0){
                                answer = true; // login is OK
                            }
                        }
                        break;
                    }
                }
            }
        }

        return answer;
    }

    public static String getLogin(String userId, DataSnapshot dataSnapshot){

        if(dataSnapshot.getChildren()!=null){
            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                if(datas.getKey()!=null){
                    if(datas.getKey().equals(userId)){

                        if(datas.child(LOGIN).getValue()!=null){
                            if(datas.child(LOGIN).getValue().toString().length()>0){
                                return datas.child(LOGIN).getValue().toString();
                            }
                        }
                        break;
                    }
                }
            }
        }

        return null;
    }

    // ------------------------------------------------------------------------------------------------
    // ------------------------------- UTILS FIREBASE UPDATE ------------------------------------------
    // ------------------------------------------------------------------------------------------------

    public static void setRoute(DatabaseReference databaseReference, Route route){
        databaseReference.child(NAME).setValue(route.getName());
        databaseReference.child(VALID).setValue(route.getValid());
    }

    public static void setRouteSegment(DatabaseReference databaseReference, List<RouteSegment> routeSegmentList){

        if(routeSegmentList!=null){
            if(routeSegmentList.size() > 0 ){
                DatabaseReference databaseReferenceRoute = databaseReference.child(POINTS);

                for(RouteSegment segment : routeSegmentList){
                    String number = String.valueOf(segment.getNumber());
                    databaseReferenceRoute.child(number).child(ID).setValue(segment.getId());
                    databaseReferenceRoute.child(number).child(LAT).setValue(segment.getLat());
                    databaseReferenceRoute.child(number).child(LNG).setValue(segment.getLng());
                    databaseReferenceRoute.child(number).child(ID_ROUTE).setValue(segment.getIdRoute());
                }
            }
        }
    }

    public static void setBikeEvent(DatabaseReference databaseReference, BikeEvent bikeEvent){

        databaseReference.child(DATE).setValue(bikeEvent.getDate());
        databaseReference.child(TIME).setValue(bikeEvent.getTime());
        databaseReference.child(ID_ORGANIZER).setValue(bikeEvent.getOrganizerId());
        databaseReference.child(ID_ROUTE).setValue(bikeEvent.getIdRoute());
        databaseReference.child(COMMENTS).setValue(bikeEvent.getComments());
        databaseReference.child(STATUS).setValue(bikeEvent.getStatus());
    }

    public static void setInvitation(DatabaseReference databaseReference, BikeEvent bikeEvent){

        databaseReference.child(DATE).setValue(bikeEvent.getDate());
        databaseReference.child(TIME).setValue(bikeEvent.getTime());
        databaseReference.child(ID_ORGANIZER).setValue(bikeEvent.getOrganizerId());
        databaseReference.child(COMMENTS).setValue(bikeEvent.getComments());
        databaseReference.child(STATUS).setValue(ONGOING);
    }

    public static void setEventFriends(DatabaseReference databaseReference, String guests_id, List<EventFriends> listEventFriends){

        DatabaseReference databaseReferenceEventFriends = databaseReference.child(GUESTS);

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(EventFriends eventFriends : listEventFriends){
                    if(!eventFriends.getIdFriend().equals(guests_id)){
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(ID_FRIEND).setValue(eventFriends.getIdFriend());
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(ACCEPTED).setValue(eventFriends.getAccepted());
                        databaseReferenceEventFriends.child(eventFriends.getIdFriend()).child(LOGIN).setValue(eventFriends.getLogin());
                    }
                }
            }
        }
    }
}
