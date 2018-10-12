package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface FriendsDao {

    @Query("SELECT * FROM Friend")
    Cursor getAllFriends();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFriend(Friend friend);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateFriend(Friend friend);

    @Query("DELETE FROM Friend WHERE id = :id")
    int deleteFriend(String id);
}
