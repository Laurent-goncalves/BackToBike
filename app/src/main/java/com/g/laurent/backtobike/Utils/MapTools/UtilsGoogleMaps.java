package com.g.laurent.backtobike.Utils.MapTools;

import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class UtilsGoogleMaps {

    // ----------------------------------------------------------------------------------------------------------
    // ----------------------------------- FIND NEAREST POINTS --------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public static String getCityWithLatLng(Context context, LatLng latLng) throws IOException {

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if (addresses.size() > 0)
            return addresses.get(0).getLocality();
        else
            return null;
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

    public static Boolean isNearestPointOnMainRoute(LatLng latLng, List<LatLng> route, List<LatLng> routeAlt) {

        Location selectPoint = new Location("select_location");
        selectPoint.setLatitude(latLng.latitude);
        selectPoint.setLongitude(latLng.longitude);

        Location infPoint = new Location("inf_point_location");
        Location supPoint = new Location("sup_point_location");

        if(route.size()!=0) {
            infPoint.setLatitude(route.get(route.size() - 1).latitude);
            infPoint.setLongitude(route.get(route.size() - 1).longitude);
        } else {
            infPoint.setLatitude(0);
            infPoint.setLongitude(0);
        }

        if(routeAlt.size()!=0) {
            supPoint.setLatitude(routeAlt.get(0).latitude);
            supPoint.setLongitude(routeAlt.get(0).longitude);
        } else {
            supPoint.setLatitude(0);
            supPoint.setLongitude(0);
        }

        return selectPoint.distanceTo(infPoint) < selectPoint.distanceTo(supPoint);
    }

    // ----------------------------------------------------------------------------------------------------------
    // --------------------------------------- BOOLEANS ---------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public static Boolean isMarkerADragPoint(Marker marker){

        if(marker.getTag()!=null)
            return extractRouteFromTag(marker.getTag().toString())==1 || extractRouteFromTag(marker.getTag().toString())==2;
        else
            return false;
    }

    public static Boolean isRouteFinished(MarkersHandler markersHandler, List<LatLng> routeAlt){

        Boolean hasStartPoint = (markersHandler.getStartPoint()!=null);
        Boolean hasEndPoint = (markersHandler.getEndPoint()!=null);
        Boolean isRouteFinished = (routeAlt==null);

        return hasStartPoint && hasEndPoint && isRouteFinished;
    }

    public static Boolean isDistanceToFingerOK(LatLng refPoint, LatLng testPoint, GoogleMap map){

        Projection projection = map.getProjection();

        Point screenRefPosition = projection.toScreenLocation(refPoint);
        Point screenTestPosition = projection.toScreenLocation(testPoint);

        return Math.sqrt(Math.pow(screenRefPosition.x - screenTestPosition.x, 2) + Math.pow(screenRefPosition.y - screenTestPosition.y, 2)) < 40;
    }

    public static Boolean routeNotInDatabase(Context context, String userId, Route route){

        Boolean answer = true;

        List<Route> listRouteDatabase = RouteHandler.getMyRoutes(context, userId);

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

    public static Boolean isMarkerAtEndOfRoute(Marker marker, List<LatLng> route){
        if(route!=null){
            if(route.size()>0){
                return marker.getPosition().equals(route.get(route.size()-1));
            } else
                return false;
        } else
            return false;
    }

    public static Boolean isMarkerAtBegOfRouteAlt(Marker marker, List<LatLng> routeAlt){
        if(routeAlt!=null){
            if(routeAlt.size()>0){
                return marker.getPosition().equals(routeAlt.get(0));
            } else
                return false;
        } else
            return false;
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------- CALCULATION TIME & MILEAGE -----------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public static double getMileageRoute(List<LatLng> route){

        double mileage = 0;

        if(route!=null) {
            if (route.size() >= 2) { // at least 2 points

                for (int i = 0; i < route.size()-1; i++) {

                    Location lastPoint = new Location("last_location");
                    lastPoint.setLatitude(route.get(i).latitude);
                    lastPoint.setLongitude(route.get(i).longitude);

                    Location nextPoint = new Location("next_location");
                    nextPoint.setLatitude(route.get(i + 1).latitude);
                    nextPoint.setLongitude(route.get(i + 1).longitude);

                    mileage = mileage + lastPoint.distanceTo(nextPoint);
                }
            }
        }
        return mileage;
    }

    public static String getMileageEstimated(List<LatLng> route){

        DecimalFormat df;
        Double distance = UtilsGoogleMaps.getMileageRoute(route);
        String mileage;

        if(distance < 1000) {
            df = new DecimalFormat("#");
            mileage = df.format(distance) + " m";
        } else {
            distance = distance / 1000;

            if(distance>9999){
                mileage = "+9999km";
            } else {
                df = new DecimalFormat("#.#");
                mileage = df.format(distance) + " km";
            }
        }

        return mileage;
    }

    public static String getMileageEstimated(List<LatLng> route1, List<LatLng> route2){

        DecimalFormat df;
        Double distance = UtilsGoogleMaps.getMileageRoute(route1) + UtilsGoogleMaps.getMileageRoute(route2);
        String mileage;

        if(distance < 1000) {
            df = new DecimalFormat("#");
            mileage = df.format(distance) + " m";
        } else {
            distance = distance / 1000;

            if(distance>9999){
                mileage = "+9999km";
            } else {
                df = new DecimalFormat("#.#");
                mileage = df.format(distance) + " km";
            }
        }

        return mileage;
    }

    public static String getTimeRoute(double mileage){
        Date date = new Date((long) mileage * 225); // by taking an average speed of 16km/h for a bike ---- date gives a time in milliseconds

        if((long) mileage * 225 < 3600000) { // if the estimated time is below 1 hour, set the time in minutes
            DecimalFormat df = new DecimalFormat("#");
            return df.format(mileage * 225 / 60000) + " min";

        } else if((long) mileage * 225 < 24 * 3600000) { // if the estimated time is below 24 hours, set the time in hours
            DateFormat formatter = new SimpleDateFormat("hh:mm", Locale.FRANCE);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(date);

        } else if((long) mileage * 225 < 7 * 24 * 3600000) {
            return getTimeInDays((long) mileage * 225);
        } else {
            return "+7days";
        }
    }

    public static String getTimeInDays(long time){
        int daysLong = (int) Math.ceil((double) time / (24 * 3600000));
        return String.valueOf(daysLong) + " days";
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------- TRANSFORMATION & EXTRACT -------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public static List<RouteSegment> transformListPointsToListRouteSegments(List<LatLng> listPoints){

        List<RouteSegment> listRouteSegments = new ArrayList<>();

        if(listPoints!=null){
            if(listPoints.size()>0){
                for(int i = 0; i<listPoints.size();i++){
                    listRouteSegments.add(new RouteSegment(0,i+1,listPoints.get(i).latitude,
                            listPoints.get(i).longitude,0));

                }
            }
        }

        return listRouteSegments;
    }

    public static List<LatLng> transformListRouteSegmentsToListPoints(List<RouteSegment> listRouteSegments){

        List<LatLng> listPoints = new ArrayList<>();

        if(listRouteSegments!=null){
            if(listRouteSegments.size()>0){
                for(int i = 0; i<listRouteSegments.size();i++){
                    listPoints.add(new LatLng(listRouteSegments.get(i).getLat(),
                            listRouteSegments.get(i).getLng()));
                }
            }
        }

        return listPoints;
    }

    public static List<LatLng> extractListPointsFromListRouteSegments(List<RouteSegment> listRouteSegments){

        List<LatLng> listPoints = new ArrayList<>();
        if(listRouteSegments!=null){
            if(listRouteSegments.size()>0){
                for(RouteSegment segment : listRouteSegments){
                    listPoints.add(new LatLng(segment.getLat(),segment.getLng()));
                }
            }
        }

        return listPoints;
    }

    public static int extractRouteFromTag(String tag){

        int answer = -1;

        if(tag!=null){
            if(tag.contains("ROUTE-"))
                return 1;
            else if (tag.contains("ROUTEALT-"))
                return 2;
        }

        return answer;
    }

    public static int extractIndexFromTag(String tag){

        int answer = -1;

        if(tag!=null){

            if(extractRouteFromTag(tag)==1){
                return Integer.parseInt(tag.substring(6, tag.length()));
            } else if(extractRouteFromTag(tag)==2){
                return Integer.parseInt(tag.substring(9, tag.length()));
            }
        }

        return answer;
    }
}
