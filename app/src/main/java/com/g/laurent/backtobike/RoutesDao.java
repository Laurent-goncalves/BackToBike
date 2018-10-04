package com.g.laurent.backtobike;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;


@Dao
public interface RoutesDao {

    @Query("SELECT * FROM Route WHERE id = :id")
    Cursor getRoute(long id);

    @Insert
    long insertRoute(Route route);

    @Update
    int updateRoute(Route route);

    @Query("DELETE FROM Route WHERE id = :id")
    int deletRoute(long id);

}
