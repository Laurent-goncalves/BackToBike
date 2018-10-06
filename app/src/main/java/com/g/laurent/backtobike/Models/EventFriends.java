package com.g.laurent.backtobike.Models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


@Entity(foreignKeys = {@ForeignKey(entity = BikeEvent.class,parentColumns = "id",childColumns = "idEvent"),
        @ForeignKey(entity = Friend.class,parentColumns = "id",childColumns = "idFriend")},indices = {@Index("idEvent"),@Index("idFriend")})
public class EventFriends {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idEvent;
    private int idFriend;

    public EventFriends(int id, int idEvent, int idFriend) {
        this.id = id;
        this.idEvent = idEvent;
        this.idFriend = idFriend;
    }

    @Ignore
    public EventFriends() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public int getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    // --- UTILS ---
    public static EventFriends fromContentValues(ContentValues values) {

        final EventFriends eventFriends = new EventFriends();

        if (values.containsKey("id")) eventFriends.setId(values.getAsInteger("id"));
        if (values.containsKey("idEvent")) eventFriends.setIdEvent(values.getAsInteger("idEvent"));
        if (values.containsKey("idFriend")) eventFriends.setIdFriend(values.getAsInteger("idFriend"));

        return eventFriends;
    }

    public static ContentValues createContentValuesFromEventFriendsInsert(EventFriends eventFriends) {

        final ContentValues values = new ContentValues();

        values.put("idEvent",eventFriends.getIdEvent());
        values.put("idFriend",eventFriends.getIdFriend());

        return values;
    }

    public static ContentValues createContentValuesFromEventFriendsUpdate(EventFriends eventFriends) {

        final ContentValues values = new ContentValues();

        values.put("id",eventFriends.getId());
        values.put("idEvent",eventFriends.getIdEvent());
        values.put("idFriend",eventFriends.getIdFriend());

        return values;
    }

    public static List<EventFriends> getEventFriendsFromCursor(Cursor cursor){

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {

                EventFriends eventFriends = new EventFriends();

                eventFriends.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                eventFriends.setIdEvent(cursor.getInt(cursor.getColumnIndexOrThrow("idEvent")));
                eventFriends.setIdFriend(cursor.getInt(cursor.getColumnIndexOrThrow("idFriend")));

                listEventFriends.add(eventFriends);
            }
            cursor.close();
        }

        return listEventFriends;
    }
}
