package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface BikeEventDao {

    @Query("SELECT * FROM BikeEvent")
    Cursor getAllBikeEvents();

    @Query("SELECT * FROM BikeEvent WHERE id = :id")
    Cursor getBikeEvent(long id);

    @Query("SELECT * FROM BikeEvent WHERE organizerId = :organizerId AND status = :status")
    Cursor getMyBikeEvents(String organizerId, String status);

    @Query("SELECT * FROM BikeEvent WHERE organizerId != :user_id  AND status = :status")
    Cursor getMyInvitiations(String user_id, String status);

    @Insert
    long insertBikeEvent(BikeEvent bikeEvent);

    @Update
    int updateBikeEvent(BikeEvent bikeEvent);

    @Query("DELETE FROM BikeEvent WHERE id = :id")
    int deleteBikeEvent(long id);

}
