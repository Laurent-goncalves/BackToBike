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
    private String idEvent;
    private String idFriend;
    private String accepted;

    public EventFriends(int id, String idEvent, String idFriend, String accepted) {
        this.id = id;
        this.idEvent = idEvent;
        this.idFriend = idFriend;
        this.accepted = accepted;
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

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(String idFriend) {
        this.idFriend = idFriend;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    // --- UTILS ---
    public static EventFriends fromContentValues(ContentValues values) {

        final EventFriends eventFriends = new EventFriends();

        if (values.containsKey("id")) eventFriends.setId(values.getAsInteger("id"));
        if (values.containsKey("idEvent")) eventFriends.setIdEvent(values.getAsString("idEvent"));
        if (values.containsKey("idFriend")) eventFriends.setIdFriend(values.getAsString("idFriend"));
        if (values.containsKey("accepted")) eventFriends.setAccepted(values.getAsString("accepted"));

        return eventFriends;
    }

    public static ContentValues createContentValuesFromEventFriendsInsert(EventFriends eventFriends) {

        final ContentValues values = new ContentValues();

        values.put("id",eventFriends.getId());
        values.put("idEvent",eventFriends.getIdEvent());
        values.put("idFriend",eventFriends.getIdFriend());
        values.put("accepted",eventFriends.getAccepted());

        return values;
    }

    public static ContentValues createContentValuesFromEventFriendsUpdate(EventFriends eventFriends) {

        final ContentValues values = new ContentValues();

        values.put("id",eventFriends.getId());
        values.put("idEvent",eventFriends.getIdEvent());
        values.put("idFriend",eventFriends.getIdFriend());
        values.put("accepted",eventFriends.getAccepted());

        return values;
    }

    public static List<EventFriends> getEventFriendsFromCursor(Cursor cursor){

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {

                EventFriends eventFriends = new EventFriends();

                eventFriends.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                eventFriends.setIdEvent(cursor.getString(cursor.getColumnIndexOrThrow("idEvent")));
                eventFriends.setIdFriend(cursor.getString(cursor.getColumnIndexOrThrow("idFriend")));
                eventFriends.setAccepted(cursor.getString(cursor.getColumnIndexOrThrow("accepted")));

                listEventFriends.add(eventFriends);
            }
            cursor.close();
        }

        return listEventFriends;
    }
}
