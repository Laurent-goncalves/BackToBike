package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.g.laurent.backtobike.Controllers.TraceActivity;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    private LatLng lastPoint;
    private int counter;
    private TraceActivity traceActivity;
    private Boolean deleteMode;
    private List<Polyline> route;
    View view;

    public ConfigureTraceActivity(View view, TraceActivity traceActivity, final GoogleMap map) {
        this.view = view;
        this.map = map;
        this.traceActivity=traceActivity;
        counter = 0;
        ButterKnife.bind(this,view);
    }

    @OnClick(R.id.button_add_segment)
    public void addSegment(){

        // Button remain pressed
        setButtonPressed(buttonAddSegment);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(lastPoint!=null){
                    drawLineOnMap(lastPoint, latLng);
                }
                lastPoint = latLng;
            }
        });
    }

    @OnClick(R.id.button_add_start_point)
    public void addStartPoint(){

        // Button remain pressed
        setButtonPressed(buttonAddStartPoint);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Add a marker on map
                Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                marker.setTag(TAG_START_POINT);

                // Change lastPoint
                lastPoint = latLng;

                // Disable button
                buttonAddStartPoint.setEnabled(false);
            }
        });
    }

    @OnClick(R.id.button_add_end_point)
    public void addEndPoint(){

        // Button remain pressed
        setButtonPressed(buttonAddEndPoint);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Add a flag on map
                Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_black_24)));
                marker.setTag(TAG_END_POINT);

                // Draw the final line
                drawLineOnMap(lastPoint,latLng);

                // Change lastPoint
                lastPoint = latLng;

                // Disable button
                buttonAddEndPoint.setEnabled(false);
            }
        });
    }

    @OnClick(R.id.button_delete)
    public void deleteAction(){

        // Button remain pressed
        setButtonPressed(buttonDelete);

        // Configure a markerListener on the markers
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(deleteMode){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(TAG_START_POINT)){

                            // Remove start point
                            marker.remove();

                            // Remove polyline from start point to second point
                            // TODO : add method to remove polyline

                        } else if(marker.getTag().equals(TAG_END_POINT)){

                            // Remove end point
                            marker.remove();

                            // Remove polyline from start point to second point
                            // TODO : add method to remove polyline

                        }
                    }
                }

                return false;
            }
        });

        // Configure a clickListener on the polyline
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if(deleteMode){
                    polyline.remove();
                    UtilsGoogleMaps.deletePolylineInTheList(polyline, route);
                }
            }
        });
    }

    private void setButtonPressed(ImageButton buttonSelected){

        if(buttonSelected.equals(buttonAddSegment)){
            deleteMode = false;
            buttonAddSegment.setSelected(true);
            buttonAddStartPoint.setSelected(false);
            buttonAddEndPoint.setSelected(false);
            buttonDelete.setSelected(false);
        } else if (buttonSelected.equals(buttonAddStartPoint)){
            deleteMode = false;
            buttonAddSegment.setSelected(false);
            buttonAddStartPoint.setSelected(true);
            buttonAddEndPoint.setSelected(false);
            buttonDelete.setSelected(false);
        } else if (buttonSelected.equals(buttonAddEndPoint)){
            deleteMode = false;
            buttonAddSegment.setSelected(false);
            buttonAddStartPoint.setSelected(false);
            buttonAddEndPoint.setSelected(true);
            buttonDelete.setSelected(false);
        } else if (buttonSelected.equals(buttonDelete)){
            deleteMode = true;
            buttonAddSegment.setSelected(false);
            buttonAddStartPoint.setSelected(false);
            buttonAddEndPoint.setSelected(false);
            buttonDelete.setSelected(true);
        }

        // Desactivate on click and on marker click listeners
        map.setOnMapClickListener(null);
        map.setOnMarkerClickListener(null);
    }

    private void drawLineOnMap(LatLng start, LatLng end){

        // Instantiates a new Polyline object for the segment
        PolylineOptions rectOptions = new PolylineOptions()
                .add(start)
                .add(end);

        // Create and add the polyline to the map
        Polyline polyline = map.addPolyline(rectOptions);
        polyline.setClickable(true);
        polyline.setTag(counter);

        // Add the polyline to the list of polylines
        route.add(polyline);

        // Increment counter
        counter++;
    }
}
