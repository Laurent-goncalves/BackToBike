package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
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
import butterknife.BindView;
import butterknife.ButterKnife;


public class ConfigureMap implements OnMapReadyCallback {

    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.layout_time_mileage) RelativeLayout timeMileageLayout;
    @BindView(R.id.mileage_estimation) TextView mileageView;
    @BindView(R.id.time_estimation) TextView timeView;
    @BindView(R.id.placeholder_map) View mapPlaceholder;
    private Context context;
    private GoogleMap googleMap;
    private List<RouteSegment> listRouteSegments;


    public ConfigureMap(Context context,View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    public void configureMapLayout(Route route){
        listRouteSegments = RouteHandler.getRouteSegments(context, route.getId());
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        List<LatLng> listPoints = UtilsGoogleMaps.extractListPointsFromListRouteSegments(listRouteSegments);

        if(listPoints.size()>0){
            drawSegments(listPoints);
            drawMarkers(listPoints.get(0), listPoints.get(listPoints.size()-1));
            displayEstimationTimeAndMileage(listPoints);
            scaleMap(listPoints);
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
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }
}
