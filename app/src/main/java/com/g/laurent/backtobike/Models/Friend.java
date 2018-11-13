package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Friend {

    @NonNull
    @PrimaryKey
    private String id;
    private String login;
    private String name;
    private String photoUrl;
    private String accepted;
    private String hasAgreed;

    public Friend(@NonNull String id, String login, String name, String photoUrl, String accepted, String hasAgreed) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.photoUrl = photoUrl;
        this.accepted = accepted;
        this.hasAgreed=hasAgreed;
    }

    @Ignore
    public Friend() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getHasAgreed() {
        return hasAgreed;
    }

    public void setHasAgreed(String hasAgreed) {
        this.hasAgreed = hasAgreed;
    }

    // --- UTILS ---
    public static Friend fromContentValues(ContentValues values) {

        final Friend friend = new Friend();

        if (values.containsKey("id")) friend.setId(values.getAsString("id"));
        if (values.containsKey("login")) friend.setLogin(values.getAsString("login"));
        if (values.containsKey("accepted")) friend.setAccepted(values.getAsString("accepted"));
        if (values.containsKey("hasAgreed")) friend.setHasAgreed(values.getAsString("hasAgreed"));
        if (values.containsKey("name")) friend.setName(values.getAsString("name"));
        if (values.containsKey("photoUrl")) friend.setPhotoUrl(values.getAsString("photoUrl"));

        return friend;
    }

    public static ContentValues createContentValuesFromFriend(Friend friend) {

        final ContentValues values = new ContentValues();

        values.put("id",friend.getId());
        values.put("login",friend.getLogin());
        values.put("accepted",friend.getAccepted());
        values.put("hasAgreed",friend.getHasAgreed());
        values.put("name",friend.getName());
        values.put("photoUrl",friend.getPhotoUrl());

        return values;
    }

    public static List<Friend> getListFriendsFromCursor(Cursor cursor){

        List<Friend> listFriend = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                Friend friend = new Friend();

                friend.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                friend.setLogin(cursor.getString(cursor.getColumnIndexOrThrow("login")));
                friend.setAccepted(cursor.getString(cursor.getColumnIndexOrThrow("accepted")));
                friend.setHasAgreed(cursor.getString(cursor.getColumnIndexOrThrow("hasAgreed")));
                friend.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                friend.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("photoUrl")));

                listFriend.add(friend);
            }
            cursor.close();
        }

        return listFriend;
    }

    public static Friend getFriendFromCursor(Cursor cursor){

        Friend friend = new Friend();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                friend.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                friend.setLogin(cursor.getString(cursor.getColumnIndexOrThrow("login")));
                friend.setAccepted(cursor.getString(cursor.getColumnIndexOrThrow("accepted")));
                friend.setHasAgreed(cursor.getString(cursor.getColumnIndexOrThrow("hasAgreed")));
                friend.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                friend.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("photoUrl")));
            }
            cursor.close();
        }
        return friend;
    }
}
