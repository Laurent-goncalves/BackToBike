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
    private Double startPointlat;
    private Double startPointlng;
    private Double endPointlat;
    private Double endPointlng;
    private int idRoute;

    public RouteSegment(int id, Double startPointlat, Double startPointlng, Double endPointlat, Double endPointlng, int idRoute) {
        this.id = id;
        this.startPointlat = startPointlat;
        this.startPointlng = startPointlng;
        this.endPointlat = endPointlat;
        this.endPointlng = endPointlng;
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
        if (values.containsKey("startPointlat")) routeSegment.setStartPointlat(values.getAsDouble("startPointlat"));
        if (values.containsKey("startPointlng")) routeSegment.setStartPointlng(values.getAsDouble("startPointlng"));
        if (values.containsKey("endPointlat")) routeSegment.setEndPointlat(values.getAsDouble("endPointlat"));
        if (values.containsKey("endPointlng")) routeSegment.setEndPointlng(values.getAsDouble("endPointlng"));
        if (values.containsKey("idRoute")) routeSegment.setIdRoute(values.getAsInteger("idRoute"));

        return routeSegment;
    }

    public static ContentValues createContentValuesFromRouteSegmentInsert(RouteSegment routeSegment) {

        final ContentValues values = new ContentValues();

        values.put("startPointlat",routeSegment.getStartPointlat());
        values.put("startPointlng",routeSegment.getStartPointlng());
        values.put("endPointlat",routeSegment.getEndPointlat());
        values.put("endPointlng",routeSegment.getEndPointlng());
        values.put("idRoute",routeSegment.getIdRoute());

        return values;
    }

    public static ContentValues createContentValuesFromRouteSegmentUpdate(RouteSegment routeSegment) {

        final ContentValues values = new ContentValues();

        values.put("id",routeSegment.getId());
        values.put("startPointlat",routeSegment.getStartPointlat());
        values.put("startPointlng",routeSegment.getStartPointlng());
        values.put("endPointlat",routeSegment.getEndPointlat());
        values.put("endPointlng",routeSegment.getEndPointlng());
        values.put("idRoute",routeSegment.getIdRoute());

        return values;
    }

    public static List<RouteSegment> getRouteSegmentFromCursor(Cursor cursor){

        final List<RouteSegment> listSegments = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                RouteSegment routeSegment = new RouteSegment();

                routeSegment.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                routeSegment.setStartPointlat(cursor.getDouble(cursor.getColumnIndexOrThrow("startPointlat")));
                routeSegment.setStartPointlng(cursor.getDouble(cursor.getColumnIndexOrThrow("startPointlng")));
                routeSegment.setEndPointlat(cursor.getDouble(cursor.getColumnIndexOrThrow("endPointlat")));
                routeSegment.setEndPointlng(cursor.getDouble(cursor.getColumnIndexOrThrow("endPointlng")));
                routeSegment.setIdRoute(cursor.getInt(cursor.getColumnIndexOrThrow("idRoute")));

                listSegments.add(routeSegment);
            }
            cursor.close();
        }

        return listSegments;
    }

}
