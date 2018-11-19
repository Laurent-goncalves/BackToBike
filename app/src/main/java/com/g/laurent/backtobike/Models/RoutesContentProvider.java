package com.g.laurent.backtobike.Models;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.EVENT_ROUTE_TYPE;
import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.MY_ROUTE_TYPE;

public class RoutesContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.g.laurent.backtobike.Models";
    public static final String TABLE_NAME = Route.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private Context context;
    private String userId;
    private String routeType;
    private String idEvent;

    public void setUtils(Context context, String userId, String idEvent, String routeType){
        this.context=context;
        this.userId=userId;
        this.idEvent=idEvent;
        this.routeType = routeType;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (context != null){

            if(routeType.equals(MY_ROUTE_TYPE)){
                long idRoute = ContentUris.parseId(uri);
                return AppDatabase.getInstance(context, userId).routesDao().getRoute(idRoute);
            } else if(routeType.equals(EVENT_ROUTE_TYPE)){
                return AppDatabase.getInstance(context, userId).routesDao().getRouteEvent(idEvent);
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
            final long id = AppDatabase.getInstance(context, userId).routesDao().insertRoute(Route.fromContentValues(values));
            if (id != 0){
                return ContentUris.withAppendedId(uri, id);
            }
        }
        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null){
            long idRoute = ContentUris.parseId(uri);
            return AppDatabase.getInstance(context, userId).routesDao().deleteRoute(idRoute);
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null && values!=null){
            return AppDatabase.getInstance(context, userId).routesDao().updateRoute(Route.fromContentValues(values));
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
