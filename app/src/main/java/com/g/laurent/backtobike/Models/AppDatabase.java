package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Route.class, RouteSegment.class, Friend.class, BikeEvent.class, EventFriends.class}, version = 21, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public abstract RoutesDao routesDao();
    public abstract FriendsDao friendsDao();
    public abstract BikeEventDao bikeEventDao();
    public abstract RouteSegmentDao routeSegmentDao();
    public abstract EventFriendsDao eventFriendsDao();

    // Create a single instance of property database
    public static AppDatabase getInstance(Context context, String userId) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "MyDatabase" + userId + ".db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
