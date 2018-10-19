package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.OnCurrentLocationFound;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.ConfigureTraceActivity;
import com.g.laurent.backtobike.Utils.GetCurrentLocation;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.internal.Utils;


public class TraceActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String SHAREDPREFERENCES = "MAPSPREFERRENCES";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private Boolean deleteMode;
    private List<Polyline> route;
    private String userId;
    private GetCurrentLocation getCurrentLocation;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);

        sharedPreferences = getSharedPreferences(SHAREDPREFERENCES,MODE_PRIVATE);
        toolbarManager.configureToolbar(this, MENU_TRACE_ROUTE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userId = FirebaseAuth.getInstance().getUid();
        route = new ArrayList<>();
    }

    // ---------------------------------------------------------------------------------------------------
    // -------------------------------------- CONFIGURATION MAP ------------------------------------------
    // ---------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation = new GetCurrentLocation();
        getCurrentLocation.getLocationPermission(this,sharedPreferences,onCurrentLocationFound);
    }

    OnCurrentLocationFound onCurrentLocationFound = currentLocation -> configureTraceActivity();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation = new GetCurrentLocation();
                    getCurrentLocation.getLocationPermission(this,sharedPreferences, onCurrentLocationFound);
                } else {
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.give_permission),Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void configureTraceActivity(){
        new ConfigureTraceActivity(findViewById(R.id.view_trace_activity),this,mMap);
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- ALERT DIALOG FOR ROUTE TITLE ------------------------------------
    // ---------------------------------------------------------------------------------------------------

    public void showAlertDialogAddNewRoute(List<LatLng> listPoints) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_route);

        EditText routeName = dialog.findViewById(R.id.edittext_route);

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {

            if(routeName!=null){
                if(routeName.length()>0){
                    // Create list of route segments
                    List<RouteSegment> listRouteSegments = UtilsGoogleMaps.transformListPointsToListRouteSegments(listPoints);
                    // Create route
                    Route route = new Route(0,routeName.toString(),true,listRouteSegments);
                    // Add route to Firebase and database
                    Action.addNewRoute(route, userId, getApplicationContext());
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources()
                            .getString(R.string.error_add_name_route),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),getApplicationContext().getResources()
                        .getString(R.string.error_add_name_route),Toast.LENGTH_LONG).show();
            }
        });

        // Button CANCEL
        Button dialogButtonCancel = dialog.findViewById(R.id.button_cancel);
        dialogButtonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------------GETTERS AND SETTERS -----------------------------------------
    // ---------------------------------------------------------------------------------------------------

    public String getUserId() {
        return userId;
    }

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
