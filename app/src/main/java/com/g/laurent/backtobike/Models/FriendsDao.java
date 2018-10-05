package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface FriendsDao {

    @Query("SELECT * FROM Friend WHERE id = :id")
    Cursor getFriend(long id);

    @Insert
    long insertFriend(Friend friend);

    @Update
    int updateFriend(Friend friend);

    @Query("DELETE FROM Friend WHERE id = :id")
    int deleteFriend(long id);


}
