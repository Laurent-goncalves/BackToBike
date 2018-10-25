package com.g.laurent.backtobike.Utils;

import android.content.Context;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.R;
import java.util.ArrayList;
import java.util.List;



public class UtilsBikeEvent {

    private static final String CANCELLED = "cancelled";
    private static final String ACCEPTED = "accepted";

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
}
