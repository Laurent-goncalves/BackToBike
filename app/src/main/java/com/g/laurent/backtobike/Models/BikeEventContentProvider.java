package com.g.laurent.backtobike.Models;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BikeEventContentProvider extends ContentProvider {

    private static final String ACCEPTED = "accepted";
    private static final String ONGOING ="ongoing";
    private static final String COMPLETED = "completed";
    private static final String ALL_EVENTS ="all_events";
    private static final String TYPE_MY_EVENTS ="type_my_events";
    private static final String TYPE_MY_INVITS ="type_my_invits";
    private static final String TYPE_SINGLE_EVENT ="type_single_event";
    public static final String AUTHORITY = "com.g.laurent.backtobike.Models";
    public static final String TABLE_NAME = BikeEvent.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private String userId;
    private Context context;
    private String typeEvent;
    private String idBikeEvent;

    public void setUtils(Context context, String typeEvent, String idBikeEvent, String userId){
        this.context=context;
        this.typeEvent=typeEvent;
        this.idBikeEvent=idBikeEvent;
        this.userId=userId;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (context != null){

            switch(typeEvent){
                case ALL_EVENTS:
                    return AppDatabase.getInstance(context, userId).bikeEventDao().getAllBikeEvents();
                case TYPE_MY_EVENTS:
                    return AppDatabase.getInstance(context, userId).bikeEventDao().getMyBikeEvents(COMPLETED);
                case TYPE_MY_INVITS:
                    return AppDatabase.getInstance(context, userId).bikeEventDao().getMyInvitations(userId, ONGOING);
                case TYPE_SINGLE_EVENT:
                    return AppDatabase.getInstance(context, userId).bikeEventDao().getBikeEvent(idBikeEvent);
            }
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
            AppDatabase.getInstance(context, userId).bikeEventDao().insertBikeEvent(BikeEvent.fromContentValues(values));
        } else
            throw new IllegalArgumentException("Failed to insert row into " + uri);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null){
            return AppDatabase.getInstance(context, userId).bikeEventDao().deleteBikeEvent(idBikeEvent);
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null && values!=null){
            return AppDatabase.getInstance(context, userId).bikeEventDao().updateBikeEvent(BikeEvent.fromContentValues(values));
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
