package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface EventFriendsDao {

    @Query("SELECT * FROM EventFriends")
    Cursor getAllEventFriends();

    @Query("SELECT * FROM EventFriends WHERE idEvent = :idEvent")
    Cursor getEventFriends(String idEvent);

    @Insert
    long insertEventFriends(EventFriends eventFriends);

    @Update
    int updateEventFriends(EventFriends eventFriends);

    @Query("DELETE FROM EventFriends WHERE idEvent = :idEvent")
    int deleteEventFriends(String idEvent);

    @Query("DELETE FROM EventFriends WHERE idFriend = :idFriend")
    int deleteFriends(String idFriend);

    @Query("DELETE FROM EventFriends")
    int deleteAllEventFriends();
}
