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


@Entity(foreignKeys = @ForeignKey(entity = Route.class, parentColumns = "id", childColumns = "idRoute"),
        indices = {@Index("idRoute")})
public class RouteSegment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int number;
    private Double lat;
    private Double lng;
    private int idRoute;

    public RouteSegment(int id, int number, Double lat, Double lng, int idRoute) {
        this.id = id;
        this.number = number;
        this.lat = lat;
        this.lng = lng;
        this.idRoute = idRoute;
    }

    @Ignore
    public RouteSegment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    // --- UTILS ---
    public static RouteSegment fromContentValues(ContentValues values) {

        final RouteSegment routeSegment = new RouteSegment();

        if (values.containsKey("id")) routeSegment.setId(values.getAsInteger("id"));
        if (values.containsKey("number")) routeSegment.setNumber(values.getAsInteger("number"));
        if (values.containsKey("lat")) routeSegment.setLat(values.getAsDouble("lat"));
        if (values.containsKey("lng")) routeSegment.setLng(values.getAsDouble("lng"));
        if (values.containsKey("idRoute")) routeSegment.setIdRoute(values.getAsInteger("idRoute"));

        return routeSegment;
    }

    public static ContentValues createContentValuesFromRouteSegmentInsert(RouteSegment routeSegment) {

        final ContentValues values = new ContentValues();

        values.put("number",routeSegment.getNumber());
        values.put("lat",routeSegment.getLat());
        values.put("lng",routeSegment.getLng());
        values.put("idRoute",routeSegment.getIdRoute());

        return values;
    }

    public static List<RouteSegment> getRouteSegmentFromCursor(Cursor cursor){

        final List<RouteSegment> listSegments = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                RouteSegment routeSegment = new RouteSegment();

                routeSegment.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                routeSegment.setNumber(cursor.getInt(cursor.getColumnIndexOrThrow("number")));
                routeSegment.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
                routeSegment.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow("lng")));
                routeSegment.setIdRoute(cursor.getInt(cursor.getColumnIndexOrThrow("idRoute")));

                listSegments.add(routeSegment);
            }
            cursor.close();
        }

        return listSegments;
    }

}
