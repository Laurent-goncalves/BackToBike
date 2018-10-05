package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;

@Entity
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Double startPointlat;
    private Double startPointlng;
    private Double endPointlat;
    private Double endPointlng;
    private Boolean valid;

    public Route(int id, String name, Double startPointlat, Double startPointlng, Double endPointlat, Double endPointlng, Boolean valid) {
        this.id = id;
        this.name = name;
        this.startPointlat = startPointlat;
        this.startPointlng = startPointlng;
        this.endPointlat = endPointlat;
        this.endPointlng = endPointlng;
        this.valid = valid;
    }

    @Ignore
    public Route() {
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

    public Double getStartPointlat() {
        return startPointlat;
    }

    public void setStartPointlat(Double startPointlat) {
        this.startPointlat = startPointlat;
    }

    public Double getStartPointlng() {
        return startPointlng;
    }

    public void setStartPointlng(Double startPointlng) {
        this.startPointlng = startPointlng;
    }

    public Double getEndPointlat() {
        return endPointlat;
    }

    public void setEndPointlat(Double endPointlat) {
        this.endPointlat = endPointlat;
    }

    public Double getEndPointlng() {
        return endPointlng;
    }

    public void setEndPointlng(Double endPointlng) {
        this.endPointlng = endPointlng;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    // --- UTILS ---
    public static Route fromContentValues(ContentValues values) {

        final Route route = new Route();

        if (values.containsKey("id")) route.setId(values.getAsInteger("id"));
        if (values.containsKey("name")) route.setName(values.getAsString("name"));
        if (values.containsKey("startPointlat")) route.setStartPointlat(values.getAsDouble("startPointlat"));
        if (values.containsKey("startPointlng")) route.setStartPointlat(values.getAsDouble("startPointlng"));
        if (values.containsKey("endPointlat")) route.setStartPointlat(values.getAsDouble("endPointlat"));
        if (values.containsKey("endPointlng")) route.setStartPointlat(values.getAsDouble("endPointlng"));
        if (values.containsKey("valid")) route.setValid(values.getAsBoolean("valid"));

        return route;
    }

    public static ContentValues createContentValuesFromRouteInsert(Route route) {

        final ContentValues values = new ContentValues();

        values.put("name",route.getName());
        values.put("startPointlat",route.getStartPointlat());
        values.put("startPointlng",route.getStartPointlng());
        values.put("endPointlat",route.getEndPointlat());
        values.put("endPointlng",route.getEndPointlng());
        values.put("valid",route.getValid());

        return values;
    }

    public static ContentValues createContentValuesFromRouteUpdate(Route route) {

        final ContentValues values = new ContentValues();

        values.put("id",route.getId());
        values.put("name",route.getName());
        values.put("startPointlat",route.getStartPointlat());
        values.put("startPointlng",route.getStartPointlng());
        values.put("endPointlat",route.getEndPointlat());
        values.put("endPointlng",route.getEndPointlng());
        values.put("valid",route.getValid());

        return values;
    }

    public static Route getRouteFromCursor(Cursor cursor){

        final Route route = new Route();

        if(cursor!=null){
            route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            route.setStartPointlat(cursor.getDouble(cursor.getColumnIndexOrThrow("startPointlat")));
            route.setStartPointlng(cursor.getDouble(cursor.getColumnIndexOrThrow("startPointlng")));
            route.setEndPointlat(cursor.getDouble(cursor.getColumnIndexOrThrow("endPointlat")));
            route.setEndPointlng(cursor.getDouble(cursor.getColumnIndexOrThrow("endPointlng")));
            route.setValid(cursor.getInt(cursor.getColumnIndexOrThrow("valid")) > 0);
        }

        return route;
    }
}
