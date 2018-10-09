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
}
