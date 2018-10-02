package com.g.laurent.backtobike.Utils;

import android.graphics.Point;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class UtilsGoogleMaps {

    private static final String TAG_ROUTE_POINT = "tag_route_point";

    public static int findIndexNearestPolyLinePoint(LatLng pointSelect, Polyline route){

        int index=-1;

        Location selectPoint = new Location("location_selected");
        selectPoint.setLatitude(pointSelect.latitude);
        selectPoint.setLongitude(pointSelect.longitude);

        double distance = 9999999;

        if(route!=null){
            if(route.getPoints()!=null){
                if(route.getPoints().size()>=2) {

                    List<LatLng> listPoints = route.getPoints();

                    for (int i = 0; i < listPoints.size(); i++) {

                        Location testPoint = new Location("test_location");
                        testPoint.setLatitude(listPoints.get(i).latitude);
                        testPoint.setLongitude(listPoints.get(i).longitude);

                        if (distance == 9999999) {
                            distance = testPoint.distanceTo(selectPoint);
                            index = i;
                        }

                        if (testPoint.distanceTo(selectPoint) < distance) {
                            distance = testPoint.distanceTo(selectPoint);
                            index = i;
                        }
                    }
                }
            }
        }

        return index;
    }

    public static int findIndexNearestPolyLinePoint(LatLng pointSelect, List<LatLng> points, Boolean hasStartPoint, Boolean hasEndPoint, GoogleMap map){

        int index=-1;
        int limitInf;
        int limitSup;

        if(hasEndPoint)
            limitSup = points.size()-1;
        else
            limitSup = points.size();

        if(hasStartPoint)
            limitInf = 1;
        else
            limitInf = 0;

        Location selectPoint = new Location("location_selected");
        selectPoint.setLatitude(pointSelect.latitude);
        selectPoint.setLongitude(pointSelect.longitude);

        double distance = 9999999;

        if(points.size()>=2) {

            for (int i = limitInf; i < limitSup; i++) {

                Location testPoint = new Location("test_location");
                testPoint.setLatitude(points.get(i).latitude);
                testPoint.setLongitude(points.get(i).longitude);

                if (testPoint.distanceTo(selectPoint) < distance && isDistanceToFingerOK(pointSelect,points.get(i),map)) {
                    distance = testPoint.distanceTo(selectPoint);
                    index = i;
                }
            }
        }
        return index;
    }

    public static Boolean isNearestPointOnMainRoute(LatLng latLng, List<LatLng> route, List<LatLng> routeAlt, GoogleMap map) {

        Location selectPoint = new Location("select_location");
        selectPoint.setLatitude(latLng.latitude);
        selectPoint.setLongitude(latLng.longitude);

        Location infPoint = new Location("inf_point_location");
        infPoint.setLatitude(route.get(route.size()-1).latitude);
        infPoint.setLongitude(route.get(route.size()-1).longitude);

        Location supPoint = new Location("sup_point_location");
        supPoint.setLatitude(routeAlt.get(0).latitude);
        supPoint.setLongitude(routeAlt.get(0).longitude);

        return selectPoint.distanceTo(infPoint) < selectPoint.distanceTo(supPoint);
    }

    public static void removeRouteMarker(List<Marker> markers){
        for(Marker mark : markers){
            if(mark.getTag()!=null) {
                if (mark.getTag().equals(TAG_ROUTE_POINT))
                    mark.remove();
            }
        }
    }

    public static Boolean isDistanceToFingerOK(LatLng refPoint, LatLng testPoint, GoogleMap map){

        Projection projection = map.getProjection();

        Point screenRefPosition = projection.toScreenLocation(refPoint);
        Point screenTestPosition = projection.toScreenLocation(testPoint);

        return Math.sqrt(Math.pow(screenRefPosition.x - screenTestPosition.x, 2) + Math.pow(screenRefPosition.y - screenTestPosition.y, 2)) < 40;
    }

    public static double getMileageRoute(Polyline route){

        double mileage = 0;

        if(route!=null) {
            if (route.getPoints() != null) {
                if (route.getPoints().size() >= 2) { // at least 2 points

                    List<LatLng> listPoints = route.getPoints();

                    for (int i = 0; i < listPoints.size() - 1; i++) {

                        Location lastPoint = new Location("last_location");
                        lastPoint.setLatitude(listPoints.get(i).latitude);
                        lastPoint.setLongitude(listPoints.get(i).longitude);

                        Location nextPoint = new Location("next_location");
                        nextPoint.setLatitude(listPoints.get(i + 1).latitude);
                        nextPoint.setLongitude(listPoints.get(i + 1).longitude);

                        mileage = mileage + lastPoint.distanceTo(nextPoint);
                    }
                }
            }
        }
        return mileage;
    }

    public static String getMileageEstimated(Polyline polyline){

        DecimalFormat df;
        Double distance = UtilsGoogleMaps.getMileageRoute(polyline);
        String mileage;

        if(distance < 1000) {
            df = new DecimalFormat("#");
            mileage = df.format(distance) + " m";
        } else {
            distance = distance / 1000;
            df = new DecimalFormat("#.#");
            mileage = df.format(distance) + " km";
        }

        return mileage;
    }

    public static String getMileageEstimated(Polyline polyline1, Polyline polyline2){

        DecimalFormat df;
        Double distance = UtilsGoogleMaps.getMileageRoute(polyline1) + UtilsGoogleMaps.getMileageRoute(polyline2);
        String mileage;

        if(distance < 1000) {
            df = new DecimalFormat("#");
            mileage = df.format(distance) + " m";
        } else {
            distance = distance / 1000;
            df = new DecimalFormat("#.#");
            mileage = df.format(distance) + " km";
        }

        return mileage;
    }

    public static String getTimeRoute(double mileage){
        Date date = new Date((long) mileage * 225); // by taking an average speed of 16km/h for a bike ---- date gives a time in milliseconds

        if((long) mileage * 225 < 3600000) { // if the estimated time is below 1 hour, set the time in minutes, else in hour
            DecimalFormat df = new DecimalFormat("#");

            return df.format(mileage * 225 / 60000) + " min";
        } else {
            DateFormat formatter = new SimpleDateFormat("h:mm", Locale.FRANCE);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(date);
        }
    }
}
