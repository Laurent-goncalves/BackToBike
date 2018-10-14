package com.g.laurent.backtobike.Utils;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;

import java.util.ArrayList;
import java.util.List;

public class UtilsApp {


    public static int getIdEventFriend(String idFriend, BikeEvent bikeEvent){
        int idEventFriend = -1;

        if(bikeEvent!=null){
            if(bikeEvent.getListEventFriends()!=null){
                if(bikeEvent.getListEventFriends().size()>0){
                    for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                        if(eventFriends.getIdFriend().equals(idFriend)) {
                            idEventFriend = eventFriends.getId();
                            break;
                        }
                    }
                }
            }
        }

        return idEventFriend;
    }

    public static String getIdInvitation(BikeEvent bikeEvent){
        String idInvitation = bikeEvent.getOrganizerId() + "_" + bikeEvent.getDate() + "_" + bikeEvent.getTime();
        idInvitation = idInvitation.replace("/","_");
        return idInvitation;
    }

    public static String createStringDate(int year, int month, int dayOfMonth){

        String Day;
        int Month = month + 1;
        String new_month;

        if(dayOfMonth<10)
            Day = "0" + dayOfMonth;
        else
            Day = String.valueOf(dayOfMonth);

        if(Month<10)
            new_month = "0" + Month;
        else
            new_month = String.valueOf(Month);

        return Day + "/" + new_month + "/" + year;
    }

    public static String createStringTime(int hourOfDay, int minute){
        if(minute<10)
            return hourOfDay + ":0" + minute;
        else
            return hourOfDay + ":" + minute;
    }

    public static int findFriendIndexInListFriends(Friend friend, List<Friend> listFriends){

        int index = -1;

        if(listFriends!=null){
            if(listFriends.size()>0){
                for(int i = 0; i < listFriends.size(); i++){
                    if(listFriends.get(i).getId().equals(friend.getId())){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListEventFriends(Friend friend, List<EventFriends> listEventFriends){

        int index = -1;

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(int i = 0; i < listEventFriends.size(); i++){
                    if(listEventFriends.get(i).getIdFriend().equals(friend.getId())){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListIds(String idFriend, ArrayList<String> listIds){

        int index = -1;

        if(listIds!=null){
            if(listIds.size()>0){
                for(int i = 0; i < listIds.size(); i++){
                    if(listIds.get(i).equals(idFriend)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findIndexRouteInList(int idRoute, List<Route> listRoutes){

        int index = -1;

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(int i = 0; i < listRoutes.size()-1 ; i++){
                    if(listRoutes.get(i).getId()==idRoute){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static List<String> transformListRouteIntoListRouteNames(List<Route> listRoutes){

        List<String> listRoutesNames = new ArrayList<>();

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(Route route : listRoutes){
                    if(route.getName()==null)
                        listRoutesNames.add("");
                    else
                        listRoutesNames.add(route.getName());
                }
            }
        }

        return listRoutesNames;
    }
}
