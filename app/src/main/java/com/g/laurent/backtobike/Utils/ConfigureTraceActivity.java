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
    private GoogleMap map;
    private Boolean deleteMode;
    private GraphicsHandler graphicsHandler;


    public ConfigureTraceActivity(View view, TraceActivity traceActivity, final GoogleMap map) {

        this.map = map;

        ButterKnife.bind(this, view);

        graphicsHandler = new GraphicsHandler(this,view, map,traceActivity.getApplicationContext());
        graphicsHandler.updateButtonsState();

        configureMapListeners();
    }

    private void configureMapListeners(){

        // LISTENERS FOR DRAGGING MARKERS (START AND END)
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {
                graphicsHandler.handleMarkerDragging(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                graphicsHandler.setIndex(-1); // reset route point index
                UtilsGoogleMaps.removeRouteMarker(graphicsHandler.getMarkers()); // Remove marker route
            }
        });

        // LISTENERS FOR MARKER CLICKING
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(deleteMode){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(TAG_START_POINT)){
                            graphicsHandler.removeStartPoint(marker);
                        } else if(marker.getTag().equals(TAG_END_POINT)){
                            graphicsHandler.removeEndPoint(marker);
                        }
                    }
                }
                return false;
            }
        });

        // LISTENERS FOR CREATING / DRAGGING POINTS FROM POLYLINE AND ADD MARKERS
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            //-----------------  If no button selected, add marker to move segment --------------------
            /*if(areAllButtonNOTselected()){
                graphicsHandler.addMarkerToMoveSegment(latLng);
            }*/

            //-------------------  Add a route marker on map OR add a polyline --------------------------
            if(buttonAddSegment.isSelected()){
                if(graphicsHandler.getRouteAlt()==null)
                    graphicsHandler.addSegment(latLng);
                else
                    graphicsHandler.addAltSegment(latLng);
            }

            //-------------------------  Add a start point on map --------------------------------------
            if(!graphicsHandler.getHasStartPoint() && buttonAddStartPoint.isSelected()) {
                graphicsHandler.addStartPoint(latLng);
            }

            //-------------------------  Add an end point on map --------------------------------------
            if(!graphicsHandler.getHasEndPoint() && buttonAddEndPoint.isSelected()) {
                graphicsHandler.addEndPoint(latLng);
            }

            //----------------------------  Delete segment on map --------------------------------------
            if(buttonDelete.isSelected()){
                if(graphicsHandler.getSegmentsHandler()!=null){
                    graphicsHandler.getSegmentsHandler().handleClickSegmentToDelete(latLng);
                }
            }
            }
        });
    }

    private Boolean areAllButtonNOTselected(){
        return !buttonAddSegment.isSelected() && !buttonAddStartPoint.isSelected() && !buttonAddEndPoint.isSelected() && !buttonDelete.isSelected();
    }

    @OnClick(R.id.button_add_segment)
    public void addSegment(){
        deleteMode = false;
        graphicsHandler.setButtonPressed(buttonAddSegment);

        buttonAddSegment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
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

    public void updateMileage(Polyline polyline){
        String mileageEstimated = UtilsGoogleMaps.getMileageEstimated(polyline);
        mileageView.setText(mileageEstimated);
    }

    public void updateMileage(Polyline polyline1, Polyline polyline2){
        String mileageEstimated = UtilsGoogleMaps.getMileageEstimated(polyline1, polyline2);
        mileageView.setText(mileageEstimated);
    }

    public void updateTimeEstimation(Polyline polyline){
        String timeEstimated = UtilsGoogleMaps.getTimeRoute(UtilsGoogleMaps.getMileageRoute(polyline));
        timeView.setText(timeEstimated);
    }

    public void updateTimeEstimation(Polyline polyline1, Polyline polyline2){
        String timeEstimated = UtilsGoogleMaps.getTimeRoute(UtilsGoogleMaps.getMileageRoute(polyline1) +
                UtilsGoogleMaps.getMileageRoute(polyline2));
        timeView.setText(timeEstimated);
    }

    public ImageButton getButtonAddSegment() {
        return buttonAddSegment;
    }

    public ImageButton getButtonDelete() {
        return buttonDelete;
    }
}
