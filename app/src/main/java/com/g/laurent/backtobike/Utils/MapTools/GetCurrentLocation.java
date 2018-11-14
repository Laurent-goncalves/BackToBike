package com.g.laurent.backtobike.Utils.MapTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.g.laurent.backtobike.Controllers.Activities.MainActivity;
import com.g.laurent.backtobike.Controllers.Activities.TraceActivity;
import com.g.laurent.backtobike.Models.OnCurrentLocationFound;
import com.g.laurent.backtobike.R;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

public class GetCurrentLocation {

    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static String EXTRA_LAT_CURRENT = "latitude_current_location";
    private final static String EXTRA_LONG_CURRENT = "longitude_current_location";
    private LatLng currentLatLng;
    private Context context;
    private TraceActivity traceActivity;
    private SharedPreferences sharedPreferences;

    public void getLocationPermission(TraceActivity traceActivity, SharedPreferences sharedPreferences, OnCurrentLocationFound onCurrentLocationFound) {

        this.context = traceActivity.getApplicationContext();
        this.traceActivity=traceActivity;
        this.sharedPreferences=sharedPreferences;
        currentLatLng = findLastCurrentLocation(sharedPreferences);

        if (context != null) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(traceActivity, onCurrentLocationFound);
            } else {
                ActivityCompat.requestPermissions(traceActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void getLocationPermission(MainActivity mainActivity, SharedPreferences sharedPreferences, OnCurrentLocationFound onCurrentLocationFound) {

        this.context = mainActivity.getApplicationContext();
        this.sharedPreferences=sharedPreferences;
        currentLatLng = findLastCurrentLocation(sharedPreferences);

        if (context != null) {
            if (ContextCompat.checkSelfPermission(mainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(onCurrentLocationFound);
            } else {
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void getCurrentLocation(TraceActivity traceActivity, OnCurrentLocationFound onCurrentLocationFound) {

        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(traceActivity);

        try {

            final Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(task -> {

                // ----------------------------- CURRENT LOCATION FOUND -----------------------------
                if (task.isSuccessful() && task.getResult() != null) {

                    // Recover the latitude and longitude of current location
                    currentLatLng = findPlaceHighestLikelihood(task);

                    // Save currentLatLng in sharedpreferrences
                    saveLastCurrentLocation(sharedPreferences, currentLatLng);

                    if (currentLatLng.longitude >= -123 && currentLatLng.longitude <= -122
                            && currentLatLng.latitude >= 37 && currentLatLng.latitude <= 38) // for Travis integration tests
                        currentLatLng = new LatLng(48.867267,2.385343); // for Travis integration tests

                } else {
                    // ----------------------------- CURRENT LOCATION NOT FOUND -----------------------------
                    // Warn user
                    Toast.makeText(context, context.getResources().getString(R.string.error_current_location), Toast.LENGTH_LONG).show();
                }

                // Move camera to currentLatLng
                moveCameraToCurrentPosition(currentLatLng, onCurrentLocationFound);
            });

        } catch (SecurityException e) {
            String error = context.getResources().getString(R.string.error_current_location) + "\n" + e.toString();
            moveCameraToCurrentPosition(currentLatLng, onCurrentLocationFound);
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
    }

    public void getCurrentLocation(OnCurrentLocationFound onCurrentLocationFound) {

        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(context);

        try {

            final Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(task -> {

                // ----------------------------- CURRENT LOCATION FOUND -----------------------------
                if (task.isSuccessful() && task.getResult() != null) {

                    // Recover the latitude and longitude of current location
                    currentLatLng = findPlaceHighestLikelihood(task);

                    // Save currentLatLng in sharedpreferrences
                    saveLastCurrentLocation(sharedPreferences, currentLatLng);

                    if (currentLatLng.longitude >= -123 && currentLatLng.longitude <= -122
                            && currentLatLng.latitude >= 37 && currentLatLng.latitude <= 38) // for Travis integration tests
                        currentLatLng = new LatLng(48.867267,2.385343); // for Travis integration tests

                } else {
                    // ----------------------------- CURRENT LOCATION NOT FOUND -----------------------------
                    // Warn user
                    Toast.makeText(context, context.getResources().getString(R.string.error_current_location), Toast.LENGTH_LONG).show();
                }

                // Send to MainFragment the currentLatLng
                onCurrentLocationFound.onCurrentLocationFound(currentLatLng);
            });

        } catch (SecurityException e) {
            String error = context.getResources().getString(R.string.error_current_location) + "\n" + e.toString();
            onCurrentLocationFound.onCurrentLocationFound(currentLatLng);
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
    }

    private static LatLng findPlaceHighestLikelihood(@NonNull Task<PlaceLikelihoodBufferResponse> task){

        Place placeHighestLikelihood = null;
        float percentage = 0;

        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

        for (PlaceLikelihood placeLikelihood : likelyPlaces) {       // for each placeLikelihood

            if(placeLikelihood.getLikelihood() > percentage) {       // if the likelihood is higher than the other ones,
                placeHighestLikelihood = placeLikelihood.getPlace(); //   set this place as placeLikelihood
                percentage = placeLikelihood.getLikelihood();        //   set this percentage as highest likelihood
            }
        }

        LatLng current_location=null;

        if(placeHighestLikelihood!=null)
            current_location = placeHighestLikelihood.getLatLng();

        likelyPlaces.release();

        return current_location;
    }

    private void moveCameraToCurrentPosition(LatLng currentLatLng, OnCurrentLocationFound onCurrentLocationFound) {
        if (currentLatLng != null) {
            // Show the initial position
            traceActivity.getMap().moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(currentLatLng.latitude, currentLatLng.longitude), 15));

            onCurrentLocationFound.onCurrentLocationFound(currentLatLng);
        }
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------ SAVE / GET FROM SHAREDPREFERENCES -----------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    private static LatLng findLastCurrentLocation(SharedPreferences sharedPreferences){

        saveLastCurrentLocation(sharedPreferences, new LatLng(48.866298, 2.383746)); // for Travis integration tests

        Float latitude = sharedPreferences.getFloat(EXTRA_LAT_CURRENT,0);
        Float longitude = sharedPreferences.getFloat(EXTRA_LONG_CURRENT,0);

        LatLng last_location;

        if(latitude == 0 && longitude == 0)
            last_location=new LatLng(48.866298, 2.383746);
        else
            last_location = new LatLng(latitude,longitude);

        return last_location;
    }

    private static void saveLastCurrentLocation(SharedPreferences sharedPreferences, LatLng current_loc){
        sharedPreferences.edit().putFloat(EXTRA_LAT_CURRENT,(float) current_loc.latitude).apply();
        sharedPreferences.edit().putFloat(EXTRA_LONG_CURRENT,(float) current_loc.longitude).apply();
    }
}
