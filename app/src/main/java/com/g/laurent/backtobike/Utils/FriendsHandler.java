package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.database.Cursor;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.FriendContentProvider;


public class FriendsHandler {

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void insertNewFriend(Context context, String idFriend, String name, String photoUrl){

        Friend friend = new Friend(idFriend, name, photoUrl);

        // Insert friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        friendContentProvider.insert(FriendContentProvider.URI_ITEM, Friend.createContentValuesFromFriend(friend));
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateFriend(Context context, String idFriend, String name, String photoUrl){

        Friend friend = new Friend(idFriend, name, photoUrl);

        // Update friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        String[] selectionArgs = new String[0];
        selectionArgs[0] = idFriend;
        friendContentProvider.update(null, Friend.createContentValuesFromFriend(friend),null,selectionArgs);
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DELETE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void deleteFriend(Context context, String idFriend){

        // Delete friend in database
        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        String[] selectionArgs = new String[0];
        selectionArgs[0] = idFriend;
        friendContentProvider.delete(null,null,selectionArgs);
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static Friend getFriend(Context context, String idFriend){

        FriendContentProvider friendContentProvider = new FriendContentProvider();
        friendContentProvider.setUtils(context);

        String[] selectionArgs = new String[0];
        selectionArgs[0] = idFriend;
        final Cursor cursor = friendContentProvider.query(null, null, null, selectionArgs, null);

        return Friend.getRouteFromCursor(cursor);
    }

}
