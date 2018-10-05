package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Route.class, RouteSegment.class, Friend.class, BikeEvent.class, EventFriends.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public abstract RoutesDao routesDao();
    public abstract FriendsDao friendsDao();
    public abstract BikeEventDao bikeEventDao();
    public abstract RouteSegmentDao routeSegmentDao();
    public abstract EventFriendsDao eventFriendsDao();

    // Create a single instance of property database
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "MyDatabase.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Prepopulate the database
   /* private static RoomDatabase.Callback prepopulateDatabase(){
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                ContentValues contentValues = new ContentValues();


                db.insert("Route", OnConflictStrategy.IGNORE, contentValues);

            }
        };
    }*/
}
