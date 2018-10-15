package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


@Entity
public class BikeEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String organizerId;
    private String date;
    private String time;
    private int idRoute;
    private String comments;
    private String status;
    private transient List<EventFriends> listEventFriends;
    private transient Route route;

    public BikeEvent(int id, String organizerId, String date, String time, int idRoute, String comments, String status) {
        this.id = id;
        this.organizerId = organizerId;
        this.date = date;
        this.time = time;
        this.idRoute = idRoute;
        this.comments = comments;
        this.status = status;
    }

    @Ignore
    public BikeEvent(int id, String organizerId, String date, String time, int idRoute, String comments, String status, List<EventFriends> listEventFriends) {
        this.id = id;
        this.organizerId = organizerId;
        this.date = date;
        this.time = time;
        this.idRoute = idRoute;
        this.comments = comments;
        this.status = status;
        this.listEventFriends=listEventFriends;
    }

    @Ignore
    public BikeEvent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EventFriends> getListEventFriends() {
        return listEventFriends;
    }

    public void setListEventFriends(List<EventFriends> listEventFriends) {
        this.listEventFriends = listEventFriends;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    // --- UTILS ---
    public static BikeEvent fromContentValues(ContentValues values) {

        final BikeEvent bikeEvent = new BikeEvent();

        if (values.containsKey("id")) bikeEvent.setId(values.getAsInteger("id"));
        if (values.containsKey("organizerId")) bikeEvent.setOrganizerId(values.getAsString("id"));
        if (values.containsKey("date")) bikeEvent.setDate(values.getAsString("date"));
        if (values.containsKey("time")) bikeEvent.setTime(values.getAsString("time"));
        if (values.containsKey("idRoute")) bikeEvent.setIdRoute(values.getAsInteger("idRoute"));
        if (values.containsKey("comments")) bikeEvent.setComments(values.getAsString("comments"));
        if (values.containsKey("status")) bikeEvent.setStatus(values.getAsString("status"));

        return bikeEvent;
    }

    public static ContentValues createContentValuesFromBikeEventInsert(BikeEvent bikeEvent) {

        final ContentValues values = new ContentValues();

        values.put("organizerId",bikeEvent.getOrganizerId());
        values.put("date",bikeEvent.getDate());
        values.put("time",bikeEvent.getTime());
        values.put("idRoute",bikeEvent.getIdRoute());
        values.put("comments",bikeEvent.getComments());
        values.put("status",bikeEvent.getStatus());

        return values;
    }

    public static ContentValues createContentValuesFromBikeEventUpdate(BikeEvent bikeEvent) {

        final ContentValues values = new ContentValues();

        values.put("id",bikeEvent.getId());
        values.put("organizerId",bikeEvent.getOrganizerId());
        values.put("date",bikeEvent.getDate());
        values.put("time",bikeEvent.getTime());
        values.put("idRoute",bikeEvent.getIdRoute());
        values.put("comments",bikeEvent.getComments());
        values.put("status",bikeEvent.getStatus());

        return values;
    }

    public static BikeEvent getBikeEventFromCursor(Cursor cursor){

        final BikeEvent bikeEvent = new BikeEvent();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                bikeEvent.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                bikeEvent.setOrganizerId(cursor.getString(cursor.getColumnIndexOrThrow("organizerId")));
                bikeEvent.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                bikeEvent.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                bikeEvent.setIdRoute(cursor.getInt(cursor.getColumnIndexOrThrow("idRoute")));
                bikeEvent.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
                bikeEvent.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            }
            cursor.close();
        }

        return bikeEvent;
    }

    public static List<BikeEvent> getListBikeEventsFromCursor(Cursor cursor){

        List<BikeEvent> listEvents = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {

                BikeEvent bikeEvent = new BikeEvent();

                bikeEvent.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                bikeEvent.setOrganizerId(cursor.getString(cursor.getColumnIndexOrThrow("organizerId")));
                bikeEvent.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                bikeEvent.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                bikeEvent.setIdRoute(cursor.getInt(cursor.getColumnIndexOrThrow("idRoute")));
                bikeEvent.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
                bikeEvent.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                listEvents.add(bikeEvent);
            }
            cursor.close();
        }

        return listEvents;
    }

}
