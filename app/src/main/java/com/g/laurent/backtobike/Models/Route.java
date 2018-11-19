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
    private String idEvent;
    private String typeRoute;
    private transient List<RouteSegment> listRouteSegment;

    public Route(int id, String name, String idEvent, String typeRoute) {
        this.id = id;
        this.name = name;
        this.idEvent=idEvent;
        this.typeRoute=typeRoute;
    }

    @Ignore
    public Route(int id, String name, String idEvent, String typeRoute, List<RouteSegment> listRouteSegment) {
        this.id = id;
        this.name = name;
        this.idEvent=idEvent;
        this.typeRoute=typeRoute;
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

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getTypeRoute() {
        return typeRoute;
    }

    public void setTypeRoute(String typeRoute) {
        this.typeRoute = typeRoute;
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
        if (values.containsKey("idEvent")) route.setIdEvent(values.getAsString("idEvent"));
        if (values.containsKey("typeRoute")) route.setTypeRoute(values.getAsString("typeRoute"));

        return route;
    }

    public static ContentValues createContentValuesFromRouteInsert(Route route) {

        final ContentValues values = new ContentValues();

        values.put("name",route.getName());
        values.put("idEvent",route.getIdEvent());
        values.put("typeRoute",route.getTypeRoute());

        return values;
    }

    public static ContentValues createContentValuesFromRouteUpdate(Route route) {

        final ContentValues values = new ContentValues();

        values.put("id",route.getId());
        values.put("name",route.getName());
        values.put("idEvent",route.getIdEvent());
        values.put("typeRoute",route.getTypeRoute());

        return values;
    }

    public static Route getRouteFromCursor(Cursor cursor){

        final Route route = new Route();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                route.setIdEvent(cursor.getString(cursor.getColumnIndexOrThrow("idEvent")));
                route.setTypeRoute(cursor.getString(cursor.getColumnIndexOrThrow("typeRoute")));
            }
            cursor.close();
        }

        return route;
    }

    public static List<Route> getListRoutesValidFromCursor(Cursor cursor) {

        List<Route> listRoutes = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                final Route route = new Route();
                route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                route.setIdEvent(cursor.getString(cursor.getColumnIndexOrThrow("idEvent")));
                route.setTypeRoute(cursor.getString(cursor.getColumnIndexOrThrow("typeRoute")));
                listRoutes.add(route);
            }
            cursor.close();
        }

        return listRoutes;
    }

    public static List<Route> getListRoutesFromCursor(Cursor cursor) {

        List<Route> listRoutes = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()) {
                final Route route = new Route();
                route.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                route.setIdEvent(cursor.getString(cursor.getColumnIndexOrThrow("idEvent")));
                route.setTypeRoute(cursor.getString(cursor.getColumnIndexOrThrow("typeRoute")));
                listRoutes.add(route);
            }
            cursor.close();
        }

        return listRoutes;
    }
}
