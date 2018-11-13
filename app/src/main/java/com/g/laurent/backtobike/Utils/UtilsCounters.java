package com.g.laurent.backtobike.Utils;

import android.content.Context;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UtilsCounters {

    private static final String NAME = "name";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String MY_ROUTES = "my_routes";
    private static final String GUESTS = "guests";
    private static final String ROUTE = "route";
    private static final String STATUS = "status";
    private static final String ONGOING = "ongoing";
    private static final String VALID = "valid";
    private static final String HAS_ACCEPTED = "has_accepted";
    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String PHOTO_URL = "photoUrl";
    private static final String LOGIN = "login";
    private static final String ID_ROUTE = "id_route";
    private static final String ID_EVENT = "id_event";
    private static final String ID_FRIEND = "id_friend";
    private static final String ID_ORGANIZER = "id_organizer";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String COMMENTS = "comments";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";


    public static List<String> getListDifferencesForFriendRequests(Context context, DataSnapshot datas, List<Friend> oldListFriends){

        List<String> listDifferences = new ArrayList<>();

        // Check for reception of new friend requests or answer pending
        for (DataSnapshot data : datas.getChildren()) {
            if(data.child(ACCEPTED).getValue().equals(ONGOING)){

                if(UtilsApp.findFriendIndexInListFriends(data.getKey(), oldListFriends) == -1){
                    // NEW friend request
                    String difference = context.getResources().getString(R.string.friend_request_reception) +
                            " " + data.child(NAME).getValue() + ".";
                    listDifferences.add(difference);
                } else {
                    // Friend waiting for answer
                    String difference = context.getResources().getString(R.string.friend_request_ongoing) +
                            " " + data.child(NAME).getValue() + ".";
                    listDifferences.add(difference);
                }
            }
        }

        // Check if a friend accepted or rejected friend request
        for (DataSnapshot data : datas.getChildren()) {
            if(data.hasChild(HAS_ACCEPTED)){

                int index = UtilsApp.findFriendIndexInListFriends(data.getKey(), oldListFriends);

                if(index != -1){

                    if(data.child(HAS_ACCEPTED).getValue()!=null){
                        if(data.child(HAS_ACCEPTED).getValue().equals(ACCEPTED) &&
                                oldListFriends.get(index).getHasAgreed().equals(ONGOING)){

                            String difference =data.child(NAME).getValue() + " " + context.getResources().getString(R.string.friend_request_accepted) + ".";

                            listDifferences.add(difference);

                        } else if(data.child(HAS_ACCEPTED).getValue().equals(REJECTED) &&
                                oldListFriends.get(index).getHasAgreed().equals(ONGOING)){

                            String difference =data.child(NAME).getValue() + " " + context.getResources().getString(R.string.friend_request_refused) + ".";

                            listDifferences.add(difference);
                        }
                    }
                }
            }
        }

        return listDifferences;
    }

    public static List<Difference> getListDifferencesBetweenListEvents(List<BikeEvent> oldList, DataSnapshot datas, Context context){

        // Get list of events on Firebase
        List<BikeEvent> newListBikeEvent = new ArrayList<>();

        if(datas.getChildrenCount()>0) {
            for (DataSnapshot data : datas.getChildren())
                newListBikeEvent.add(FirebaseRecover.buildBikeEvent(data));
        }

        List<Difference> listDiff = new ArrayList<>();

        if(newListBikeEvent!=null && oldList !=null){
            if(newListBikeEvent.size()>0 && oldList.size()>0){
                for(int i = 0; i < newListBikeEvent.size(); i++){

                    int index = UtilsApp.findIndexEventInList(newListBikeEvent.get(i).getId(), oldList);

                    if(index!=-1){
                        listDiff.addAll(getListDifferencesBetweenEvents(oldList.get(index), newListBikeEvent.get(i), context));
                    }
                }
            }
        }

        return listDiff;
    }

    public static List<Difference> getListDifferencesBetweenListEvents(List<BikeEvent> oldList, List<BikeEvent> newList, Context context){

        List<Difference> listDiff = new ArrayList<>();

        if(newList!=null && oldList !=null){
            if(newList.size()>0 && oldList.size()>0){
                for(int i = 0; i < newList.size(); i++){

                    int index = UtilsApp.findIndexEventInList(newList.get(i).getId(), oldList);

                    if(index!=-1){
                        listDiff.addAll(getListDifferencesBetweenEvents(oldList.get(index), newList.get(i), context));
                    }
                }
            }
        }

        return listDiff;
    }

    public static List<Difference> getListDifferencesBetweenEvents(BikeEvent oldEvent, BikeEvent newEvent, Context context){

        List<Difference> listDiff = new ArrayList<>();

        if(oldEvent!=null && newEvent !=null){

            // Compare status
            if(!newEvent.getStatus().equals(oldEvent.getStatus())){
                if(newEvent.getStatus().equals(CANCELLED))
                    listDiff.add(new Difference(context.getResources().getString(R.string.bike_event_cancelled),newEvent.getId()));
            }

            // Compare friends acceptance
            if(newEvent.getListEventFriends()!=null) {
                if (newEvent.getListEventFriends().size() > 0) {

                    int countAccept = 0;
                    int countReject = 0;
                    String idEvent = newEvent.getId();

                    for (int j = 0; j < newEvent.getListEventFriends().size(); j++) {

                        // find friend in list
                        int indexFriend = UtilsApp.findFriendIndexInListEventFriends(newEvent.getListEventFriends().get(j).getIdFriend(),
                                oldEvent.getListEventFriends());

                        // Compare acceptance
                        if (indexFriend != -1) {

                            if (!newEvent.getListEventFriends().get(j).getAccepted().equals(oldEvent.getListEventFriends().get(indexFriend).getAccepted())) {
                                if (newEvent.getListEventFriends().get(j).getAccepted().equals(ACCEPTED))
                                    countAccept++;
                                else
                                    countReject++;
                            }
                        }
                    }

                    // Update list of differences
                    if (countAccept == 1) {
                        listDiff.add(new Difference(countAccept + " " + context.getResources().getString(R.string.friend_accepted), idEvent));
                    } else if (countAccept > 1)
                        listDiff.add(new Difference(countAccept + " " + context.getResources().getString(R.string.friends_accepted), idEvent));

                    if (countReject == 1) {
                        listDiff.add(new Difference(countReject + " " + context.getResources().getString(R.string.friend_refused), idEvent));
                    } else if (countReject > 1)
                        listDiff.add(new Difference(countReject + " " + context.getResources().getString(R.string.friends_refused), idEvent));
                }
            }
        }

        return listDiff;
    }

    public static List<Difference> getListDifferencesFromBikeEvent(String idEvent, List<Difference> listDifferences){

        List<Difference> listDiff = new ArrayList<>();

        if(listDifferences!=null){
            if(listDifferences.size()>0){
                for(Difference diff : listDifferences){
                    if(diff.getIdEvent().equals(idEvent)){
                        listDiff.add(diff);
                    }
                }
            }
        }

        return listDiff;
    }

    public static List<String> getListDifferencesForInvitations(Context context, DataSnapshot datas, List<BikeEvent> oldListInvitations){

        List<String> listDifferences = new ArrayList<>();
        int countNewInvitation = 0;
        int countInvitationsOngoing = 0;
        int countInvitationsCancelled = 0;

        for (DataSnapshot data : datas.getChildren()) {

            int index = UtilsApp.findIndexEventInList(data.getKey(), oldListInvitations);

            if(index==-1){ // NEW INVITATION

                    countNewInvitation++;
            } else { // INVITATION PENDING
                if(data.child(STATUS).getValue()!=null){
                    if(data.child(STATUS).getValue().equals(CANCELLED))
                        countInvitationsCancelled++;
                    else
                        countInvitationsOngoing++;
                }
            }
        }

        if(countNewInvitation>0){

            String text = context.getResources().getString(R.string.new_invitation) + " " + countNewInvitation + " ";

            if(countNewInvitation==1)
                listDifferences.add(text + " " + context.getResources().getString(R.string.new_invitation2));
            else
                listDifferences.add(text + " " + context.getResources().getString(R.string.new_invitations));
        }

        if(countInvitationsOngoing>0){

            if(countInvitationsOngoing==1)
                listDifferences.add(countInvitationsOngoing + " " + context.getResources().getString(R.string.invitation_pending));
            else
                listDifferences.add(countInvitationsOngoing + " " + context.getResources().getString(R.string.invitations_pending));
        }

        if(countInvitationsCancelled>0){

            if(countInvitationsCancelled==1)
                listDifferences.add(countInvitationsCancelled + " " + context.getResources().getString(R.string.invitation_cancelled));
            else
                listDifferences.add(countInvitationsCancelled + " " + context.getResources().getString(R.string.invitations_cancelled));
        }

        return listDifferences;
    }

    private static Route buildListRoute(DataSnapshot dataSnapshot){

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

    private static BikeEvent buildBikeEvent(DataSnapshot datas){

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

    private static List<EventFriends> buildListEventFriends(DataSnapshot guests, String idEvent) {

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

    public static String transformListDifferencesToString(List<Difference> listDifferences, List<String> listStringDifferences){

        StringBuilder text = new StringBuilder();

        if(listDifferences!=null){
            if(listDifferences.size()>0){
                for(Difference diff : listDifferences){
                    text.append(diff.getDifference());
                    text.append("\n");
                }
            }
        }

        if(listStringDifferences!=null){
            if(listStringDifferences.size()>0){
                for(String diff : listStringDifferences){
                    text.append(diff);
                    text.append("\n");
                }
            }
        }

        return text.toString();
    }
}
