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
    private static final String TAG_DELETE = "tag_delete";
    private static final String TAG_ADD_SEGMENT = "tag_add_segment";
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
    private List<LatLng> route;
    private List<LatLng> routeAlt;
    private Context context;
    private SegmentsHandler segmentsHandler;
    private MarkersHandler markersHandler;

    public GraphicsHandler(ConfigureTraceActivity config, View view, GoogleMap map, Context context) {
        this.config = config;
        this.context=context;
        this.map=map;
        route = new ArrayList<>();
        segmentsHandler = new SegmentsHandler(this, map,context);
        markersHandler = new MarkersHandler(this, map);
        ButterKnife.bind(this, view);
    }

    // ------------------------------- BUTTONS ADD AND DELETE ---------------------------------------

    public void updateButtonsState(String buttonSelected){

        Boolean hasStartPoint = (markersHandler.getStartPoint()!=null);
        Boolean hasEndPoint = (markersHandler.getEndPoint()!=null);
        Boolean hasRouteAlt = (routeAlt!=null);
        Boolean hasSegments = (route.size()>=2);

        // Add start point
        if(hasStartPoint)
            setColorButtonEnabled(buttonAddStartPoint,false);
        else {
            setColorButtonEnabled(buttonAddStartPoint, true);

            if(buttonSelected.equals(TAG_START_POINT))
                setButtonPressed(buttonAddStartPoint);
        }

        // Add segment point
        if((hasStartPoint && !(hasEndPoint && hasSegments && !hasRouteAlt)) || (!hasEndPoint && hasSegments)) {
            setColorButtonEnabled(buttonAddSegment,true);

            if(buttonSelected.equals(TAG_ADD_SEGMENT))
                setButtonPressed(buttonAddSegment);
        } else
            setColorButtonEnabled(buttonAddSegment,false);

        // Add end point
        if((hasStartPoint && !hasEndPoint) || (!hasStartPoint && !hasEndPoint & hasSegments & !hasRouteAlt)) {
            setColorButtonEnabled(buttonAddEndPoint,true);

            if(buttonSelected.equals(TAG_END_POINT))
                setButtonPressed(buttonAddEndPoint);

        } else
            setColorButtonEnabled(buttonAddEndPoint,false);

        // Delete button
        if(!hasStartPoint && !hasEndPoint & !hasSegments & !hasRouteAlt)
            setColorButtonEnabled(buttonDelete, false);
        else {
            setColorButtonEnabled(buttonDelete, true);

            if(buttonSelected.equals(TAG_DELETE))
                setButtonPressed(buttonDelete);
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

        if(buttonSelected==null){
            setColorButtonSelected(buttonAddStartPoint,false);
            setColorButtonSelected(buttonAddSegment,false);
            setColorButtonSelected(buttonAddEndPoint,false);
            setColorButtonSelected(buttonDelete,false);
        } else {
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
    }

    // ----------------------------------- ADD MARKERS ---------------------------------------

    public void handleStartPointAdding(LatLng latLng){
        getMarkersHandler().addStartPoint(latLng);
        config.handleDrawMap();
        updateButtonsState(TAG_START_POINT);
    }

    public void handleEndPointAdding(LatLng latLng){
        getMarkersHandler().addEndPoint(latLng);
        config.handleDrawMap();
        updateButtonsState(TAG_END_POINT);
    }

    // ----------------------------------- DRAG MARKERS --------------------------------------------------

    public void handleMarkerDragging(Marker marker, int index, int routeNumber){

        if(marker.getTag()!=null) {
            if (marker.getTag().equals(TAG_START_POINT)) { // move polyline from start point
                route.set(0, marker.getPosition());
                markersHandler.setStartPoint(marker.getPosition());

            } else if(marker.getTag().equals(TAG_END_POINT)){ // move polyline from end point
                if(routeAlt==null)
                    route.set(route.size()-1, marker.getPosition());
                else
                    routeAlt.set(routeAlt.size()-1, marker.getPosition());

                markersHandler.setEndPoint(marker.getPosition());
            } else if(UtilsGoogleMaps.isMarkerADragPoint(marker)) { // move polyline from route point

                if(routeNumber==1 && index!=-1)
                    route.set(index, marker.getPosition());
                else if(routeNumber==2 && index!=-1)
                    routeAlt.set(index, marker.getPosition());
            }
        }

        drawOnlySegments();
    }

    // ----------------------------------- ADD SEGMENT ---------------------------------------

    public void handleSegmentAdding(LatLng latLng){

        if(routeAlt==null){ // if no routeAlt

            Boolean hasStartPoint = (markersHandler.getStartPoint()!=null);
            Boolean hasEndPoint= (markersHandler.getEndPoint()!=null);

            int index = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng, route, hasStartPoint, hasEndPoint, map);

            if(index==-1){ // if index found, add a drag marker
                route.add(latLng);
            }

        } else {

            int indexNear = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng, routeAlt, false, false, map);

            if(indexNear!=-1){
                segmentsHandler.closeRouteAlt();
                drawMap(false);
            } else {// if no point near
                route.add(latLng);
            }
        }

        config.handleDrawMap();
        updateButtonsState(TAG_ADD_SEGMENT);
    }

    // ----------------------------------- DELETE SEGMENT ---------------------------------------

    public void handleSegmentDeleting(LatLng latLng){
        segmentsHandler.handleClickSegmentToDelete(latLng);
        config.handleDrawMap();
        updateButtonsState(TAG_DELETE);
    }

    // ----------------------------------- DRAW MAP ---------------------------------------

    public void drawOnlySegments(){

        segmentsHandler.drawSegments(isRouteFinished());

        config.updateMileage(route, routeAlt);
        config.updateTimeEstimation(route, routeAlt);
    }

    public void drawMap(Boolean drawDragPoints){

        map.clear();

        markersHandler.drawMarkers(isRouteFinished(), drawDragPoints);
        segmentsHandler.drawSegments(isRouteFinished());

        config.updateMileage(route, routeAlt);
        config.updateTimeEstimation(route, routeAlt);
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

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

    public ConfigureTraceActivity getConfig() {
        return config;
    }

    public MarkersHandler getMarkersHandler() {
        return markersHandler;
    }

    public Context getContext() {
        return context;
    }

    public Boolean isRouteFinished(){

        Boolean hasStartPoint = (markersHandler.getStartPoint()!=null);
        Boolean hasEndPoint = (markersHandler.getEndPoint()!=null);
        Boolean isRouteFinished = (routeAlt==null);

        return hasStartPoint && hasEndPoint && isRouteFinished;
    }
}
