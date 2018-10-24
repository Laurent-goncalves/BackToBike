package com.g.laurent.backtobike.Models;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class FriendContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.g.laurent.backtobike.Models";
    public static final String TABLE_NAME = Friend.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private Context context;
    private String idFriend;
    private String userId;

    public void setUtils(Context context, String idFriend, String userId){
        this.context=context;
        this.idFriend=idFriend;
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
            if(idFriend==null)
                return AppDatabase.getInstance(context, userId).friendsDao().getAllFriends();
            else // get single friend
                return AppDatabase.getInstance(context, userId).friendsDao().getFriend(idFriend);
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
            AppDatabase.getInstance(context, userId).friendsDao().insertFriend(Friend.fromContentValues(values));
        } else
            throw new IllegalArgumentException("Failed to insert row into " + uri);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null){
            return AppDatabase.getInstance(context, userId).friendsDao().deleteFriend(idFriend);
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (context!= null && values!=null){
            return AppDatabase.getInstance(context, userId).friendsDao().updateFriend(Friend.fromContentValues(values));
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
