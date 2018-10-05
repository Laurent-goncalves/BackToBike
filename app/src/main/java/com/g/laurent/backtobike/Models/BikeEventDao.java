package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface BikeEventDao {

    @Query("SELECT * FROM BikeEvent WHERE id = :id")
    Cursor getBikeEvent(long id);

    @Insert
    long insertBikeEvent(BikeEvent bikeEvent);

    @Update
    int updateBikeEvent(BikeEvent bikeEvent);

    @Query("DELETE FROM BikeEvent WHERE id = :id")
    int deleteBikeEvent(long id);

}
