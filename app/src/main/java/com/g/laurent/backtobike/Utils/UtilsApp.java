package com.g.laurent.backtobike.Utils;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;

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
        return hourOfDay + ":" + minute;
    }

}
