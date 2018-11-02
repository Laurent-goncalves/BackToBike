package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface RouteSegmentDao {

    @Query("SELECT * FROM RouteSegment WHERE idRoute = :idRoute")
    Cursor getRouteSegment(long idRoute);

    @Query("SELECT * FROM RouteSegment")
    Cursor getAllRouteSegment();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRouteSegmentDao(RouteSegment routeSegment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateRouteSegment(RouteSegment routeSegment);

    @Query("DELETE FROM RouteSegment WHERE idRoute = :idRoute")
    int deleteRouteSegment(long idRoute);

    @Query("DELETE FROM RouteSegment")
    int deleteRouteSegment();
}
