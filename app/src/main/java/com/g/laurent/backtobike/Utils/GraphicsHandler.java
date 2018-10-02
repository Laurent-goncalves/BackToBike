package com.g.laurent.backtobike.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("ClickableViewAccessibility")
public class GraphicsHandler {

    private static final String TAG_START_POINT = "tag_start_point";
    private static final String TAG_END_POINT = "tag_end_point";
    private static final String TAG_ROUTE_POINT = "tag_route_point";
    @BindView(R.id.button_add_segment) ImageButton buttonAddSegment;
    @BindView(R.id.button_add_start_point) ImageButton buttonAddStartPoint;
    @BindView(R.id.button_add_end_point) ImageButton buttonAddEndPoint;
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_save) Button buttonSave;
    @BindView(R.id.button_delete) ImageButton buttonDelete;
    @BindView(R.id.mileage_measured) TextView mileageView;
    @BindView(R.id.time_estimated) TextView timeView;
    private ConfigureTraceActivity config;
    private GoogleMap map;
    private PolylineOptions rectOptions;
    private PolylineOptions rectOptionsAlt;
    private List<LatLng> route;
    private List<LatLng> routeAlt;
    private Polyline polyline;
    private Polyline polylineAlt;
    private List<Marker> markers;
    private Context context;
    private View view;
    private int index;
    private LatLng lastPoint;
    private Boolean hasStartPoint;
    private Boolean hasEndPoint;
    private SegmentsHandler segmentsHandler;

    public GraphicsHandler(ConfigureTraceActivity config, View view, GoogleMap map, Context context) {
        this.config = config;
        this.view = view;
        this.context=context;
        this.map=map;
        hasStartPoint = false;
        hasEndPoint = false;
        segmentsHandler = new SegmentsHandler(this, map);
        route = new ArrayList<>();
        markers = new ArrayList<>();
        ButterKnife.bind(this, view);
    }

    // ------------------------------- BUTTONS ADD AND DELETE ---------------------------------------

    public void updateButtonsState(){

        if(hasStartPoint && hasEndPoint){
            setColorButtonEnabled(buttonAddStartPoint,false);
            setColorButtonEnabled(buttonAddSegment,false);
            setColorButtonEnabled(buttonAddEndPoint,false);
            setColorButtonEnabled(buttonDelete,true);
        } else if(hasStartPoint && !hasEndPoint){
            setColorButtonEnabled(buttonAddStartPoint,false);
            setColorButtonEnabled(buttonAddSegment,true);
            setColorButtonEnabled(buttonAddEndPoint,true);
            setColorButtonEnabled(buttonDelete,true);
        } else if(!hasStartPoint){ // if no start point and no route
            setColorButtonEnabled(buttonAddStartPoint,true);
            setColorButtonEnabled(buttonAddSegment,false);
            setColorButtonEnabled(buttonAddEndPoint,false);
            setColorButtonEnabled(buttonDelete,false);
        }
    }

    private void setColorButtonSelected(ImageButton button, Boolean selected){

        button.setPressed(selected);
        button.setSelected(selected);

        if(selected)
            button.setColorFilter(context.getResources().getColor(R.color.colorWhite));
        else if(button.isEnabled())
            button.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark));
        else if(!button.isEnabled())
            button.setColorFilter(context.getResources().getColor(R.color.colorGray));
    }

    private void setColorButtonEnabled(ImageButton button, Boolean enabled){
        button.setEnabled(enabled);
        setColorButtonSelected(button,false);
    }

    public void setButtonPressed(ImageButton buttonSelected){

        if(buttonSelected.equals(buttonAddStartPoint)){
            setColorButtonSelected(buttonAddStartPoint,true);
            setColorButtonSelected(buttonAddSegment,false);
            setColorButtonSelected(buttonAddEndPoint,false);
            setColorButtonSelected(buttonDelete,false);
        } else if (buttonSelected.equals(buttonAddSegment)){
            setColorButtonSelected(buttonAddStartPoint,false);
            setColorButtonSelected(buttonAddSegment,true);
            setColorButtonSelected(buttonAddEndPoint,false);
            setColorButtonSelected(buttonDelete,false);
        } else if (buttonSelected.equals(buttonAddEndPoint)){
            setColorButtonSelected(buttonAddStartPoint,false);
            setColorButtonSelected(buttonAddSegment,false);
            setColorButtonSelected(buttonAddEndPoint,true);
            setColorButtonSelected(buttonDelete,false);
        } else if (buttonSelected.equals(buttonDelete)){
            setColorButtonSelected(buttonAddStartPoint,false);
            setColorButtonSelected(buttonAddSegment,false);
            setColorButtonSelected(buttonAddEndPoint,false);
            setColorButtonSelected(buttonDelete,true);
        }
    }

    // ----------------------------------- MARKERS --------------------------------------------------

    public void handleMarkerDragging(Marker marker){
        if(marker.getTag()!=null) {
            if (marker.getTag().equals(TAG_START_POINT)) { // move polyline from start point
                route.set(0, marker.getPosition());
            } else if(marker.getTag().equals(TAG_END_POINT)){ // move polyline from end point
                route.set(route.size()-1, marker.getPosition());
            } else if(marker.getTag().equals(TAG_ROUTE_POINT) && index!=-1) { // move polyline from route point
                route.set(index, marker.getPosition());
            }

            if(routeAlt!=null)
                draw2PolyLines(); // draw 2 polylines
            else
                drawPolyLine(); // draw polyline
        }
    }

    public void addStartPoint(LatLng latLng){

        // remove Route Marker
        UtilsGoogleMaps.removeRouteMarker(markers);

        // change hasStartPoint state
        hasStartPoint = true;

        // Create marker
        Marker marker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(latLng).draggable(true));
        marker.setTag(TAG_START_POINT);

        // Add marker to markers list
        markers.add(marker);

        // Update buttons state
        updateButtonsState();

        // Update polyline
        if(route.size()>=1) // if already a path exists
            drawLineOnMap(latLng, 0);
        else
            drawLineOnMap(latLng);
    }

    public void removeStartPoint(Marker marker){

        // change hasEndPoint state
        hasStartPoint = false;

        // Remove marker
        for(Marker mark : markers){
            if(mark.getTag()!=null){
                if(mark.getTag().equals(TAG_START_POINT))
                    mark.remove();
            }
        }

        marker.remove();

        // Update buttons state
        updateButtonsState();

        // Remove first point of route
        route.remove(0);

        // Update polyline
        if(route.size()>=2) {
            lastPoint = route.get(0);
        } else if (hasEndPoint) {
            lastPoint = route.get(route.size()-1);
        } else {
            route = new ArrayList<>();
            lastPoint = null;
        }

        drawPolyLine();
    }

    public void addEndPoint(LatLng latLng){
        // remove Route Marker
        UtilsGoogleMaps.removeRouteMarker(markers);

        // change hasEndPoint state
        hasEndPoint = true;

        // Create marker
        Marker marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true)
                .anchor(0.3f,0.85f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_blue_48)));
        marker.setTag(TAG_END_POINT);

        // Add marker to markers list
        markers.add(marker);

        // Update buttons state
        updateButtonsState();

        // Update polyline
        if(routeAlt==null){
            drawLineOnMap(latLng);
        } else {
            routeAlt.add(latLng);
            draw2PolyLines();
        }
    }

    public void removeEndPoint(Marker marker){

        // change hasEndPoint state
        hasEndPoint = false;

        // Remove marker
        for(Marker mark : markers){
            if(mark.getTag()!=null){
                if(mark.getTag().equals(TAG_END_POINT))
                    mark.remove();
            }
        }

        marker.remove();

        // Update buttons state
        updateButtonsState();

        // Remove last point of route
        route.remove(route.size()-1);

        // Update polyline
        if(route.size()>=2)
            lastPoint = route.get(route.size()-1);
        else if(route.size()==1)
            lastPoint = route.get(0);

        drawPolyLine();
    }

    // ---------------------------- DRAW POLYLINE AND MARKERS ---------------------------------------

    private void drawLineOnMap(LatLng point, int index){
        if(index!=-1)
            route.add(index, point);
        else
            route.add(point);

        if(routeAlt==null)
            drawPolyLine();
        else
            draw2PolyLines();
    }

    private void drawLineOnMap(LatLng point){
        route.add(point);
        drawPolyLine();
        lastPoint = point;
    }

    public void drawPolyLine(){

        // Create and add the polyline to the map
        if(polyline!=null)
            polyline.remove();

        if(hasStartPoint && hasEndPoint && routeAlt == null){ // if route finished
            setRouteFinished(true);
        } else { // if route not finished
            setRouteFinished(false);
        }

        config.updateMileage(polyline);
        config.updateTimeEstimation(polyline);
    }

    int stop = 0;

    public void setStop(int stop) {
        this.stop = stop;
    }

    public void draw2PolyLines() {

        // Create and add the polylines to the map
        if (polyline != null) {
            polyline.remove();
        }

        if (polylineAlt != null){
            polylineAlt.remove();
        }

        rectOptions = new PolylineOptions()
                .width(8)
                .color(context.getResources().getColor(R.color.colorPolylineNotComplete))
                .addAll(route);

        polyline = map.addPolyline(rectOptions);

        rectOptionsAlt = null;

        if (routeAlt != null) {
            rectOptionsAlt = new PolylineOptions()
                    .width(8)
                    .color(context.getResources().getColor(R.color.colorPolylineNotComplete))
                    .addAll(routeAlt);

            polylineAlt = map.addPolyline(rectOptionsAlt);
        }


        for(Marker marker : markers){
            if(marker.getTag()!=null){
                if(marker.getTag().equals(TAG_START_POINT))
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                else if(marker.getTag().equals(TAG_END_POINT))
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_blue_48));
            }
        }

        if(hasStartPoint && hasEndPoint && routeAlt == null){ // if route finished
            setRouteFinished(true);
        }

        config.updateMileage(polyline, polylineAlt);
        config.updateTimeEstimation(polyline, polylineAlt);
    }

    public void addSegment(LatLng latLng){

        // remove Route Marker
        UtilsGoogleMaps.removeRouteMarker(markers);

        index = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng, route, hasStartPoint, hasEndPoint, map);

        if(index!=-1){ // if index found, add a marker

            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_radio_button_checked_blue_24))
                    .anchor(0.5f,0.5f)
                    .position(route.get(index)).draggable(true));
            marker.setTag(TAG_ROUTE_POINT);
            markers.add(marker);

        } else if(!hasEndPoint){ // if index not found, add polyline
            if (lastPoint != null) {
                drawLineOnMap(latLng);
            }
            lastPoint = latLng;
        }
    }

    public void addMarkerToMoveSegment(LatLng latLng){

        // remove Route Marker
        UtilsGoogleMaps.removeRouteMarker(markers);

        index = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng, route, hasStartPoint, hasEndPoint, map);

        if(index!=-1){ // if index found, add a marker

            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_radio_button_checked_blue_24))
                    .anchor(0.5f,0.5f)
                    .position(route.get(index)).draggable(true));
            marker.setTag(TAG_ROUTE_POINT);
            markers.add(marker);

        }
    }

    public void addAltSegment(LatLng latLng){

        // remove Route Marker
        UtilsGoogleMaps.removeRouteMarker(markers);

        int indexNear = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng, routeAlt, false, false, map);

        if(indexNear!=-1){
            segmentsHandler.closeRouteAlt();
        } else {// if no point near, draw a new segment
            drawLineOnMap(latLng);
            lastPoint = latLng;
        }
    }

    private void setRouteFinished(Boolean finished){

        // ---------------------------------- DRAW POLYLINE ----------------------------------------
        if(finished){

            rectOptions = new PolylineOptions()
                    .width(8)
                    .color(context.getResources().getColor(R.color.colorPolylineComplete))
                    .addAll(route);

            polyline = map.addPolyline(rectOptions);

            // Update buttons state
            updateButtonsState();

        } else {

            rectOptions = new PolylineOptions()
                    .width(8)
                    .color(context.getResources().getColor(R.color.colorPolylineNotComplete))
                    .addAll(route);

            polyline = map.addPolyline(rectOptions);
        }

        // ---------------------------------- CHANGE MARKERS COLOR ----------------------------------------
        if(finished){
            for(Marker marker : markers){
                if(marker.getTag()!=null){
                    if(marker.getTag().equals(TAG_START_POINT))
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_green_36));
                    else if(marker.getTag().equals(TAG_END_POINT))
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_green_48));
                }
            }
        } else {
            for(Marker marker : markers){
                if(marker.getTag()!=null){
                    if(marker.getTag().equals(TAG_START_POINT))
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    else if(marker.getTag().equals(TAG_END_POINT))
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_blue_48));
                }
            }
        }
    }


    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void setIndex(int index) {
        this.index = index;
    }

    public Boolean getHasStartPoint() {
        return hasStartPoint;
    }

    public Boolean getHasEndPoint() {
        return hasEndPoint;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public SegmentsHandler getSegmentsHandler() {
        return segmentsHandler;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public void setRouteAlt(List<LatLng> routeAlt) {
        this.routeAlt = routeAlt;
    }

    public List<LatLng> getRouteAlt() {
        return routeAlt;
    }

    public void setLastPoint(LatLng lastPoint) {
        this.lastPoint = lastPoint;
    }

    public ConfigureTraceActivity getConfig() {
        return config;
    }
}
