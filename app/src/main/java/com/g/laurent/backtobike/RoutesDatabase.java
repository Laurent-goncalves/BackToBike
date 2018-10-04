package com.g.laurent.backtobike;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Route.class}, version = 1, exportSchema = false)
public abstract class RoutesDatabase extends RoomDatabase {

    private static volatile RoutesDatabase INSTANCE;
    public abstract RoutesDao routesDao();

    // Create a single instance of property database
    public static RoutesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RoutesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoutesDatabase.class, "MyDatabase.db")
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
