package com.g.laurent.backtobike.Controllers.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.ConfigureTraceActivity;
import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class TraceActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean deleteMode;
    private List<Polyline> route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);
        toolbarManager.configureToolbar(this, MENU_TRACE_ROUTE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        route = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new ConfigureTraceActivity(findViewById(R.id.view_trace_activity),this,mMap);
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------------GETTERS AND SETTERS -----------------------------------------
    // ---------------------------------------------------------------------------------------------------

    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap map) {
        mMap = map;
    }

    public Boolean getDeleteMode() {
        return deleteMode;
    }

    public void setDeleteMode(Boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public List<Polyline> getRoute() {
        return route;
    }

    public void setRoute(List<Polyline> route) {
        this.route = route;
    }
}
