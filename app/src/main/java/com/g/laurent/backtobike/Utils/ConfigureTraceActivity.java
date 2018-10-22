package com.g.laurent.backtobike.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.g.laurent.backtobike.Controllers.Activities.TraceActivity;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private GoogleMap map;
    private Boolean deleteMode;
    private int index;
    private int routeNumber;
    private TraceActivity traceActivity;
    private GraphicsHandler graphicsHandler;
    private Context context;
    private View view;
    private Route initialRoute;

    public ConfigureTraceActivity(View view, TraceActivity traceActivity, Route route, final GoogleMap map) {

        this.view=view;
        this.initialRoute = route;
        this.traceActivity=traceActivity;
        this.map = map;
        context = traceActivity.getApplicationContext();

        ButterKnife.bind(this, view);

        List<LatLng> points = UtilsGoogleMaps.transformListRouteSegmentsToListPoints(route.getListRouteSegment());

        graphicsHandler = new GraphicsHandler(this,view, points, map, context);
        graphicsHandler.updateButtonsState("any");

        configureMapListeners();
        traceActivity.getProgressBar().setVisibility(View.GONE);
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
        map.setOnMarkerClickListener(marker -> {
            if(deleteMode){
                if(marker.getTag()!=null){
                    if(marker.getTag().equals(TAG_START_POINT)){
                        graphicsHandler.getRoute().remove(0);
                        graphicsHandler.getMarkersHandler().removeStartPoint();
                        handleDrawMap();
                        graphicsHandler.updateButtonsState(TAG_DELETE);
                    } else if(marker.getTag().equals(TAG_END_POINT)){
                        graphicsHandler.getRoute().remove(graphicsHandler.getRoute().size()-1);
                        graphicsHandler.getMarkersHandler().removeEndPoint();
                        handleDrawMap();
                        graphicsHandler.updateButtonsState(TAG_DELETE);
                    }
                }
            } else if(buttonAddSegment.isSelected() && graphicsHandler.getRouteAlt()!=null) {
                graphicsHandler.handleSegmentAdding(marker.getPosition());
            }
            return false;
        });

        // LISTENERS FOR CREATING / DRAGGING POINTS FROM POLYLINE AND ADD MARKERS
        map.setOnMapClickListener(latLng -> {

            //-------------------------  Add a start point on map --------------------------------------
            if (buttonAddStartPoint.isSelected()) {
                graphicsHandler.handleStartPointAdding(latLng);
            }

            //-------------------  Add a route marker on map OR add a polyline --------------------------
            if (buttonAddSegment.isSelected()) {
                graphicsHandler.handleSegmentAdding(latLng);
            }

            //-------------------------  Add an end point on map --------------------------------------
            if (buttonAddEndPoint.isSelected()) {
                graphicsHandler.handleEndPointAdding(latLng);
            }

            //----------------------------  Delete segment on map --------------------------------------
            if (buttonDelete.isSelected()) {
                graphicsHandler.handleSegmentDeleting(latLng);
            }
        });
    }

    @OnClick(R.id.button_add_segment)
    public void addSegment(){
        setButtonAsPressed(buttonAddSegment);
        graphicsHandler.drawMap(true);
    }

    @OnClick(R.id.button_add_start_point)
    public void addStartPoint(){
        setButtonAsPressed(buttonAddStartPoint);
    }

    @OnClick(R.id.button_add_end_point)
    public void addEndPoint(){
        setButtonAsPressed(buttonAddEndPoint);
    }

    @OnClick(R.id.button_delete)
    public void deleteAction(){
        setButtonAsPressed(buttonDelete);
    }

    @OnClick(R.id.button_cancel)
    public void cancel(){
        // Ask user to confirm to leave traceActivity
        traceActivity.askForConfirmationToLeaveTraceActivity();
    }

    @OnClick(R.id.button_save)
    public void save(){
        // Open dialog fragment to give name to the route
        traceActivity.showAlertDialogAddNewRoute(graphicsHandler.getRoute());
    }

    private void setButtonAsPressed(final ImageButton button){

        // set deleteMode value
        deleteMode = button.equals(buttonDelete);

        // Button remain pressed
        graphicsHandler.setButtonPressed(button);

        // Display instruction to user
        displayMessageToUser(button);

        // Draw map according to button selected
        handleDrawMap();

        // Button remain pressed
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    deleteMode = false;
                    button.setOnTouchListener(null);
                    graphicsHandler.setButtonPressed(null);
                }
                return true;
            }
        });
    }

    public void handleDrawMap(){
        if(buttonDelete.isSelected() || buttonAddSegment.isSelected())
            graphicsHandler.drawMap(true);
        else
            graphicsHandler.drawMap(false);
    }

    private void displayMessageToUser(ImageButton button){

        if(button.equals(buttonAddStartPoint)){
            Toast.makeText(context, context.getResources().getString(R.string.add_start_point_message),Toast.LENGTH_LONG).show();
        } else if(button.equals(buttonAddSegment)) {
            Toast.makeText(context, context.getResources().getString(R.string.add_segment_message),Toast.LENGTH_LONG).show();
        } else if (button.equals(buttonAddEndPoint)){
            Toast.makeText(context, context.getResources().getString(R.string.add_end_point_message),Toast.LENGTH_LONG).show();
        } else if (button.equals(buttonDelete)){
            Toast.makeText(context, context.getResources().getString(R.string.delete_message),Toast.LENGTH_LONG).show();
        }
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


}
