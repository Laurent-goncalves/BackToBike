package com.g.laurent.backtobike.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


public class MarkersHandler {

    private static final String TAG_START_POINT = "tag_start_point";
    private static final String TAG_END_POINT = "tag_end_point";
    private GraphicsHandler graphicsHandler;
    private GoogleMap map;
    private LatLng startPoint;
    private LatLng endPoint;
    private List<Marker> markers;
    private List<LatLng> route;
    private List<LatLng> routeAlt;
    private int limitInf;
    private int limitSup;

    public MarkersHandler(GraphicsHandler graphicsHandler, GoogleMap map) {
        this.graphicsHandler = graphicsHandler;
        this.map=map;
        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();
        markers = new ArrayList<>();
    }

    public void addDragMarker(LatLng position, Boolean routeFinished, String tag){

        Marker marker;
        Bitmap iconBitmap;

        if(routeFinished){ // Route finished
            iconBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(graphicsHandler.getContext().getResources(),
                    R.drawable.baseline_radio_button_checked_green_18), 30, 30, false);
        } else { // Route not finished
            iconBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(graphicsHandler.getContext().getResources(),
                    R.drawable.baseline_radio_button_checked_blue_18), 30, 30, false);
        }

        marker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                .anchor(0.5f,0.5f)
                .position(position).draggable(true));

        marker.setTag(tag);
        markers.add(marker);
    }

    public void addStartPoint(LatLng latLng){

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        // change hasStartPoint state
        startPoint = latLng;

        // Update polyline
        if(route.size()>=1) // if already a path exists
            route.add(0, startPoint);
        else {
            route.add(startPoint);
        }
    }

    public void removeStartPoint(){

        route = graphicsHandler.getRoute();

        // change hasEndPoint state
        startPoint = null;

        // Remove first point of route
        route.remove(0);

        graphicsHandler.setRoute(route);
    }

    public void addEndPoint(LatLng latLng){

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        // change endPoint
        endPoint = latLng;

        // Update polyline
        if(routeAlt!=null){
            routeAlt.add(endPoint);
        } else
            route.add(endPoint);

        graphicsHandler.setRoute(route);
        graphicsHandler.setRouteAlt(routeAlt);
    }

    public void removeEndPoint(){

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        // change endPoint
        endPoint = null;

        // Remove last point of route
        if(routeAlt==null)
            route.remove(route.size()-1);
        else
            routeAlt.remove(routeAlt.size()-1);

        graphicsHandler.setRoute(route);
        graphicsHandler.setRouteAlt(routeAlt);
    }

    public void drawMarkers(Boolean routeFinished, Boolean addDragMarkers){

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();
        markers = new ArrayList<>();

        // |||||||||||||||||||||||||||||||||||| START POINT |||||||||||||||||||||||||||||||||||||||||||||||||||||||||

        if(startPoint!=null){ // if start point latlng not null, add a marker

            Marker marker;

            if(routeFinished){ // Route finished
                marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_green_36))
                        .position(startPoint).draggable(true));
            } else { // Route not finished
                marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(startPoint).draggable(true));
            }

            marker.setTag(TAG_START_POINT);

            // Add marker to markers list
            markers.add(marker);

        } else { // if start point null, remove marker if exists
            for(Marker marker : markers){
                if(marker.getTag()!=null){
                    if(marker.getTag().equals(TAG_START_POINT))
                        marker.remove();
                }
            }
        }

        // |||||||||||||||||||||||||||||||||||| END POINT |||||||||||||||||||||||||||||||||||||||||||||||||||||||||

        if(endPoint!=null){ // if start point latlng not null, add a marker

            Marker marker;

            if(routeFinished){ // Route finished
                marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_green_48))
                        .anchor(0.3f,0.85f)
                        .position(endPoint).draggable(true));
            } else { // Route not finished
                marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_blue_48))
                        .anchor(0.3f,0.85f)
                        .position(endPoint).draggable(true));
            }

            marker.setTag(TAG_END_POINT);

            // Add marker to markers list
            markers.add(marker);

        } else { // if start point null, remove marker if exists
            for(Marker marker : markers){
                if(marker.getTag()!=null){
                    if(marker.getTag().equals(TAG_END_POINT))
                        marker.remove();
                }
            }
        }

        // |||||||||||||||||||||||||||||||||||| DRAG POINTS |||||||||||||||||||||||||||||||||||||||||||||||||||||||||

        if(addDragMarkers){
            drawDragMarkers(routeFinished);
        }
    }

    public void drawDragMarkers(Boolean routeFinished){

        if(route.size()>=3){

            if(endPoint!=null)
                limitSup = route.size()-2;
            else
                limitSup = route.size()-1;

            if(startPoint!=null)
                limitInf = 1;
            else
                limitInf = 0;

            for(int i = limitInf; i <= limitSup ; i++){
                addDragMarker(route.get(i), routeFinished, "ROUTE-" + i);
            }
        }

        if(routeAlt!=null){
            if(routeAlt.size()>=2) {

                limitInf = 1;

                if(endPoint!=null)
                    limitSup = routeAlt.size()-2;
                else
                    limitSup = routeAlt.size()-1;

                for(int i = limitInf; i <= limitSup ; i++){
                    addDragMarker(routeAlt.get(i), routeFinished, "ROUTEALT-" + i);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public LatLng getStartPoint() {
        return startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }
}


        /*if(addMarker){



            if(route.size()>=3){



                if(endPoint!=null)
                    limitSup = route.size()-1;
                else
                    limitSup = route.size();

                if(startPoint!=null)
                    limitInf = 1;
                else
                    limitInf = 0;

                for(int i = limitInf; i<=limitSup ; i++){
                    Marker marker = map.addMarker(new MarkerOptions())
                }
            }

            if(routeAlt!=null){
                if(routeAlt.size()>=3) {

                    int limitSup;

                    if(endPoint!=null)
                        limitSup = routeAlt.size()-1;
                    else
                        limitSup = routeAlt.size();

                    for(int i = 0; i<=limitSup ; i++){

                    }
                }
            }
        }*/