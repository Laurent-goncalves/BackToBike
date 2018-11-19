package com.g.laurent.backtobike.Controllers.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.g.laurent.backtobike.Utils.Configurations.ConfigureTraceActivity;
import com.g.laurent.backtobike.Utils.MapTools.GetCurrentLocation;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.MapTools.UtilsGoogleMaps;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.MY_ROUTE_TYPE;


public class TraceActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private Route route;
    private GetCurrentLocation getCurrentLocation;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);
        assignToolbarViews();

        userId = FirebaseAuth.getInstance().getUid();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // open progressBar

        savePreviousPage(MENU_TRACE_ROUTE);

        if(userId!=null)
            defineCountersAndConfigureToolbar(MENU_TRACE_ROUTE);

        defineRouteToTrace(savedInstanceState, getIntent().getExtras());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userId!=null) {
            defineCountersAndConfigureToolbar(MENU_TRACE_ROUTE);
        }
    }

    @Override
    protected void refreshActivity(){
        defineCountersAndConfigureToolbar(MENU_TRACE_ROUTE);
    }

    private void defineRouteToTrace(Bundle savedInstanceState, Bundle extras){

        if(extras!=null || savedInstanceState!=null){
            int idRoute;

            // Get idRoute if exists
            if(extras!=null)
                idRoute = extras.getInt(BUNDLE_ROUTE_ID);
            else
                idRoute = savedInstanceState.getInt(BUNDLE_ROUTE_ID);

            if(idRoute!=-1){
                // Define route
                List<RouteSegment> listRouteSegments = RouteHandler.getRouteSegments(getApplicationContext(),idRoute,userId);
                route = RouteHandler.getRoute(getApplicationContext(),idRoute,userId);
                route.setListRouteSegment(listRouteSegments);
                route.setTypeRoute(MY_ROUTE_TYPE);
            }

        } else {
            route = new Route();
            route.setId(-1);
            route.setTypeRoute(MY_ROUTE_TYPE);
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // -------------------------------------- CONFIGURATION MAP ------------------------------------------
    // ---------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(route.getListRouteSegment().size()>0){ // CHANGE EXISTING MAP -> zoom on route
            scaleMap(mMap);
        } else { // NEW MAP -> zoom on current location
            getCurrentLocation = new GetCurrentLocation();
            getCurrentLocation.getLocationPermission(this, sharedPref, onCurrentLocationFound);
        }
    }

    private void scaleMap(GoogleMap googleMap){

        // Create bounds for camera zooming
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        // Recover list of points to show
        List<LatLng> listPoints = UtilsGoogleMaps.transformListRouteSegmentsToListPoints(route.getListRouteSegment());

        // Define bounds to include all points
        for(LatLng point : listPoints)
            bounds.include(point);

        // Zoom on specified area
        googleMap.setOnMapLoadedCallback(() -> {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
            configureTraceActivity();
        });
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
                    getCurrentLocation.getLocationPermission(this,sharedPref, onCurrentLocationFound);
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
        dialogButtonSave.setText(getApplicationContext().getResources().getString(R.string.save));
        dialogButtonSave.setOnClickListener(v -> {

            if(routeName!=null){
                if(routeName.length()>0){
                    if(UtilsApp.areCharactersAllowed(routeName.getText().toString())){
                        // Create list of route segments
                        List<RouteSegment> listRouteSegments = UtilsGoogleMaps.transformListPointsToListRouteSegments(listPoints);
                        // Create route
                        Route routeToSave = new Route(route.getId(), routeName.getText().toString(), null, MY_ROUTE_TYPE, listRouteSegments);

                        if(route.getName()!=null){ // IF ROUTE UPDATE
                            // Update route in Firebase and database
                            Action.updateRoute(routeToSave, userId, getApplicationContext());
                            // Launch displayActivity with the updated route
                            launchDisplayActivity(DISPLAY_MY_ROUTES, String.valueOf(routeToSave.getId()));
                            // Display message to user
                            showSnackBar(this, getApplicationContext().getResources().getString(R.string.route_update_saved));

                        } else { // IF NEW ROUTE
                            // Add route to Firebase and database
                            int idRoute = Action.addNewRoute(routeToSave, userId, getApplicationContext());
                            // Launch displayActivity with this new route
                            launchDisplayActivity(DISPLAY_MY_ROUTES, String.valueOf(idRoute));
                            // Display message to user
                            showSnackBar(this, getApplicationContext().getResources().getString(R.string.new_route_saved));
                        }

                        dialog.dismiss();
                    } else
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources()
                                .getString(R.string.error_forbidden_characters3),Toast.LENGTH_LONG).show();
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

    public static void showSnackBar(TraceActivity traceActivity, String text) {
        Snackbar.make(traceActivity.findViewById(R.id.map), text, Snackbar.LENGTH_LONG).show();
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
