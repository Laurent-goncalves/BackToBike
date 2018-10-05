package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;


@Entity
public class Friend {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String photoUrl;

    public Friend(int id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    @Ignore
    public Friend() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    // --- UTILS ---
    public static Friend fromContentValues(ContentValues values) {

        final Friend friend = new Friend();

        if (values.containsKey("id")) friend.setId(values.getAsInteger("id"));
        if (values.containsKey("name")) friend.setName(values.getAsString("name"));
        if (values.containsKey("photoUrl")) friend.setPhotoUrl(values.getAsString("photoUrl"));

        return friend;
    }

    public static ContentValues createContentValuesFromFriendInsert(Friend friend) {

        final ContentValues values = new ContentValues();

        values.put("name",friend.getName());
        values.put("photoUrl",friend.getPhotoUrl());

        return values;
    }

    public static ContentValues createContentValuesFromFriendUpdate(Friend friend) {

        final ContentValues values = new ContentValues();

        values.put("id",friend.getId());
        values.put("name",friend.getName());
        values.put("photoUrl",friend.getPhotoUrl());

        return values;
    }

    public static Friend getRouteFromCursor(Cursor cursor){

        final Friend friend = new Friend();

        if(cursor!=null){
            friend.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            friend.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            friend.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("photoUrl")));
        }

        return friend;
    }

}
