package com.g.laurent.backtobike.Models;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class EventFriendsContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.g.laurent.backtobike.Models";
    public static final String TABLE_NAME = EventFriends.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private Context context;

    public void setUtils(Context context){
        this.context=context;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (context != null){
            long idEvent = ContentUris.parseId(uri);
            return AppDatabase.getInstance(context).eventFriendsDao().getEventFriends(idEvent);
        }
        throw new IllegalArgumentException("Failed to query row for uri " +  uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (context != null && values !=null){
            final long id = AppDatabase.getInstance(context).eventFriendsDao().insertEventFriends(EventFriends.fromContentValues(values));
            if (id != 0){
                return ContentUris.withAppendedId(uri, id);
            }
        }
        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null){
            long idEvent = ContentUris.parseId(uri);
            return AppDatabase.getInstance(context).eventFriendsDao().deleteEventFriends(idEvent);
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null && values!=null){
            return AppDatabase.getInstance(context).eventFriendsDao().updateEventFriends(EventFriends.fromContentValues(values));
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
