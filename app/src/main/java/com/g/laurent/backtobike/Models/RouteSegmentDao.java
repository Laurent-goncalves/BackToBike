package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface RouteSegmentDao {

    @Query("SELECT * FROM RouteSegment WHERE id = :id")
    Cursor getRouteSegment(long id);

    @Insert
    long insertRouteSegmentDao(RouteSegment routeSegment);

    @Update
    int updateRouteSegment(RouteSegment routeSegment);

    @Query("DELETE FROM RouteSegment WHERE id = :id")
    int deleteRouteSegment(long id);
}
