package com.g.laurent.backtobike.Controllers.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.OnCurrentLocationFound;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.ConfigureTraceActivity;
import com.g.laurent.backtobike.Utils.GetCurrentLocation;
import com.g.laurent.backtobike.Utils.RouteHandler;
import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;



public class TraceActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String SHAREDPREFERENCES = "MAPSPREFERRENCES";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private Route route;
    private String userId;
    private GetCurrentLocation getCurrentLocation;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE); // open progressBar

        sharedPreferences = getSharedPreferences(SHAREDPREFERENCES,MODE_PRIVATE);
        toolbarManager.configureToolbar(this, MENU_TRACE_ROUTE);

        userId = FirebaseAuth.getInstance().getUid();
        defineRouteToTrace(savedInstanceState, getIntent().getExtras());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void defineRouteToTrace(Bundle savedInstanceState, Bundle extras){

        if(extras!=null || savedInstanceState!=null){
            int idRoute;

            if(extras!=null)
                idRoute = extras.getInt(BUNDLE_ROUTE_ID);
            else
                idRoute = savedInstanceState.getInt(BUNDLE_ROUTE_ID);

            if(idRoute!=-1){
                // Define route
                List<RouteSegment> listRouteSegments = RouteHandler.getRouteSegments(getApplicationContext(),idRoute,userId);
                route = RouteHandler.getRoute(getApplicationContext(),idRoute,userId);
                route.setListRouteSegment(listRouteSegments);
            }

        } else {
            route = new Route();
            route.setId(0);
        }
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
        new ConfigureTraceActivity(findViewById(R.id.view_trace_activity),this, route, mMap);
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- ALERT DIALOG FOR ROUTE TITLE ------------------------------------
    // ---------------------------------------------------------------------------------------------------

    public void showAlertDialogAddNewRoute(List<LatLng> listPoints) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_route);

        EditText routeName = dialog.findViewById(R.id.edittext_route);

        // Add route name to edittext
        if(route.getName()!=null){
            routeName.setText(route.getName());
        }

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {

            if(routeName!=null){
                if(routeName.length()>0){

                    // Create list of route segments
                    List<RouteSegment> listRouteSegments = UtilsGoogleMaps.transformListPointsToListRouteSegments(listPoints);
                    // Create route
                    Route routeToSave = new Route(route.getId(),routeName.getText().toString(),true, listRouteSegments);

                    if(route.getName()!=null){ // IF ROUTE UPDATE
                        // Update route in Firebase and database
                        Action.updateRoute(routeToSave,userId,getApplicationContext());
                        // Launch displayActivity with the updated route
                        launchDisplayActivity(DISPLAY_MY_ROUTES, String.valueOf(routeToSave.getId()));

                    } else { // IF NEW ROUTE
                        // Add route to Firebase and database
                        int idRoute = Action.addNewRoute(routeToSave, userId, getApplicationContext());
                        // Launch displayActivity with this new route
                        launchDisplayActivity(DISPLAY_MY_ROUTES, String.valueOf(idRoute));
                    }

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

    public void askForConfirmationToLeaveTraceActivity() {

        Context context = getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.warning_title));
        builder.setMessage(context.getResources().getString(R.string.leave_authorization));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm), (dialog, id) -> {

                    String idRoute = null;
                    if(route.getName()!=null){
                        idRoute = String.valueOf(route.getId());
                    }
                    launchDisplayActivity(DISPLAY_MY_ROUTES, idRoute);
                }
            )
                .setNegativeButton(R.string.cancel, (dialog, id) -> { });

        AlertDialog dialog = builder.create();
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

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
