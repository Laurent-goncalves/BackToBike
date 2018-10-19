package com.g.laurent.backtobike.Utils;

import android.content.Context;

import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import java.util.List;


public class SynchronizeWithFirebase {


    public static void synchronizeFriends(String userId, Context context, CallbackSynchronizeEnd callbackSynchronizeEnd){

        // Recover friends on database
        List<Friend> listFriendApp = FriendsHandler.getListFriends(context, userId);

        // Recover friends from user on Firebase
        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        firebaseRecover.recoverFriendsUser(userId, new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {}

            @Override
            public void onSuccess(List<Friend> listFriendFirebase) {

                if(listFriendFirebase!=null){
                    if(listFriendFirebase.size()>0){
                        for(Friend friend : listFriendFirebase){

                            if(UtilsApp.findFriendIndexInListFriends(friend, listFriendApp) != -1) // if friend in database
                                FriendsHandler.updateFriend(context, friend, userId); // update friend
                            else
                                FriendsHandler.insertNewFriend(context, friend, userId); // add new friend in database

                            callbackSynchronizeEnd.onCompleted();
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }


}
