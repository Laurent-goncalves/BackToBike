package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.database.Cursor;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.FriendContentProvider;
import java.util.List;


public class FriendsHandler {

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void insertNewFriend(Context context, Friend friend){

        // Insert friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriend(friend));
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateFriend(Context context, Friend friend){

        // Update friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        friendContentProvider.update(null, Friend.createContentValuesFromFriend(friend),null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DELETE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void deleteFriend(Context context, String idFriend){

        // Delete friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);
        friendContentProvider.setIdFriend(idFriend);

        friendContentProvider.delete(null,null,null);
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static List<Friend> getListFriends(Context context){

        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        final Cursor cursor = friendContentProvider.query(null, null, null, null, null);

        return Friend.getListFriendsFromCursor(cursor);
    }

    public static Friend getFriend(Context context, String idFriend){

        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);
        friendContentProvider.setIdFriend(idFriend);

        final Cursor cursor = friendContentProvider.query(null, null, null, null, null);

        return Friend.getFriendFromCursor(cursor);
    }
}
