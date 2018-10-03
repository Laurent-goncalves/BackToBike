package com.g.laurent.backtobike.Utils;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.g.laurent.backtobike.Controllers.TraceActivity;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressLint("ClickableViewAccessibility")
public class ConfigureTraceActivity {


    @BindView(R.id.button_add_segment) ImageButton buttonAddSegment;
    @BindView(R.id.button_add_start_point) ImageButton buttonAddStartPoint;
    @BindView(R.id.button_add_end_point) ImageButton buttonAddEndPoint;
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_save) Button buttonSave;
    @BindView(R.id.button_delete) ImageButton buttonDelete;
    @BindView(R.id.mileage_measured) TextView mileageView;
    @BindView(R.id.time_estimated) TextView timeView;
    private static final String TAG_START_POINT = "tag_start_point";
    private static final String TAG_END_POINT = "tag_end_point";
    private static final String TAG_DELETE = "tag_delete";
    private static final String TAG_ADD_SEGMENT = "tag_add_segment";
    private GoogleMap map;
    private Boolean deleteMode;
    private int index;
    private int routeNumber;
    private GraphicsHandler graphicsHandler;


    public ConfigureTraceActivity(View view, TraceActivity traceActivity, final GoogleMap map) {

        this.map = map;

        ButterKnife.bind(this, view);

        graphicsHandler = new GraphicsHandler(this,view, map,traceActivity.getApplicationContext());
        graphicsHandler.updateButtonsState("any");

        configureMapListeners();
    }

    private void configureMapListeners(){

        // LISTENERS FOR DRAGGING MARKERS (START AND END)
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                if(marker.getTag()!=null){
                    if(UtilsGoogleMaps.isMarkerADragPoint(marker)){
                        routeNumber = UtilsGoogleMaps.extractRouteFromTag(marker.getTag().toString());
                        index = UtilsGoogleMaps.extractIndexFromTag(marker.getTag().toString());
                    }
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                graphicsHandler.handleMarkerDragging(marker, index, routeNumber);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                if(marker.getTag()!=null) {
                    if (UtilsGoogleMaps.isMarkerADragPoint(marker))
                        graphicsHandler.drawMap(true);
                    else
                        graphicsHandler.drawMap(false);
                }
            }
        });

        // LISTENERS FOR MARKER CLICKING
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(deleteMode){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(TAG_START_POINT)){
                            graphicsHandler.getMarkersHandler().removeStartPoint();
                            graphicsHandler.drawMap(false);
                            graphicsHandler.updateButtonsState(TAG_DELETE);
                        } else if(marker.getTag().equals(TAG_END_POINT)){
                            graphicsHandler.getMarkersHandler().removeEndPoint();
                            graphicsHandler.drawMap(false);
                            graphicsHandler.updateButtonsState(TAG_DELETE);
                        }
                    }
                } else if(buttonAddSegment.isSelected() && graphicsHandler.getRouteAlt()!=null) {
                    graphicsHandler.handleSegmentAdding(marker.getPosition());
                }
                return false;
            }
        });

        // LISTENERS FOR CREATING / DRAGGING POINTS FROM POLYLINE AND ADD MARKERS
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //-------------------------  Add a start point on map --------------------------------------
                if (buttonAddStartPoint.isSelected()) {
                    graphicsHandler.handleStartPointAdding(latLng);
                    graphicsHandler.drawMap(false);
                    graphicsHandler.updateButtonsState(TAG_START_POINT);
                }

                //-------------------  Add a route marker on map OR add a polyline --------------------------
                if (buttonAddSegment.isSelected()) {
                    graphicsHandler.handleSegmentAdding(latLng);
                    graphicsHandler.drawMap(true);
                    graphicsHandler.updateButtonsState(TAG_ADD_SEGMENT);
                }

                //-------------------------  Add an end point on map --------------------------------------
                if (buttonAddEndPoint.isSelected()) {
                    graphicsHandler.handleEndPointAdding(latLng);
                    graphicsHandler.drawMap(false);
                    graphicsHandler.updateButtonsState(TAG_END_POINT);
                }

                //----------------------------  Delete segment on map --------------------------------------
                if (buttonDelete.isSelected()) {
                    graphicsHandler.handleSegmentDeleting(latLng);
                    graphicsHandler.drawMap(false);
                    graphicsHandler.updateButtonsState(TAG_DELETE);
                }
            }
        });
    }

    @OnClick(R.id.button_add_segment)
    public void addSegment(){
        deleteMode = false;

        graphicsHandler.setButtonPressed(buttonAddSegment);

        graphicsHandler.drawMap(true);

        buttonAddSegment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    deleteMode = false;
                    graphicsHandler.setButtonPressed(buttonAddSegment);
                }
                return true;
            }
        });
    }

    @OnClick(R.id.button_add_start_point)
    public void addStartPoint(){
        deleteMode = false;
        graphicsHandler.setButtonPressed(buttonAddStartPoint);

        buttonAddStartPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    deleteMode = false;
                    graphicsHandler.setButtonPressed(buttonAddStartPoint);
                }
                return true;
            }
        });
    }

    @OnClick(R.id.button_add_end_point)
    public void addEndPoint(){

        deleteMode = false;
        graphicsHandler.setButtonPressed(buttonAddEndPoint);

        // Button remain pressed
        buttonAddEndPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    deleteMode = false;
                    graphicsHandler.setButtonPressed(buttonAddEndPoint);
                }
                return true;
            }
        });
    }

    @OnClick(R.id.button_delete)
    public void deleteAction(){

        deleteMode = true;

        // Button remain pressed
        graphicsHandler.setButtonPressed(buttonDelete);
    }

    // ---------------------------- ESTIMATE MILEAGE AND TIME ---------------------------------------

    public void updateMileage(List<LatLng> route, List<LatLng> routeAlt){

        String mileageEstimated;
        if(routeAlt==null){
            mileageEstimated = UtilsGoogleMaps.getMileageEstimated(route);
        } else {
            mileageEstimated = UtilsGoogleMaps.getMileageEstimated(route, routeAlt);
        }

        mileageView.setText(mileageEstimated);
    }

    public void updateTimeEstimation(List<LatLng> route, List<LatLng> routeAlt){
        String timeEstimated;

        if(routeAlt==null){
            timeEstimated = UtilsGoogleMaps.getTimeRoute(UtilsGoogleMaps.getMileageRoute(route));
        } else {
            timeEstimated = UtilsGoogleMaps.getTimeRoute(UtilsGoogleMaps.getMileageRoute(route) +
                    UtilsGoogleMaps.getMileageRoute(routeAlt));
        }

        timeView.setText(timeEstimated);
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public ImageButton getButtonAddSegment() {
        return buttonAddSegment;
    }

    public ImageButton getButtonDelete() {
        return buttonDelete;
    }

    public Boolean getDeleteMode() {
        return deleteMode;
    }
}
