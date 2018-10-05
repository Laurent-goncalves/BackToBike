package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;

@Entity
public class BikeEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String time;
    private int idRoute;
    private String comments;
    private String status;

    public BikeEvent(int id, String date, String time, int idRoute, String comments, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.idRoute = idRoute;
        this.comments = comments;
        this.status = status;
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

    // --- UTILS ---
    public static BikeEvent fromContentValues(ContentValues values) {

        final BikeEvent bikeEvent = new BikeEvent();

        if (values.containsKey("id")) bikeEvent.setId(values.getAsInteger("id"));
        if (values.containsKey("date")) bikeEvent.setDate(values.getAsString("date"));
        if (values.containsKey("time")) bikeEvent.setTime(values.getAsString("time"));
        if (values.containsKey("idRoute")) bikeEvent.setIdRoute(values.getAsInteger("idRoute"));
        if (values.containsKey("comments")) bikeEvent.setComments(values.getAsString("comments"));
        if (values.containsKey("status")) bikeEvent.setStatus(values.getAsString("status"));

        return bikeEvent;
    }

    public static ContentValues createContentValuesFromBikeEventInsert(BikeEvent bikeEvent) {

        final ContentValues values = new ContentValues();

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
            bikeEvent.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            bikeEvent.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            bikeEvent.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
            bikeEvent.setIdRoute(cursor.getInt(cursor.getColumnIndexOrThrow("idRoute")));
            bikeEvent.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
            bikeEvent.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        }

        return bikeEvent;
    }

}
