package com.g.laurent.backtobike.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Boolean valid;
    private transient List<RouteSegment> listRouteSegment;

    public Route(int id, String name, Boolean valid) {
        this.id = id;
        this.name = name;
        this.valid = valid;
    }

    @Ignore
    public Route(int id, String name, Boolean valid, List<RouteSegment> listRouteSegment) {
        this.id = id;
        this.name = name;
        this.valid = valid;
        this.listRouteSegment=listRouteSegment;
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

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public List<RouteSegment> getListRouteSegment() {
        return listRouteSegment;
    }

    public void setListRouteSegment(List<RouteSegment> listRouteSegment) {
        this.listRouteSegment = listRouteSegment;
    }

    // --- UTILS ---
    public static Route fromContentValues(ContentValues values) {

        final Route route = new Route();

        if (values.containsKey("id")) route.setId(values.getAsInteger("id"));
        if (values.containsKey("name")) route.setName(values.getAsString("name"));
        if (values.containsKey("valid")) route.setValid(values.getAsBoolean("valid"));

        return route;
    }

    public static ContentValues createContentValuesFromRouteInsert(Route route) {

        final ContentValues values = new ContentValues();

        values.put("name",route.getName());
        values.put("valid",route.getValid());

        return values;
    }

    public static ContentValues createContentValuesFromRouteUpdate(Route route) {

        final ContentValues values = new ContentValues();

        values.put("id",route.getId());
        values.put("name",route.getName());
        values.put("valid",route.getValid());

        return values;
    }

    public static Route getRouteFromCursor(Cursor cursor){

        final Route route = new Route();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                route.setValid(cursor.getInt(cursor.getColumnIndexOrThrow("valid")) > 0);
            }
            cursor.close();
        }

        return route;
    }

    public static List<Route> getListRoutesFromCursor(Cursor cursor) {

        List<Route> listRoutes = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                final Route route = new Route();
                route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                route.setValid(cursor.getInt(cursor.getColumnIndexOrThrow("valid")) > 0);
                listRoutes.add(route);
            }
            cursor.close();
        }

        return listRoutes;
    }
}
