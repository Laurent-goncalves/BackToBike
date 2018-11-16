package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.database.Cursor;
import com.g.laurent.backtobike.Models.EventFriendsContentProvider;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.FriendContentProvider;
import java.util.List;

public class FriendsHandler {

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void insertNewFriend(Context context, Friend friend, String userId){

        // Insert friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context, null, userId);

        friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriend(friend));
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateFriend(Context context, Friend friend, String userId){

        // Update friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context, null, userId);

        friendContentProvider.update(null, Friend.createContentValuesFromFriend(friend),null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DELETE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void deleteFriend(Context context, String idFriend, String userId){

        // Delete event friends
        EventFriendsContentProvider eventFriendsContentProvider = new EventFriendsContentProvider();
        eventFriendsContentProvider.setUtils(context, idFriend, null,userId);

        // Delete friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context,idFriend, userId);

        friendContentProvider.delete(null,null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static List<Friend> getListFriends(Context context, String userId){

        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context, null, userId);

        final Cursor cursor = friendContentProvider.query(null, null, null, null, null);

        return Friend.getListFriendsFromCursor(cursor);
    }

    public static Friend getFriend(Context context, String idFriend, String userId){

        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context, idFriend, userId);

        final Cursor cursor = friendContentProvider.query(null, null, null, null, null);

        return Friend.getFriendFromCursor(cursor);
    }
}
