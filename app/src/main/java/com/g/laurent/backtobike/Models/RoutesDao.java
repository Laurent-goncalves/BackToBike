package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;


@Dao
public interface RoutesDao {

    @Query("SELECT * FROM Route WHERE id = :id")
    Cursor getRoute(long id);

    @Query("SELECT * FROM Route")
    Cursor getAllRoutes();

    @Insert
    long insertRoute(Route route);

    @Update
    int updateRoute(Route route);

    @Query("DELETE FROM Route WHERE id = :id")
    int deleteRoute(long id);
}
