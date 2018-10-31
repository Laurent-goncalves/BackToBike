package com.g.laurent.backtobike.Utils.Configurations;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.MapTools.UtilsGoogleMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.List;


public class ConfigureMap implements OnMapReadyCallback {

    private MapView mapView;
    private TextView titleView;
    private TextView mileageView;
    private TextView timeView;
    private Button buttonAddRoute;
    private Context context;
    private GoogleMap googleMap;
    private Route route;


    public ConfigureMap(Context context, View view) {
        this.context = context;

        mapView = view.findViewById(R.id.map);
        mileageView = view.findViewById(R.id.mileage_estimation);
        timeView = view.findViewById(R.id.time_estimation);
        titleView = view.findViewById(R.id.title_route);
        buttonAddRoute= view.findViewById(R.id.button_add_my_routes);
    }

    public void configureMapLayout(Route route){
        this.route=route;

        if(route!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        List<LatLng> listPoints = UtilsGoogleMaps.extractListPointsFromListRouteSegments(route.getListRouteSegment());

        if(listPoints.size()>0){
            setTitleMap(route.getName());
            drawSegments(listPoints);
            drawMarkers(listPoints.get(0), listPoints.get(listPoints.size()-1));
            displayEstimationTimeAndMileage(listPoints);
            scaleMap(listPoints);
        }
    }

    private void setTitleMap(String title){
        if(title!=null) {
            if (title.length() <= 50)
                titleView.setText(title);
            else
                titleView.setText(title.substring(0, 50));
        }
    }

    private void drawSegments(List<LatLng> listPoints) {

        PolylineOptions rectOptions = new PolylineOptions()
                .width(8)
                .color(context.getResources().getColor(R.color.colorPolylineComplete))
                .addAll(listPoints);

        googleMap.addPolyline(rectOptions);
    }

    private void drawMarkers(LatLng start, LatLng end){

        //  START POINT
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_green_36))
                .position(start).draggable(false));

        //  END POINT
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_flag_green_48))
                .anchor(0.3f,0.85f)
                .position(end).draggable(false));
    }

    private void displayEstimationTimeAndMileage(List<LatLng> listPoints){

        double mileage = UtilsGoogleMaps.getMileageRoute(listPoints);

        if(mileage>0){
            String mileageText = UtilsGoogleMaps.getMileageEstimated(listPoints);
            String timeText = UtilsGoogleMaps.getTimeRoute(mileage);

            mileageView.setText(mileageText);
            timeView.setText(timeText);
        }
    }
    private void scaleMap(List<LatLng> listPoints){
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for(LatLng point : listPoints)
            bounds.include(point);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
    }

    public void configureButtonAddToMyRoutes(Context context, String userId, BikeEvent bikeEvent) {

        if(bikeEvent.getRoute()!=null) {
            if (routeNotInDatabase(context, userId, bikeEvent.getRoute())) {
                buttonAddRoute.setVisibility(View.VISIBLE);
                buttonAddRoute.setOnClickListener(v -> {
                    Action.addInvitRouteToMyRoutes(bikeEvent, userId, context);
                    buttonAddRoute.setVisibility(View.INVISIBLE);
                });
            }
        }
    }

    private Boolean routeNotInDatabase(Context context, String userId, Route route){

        Boolean answer = true;

        List<Route> listRouteDatabase = RouteHandler.getAllRoutes(context, userId);

        if(listRouteDatabase!=null){
            if(listRouteDatabase.size()>0){
                for(Route routeDB : listRouteDatabase){
                    if(UtilsApp.areRoutesEquals(routeDB, route)) {
                        answer = false;
                        break;
                    }
                }
                return answer;
            } else
                return true;
        } else
            return true;
    }
}
