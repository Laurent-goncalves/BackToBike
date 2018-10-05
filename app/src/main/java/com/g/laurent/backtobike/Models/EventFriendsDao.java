package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface EventFriendsDao {

    @Query("SELECT * FROM EventFriends WHERE id = :id")
    Cursor getEventFriends(long id);

    @Insert
    long insertEventFriends(EventFriends eventFriends);

    @Update
    int updateEventFriends(EventFriends eventFriends);

    @Query("DELETE FROM EventFriends WHERE id = :id")
    int deleteEventFriends(long id);
}
