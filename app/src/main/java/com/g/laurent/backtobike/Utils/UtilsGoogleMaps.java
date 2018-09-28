package com.g.laurent.backtobike.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UtilsGoogleMaps {


    public static double getMileageRoute(List<Polyline> route){

        double mileage = 0;

        if(route!=null){
            if(route.size()>0){

                for(Polyline segment : route){

                    Location lastPoint = new Location("last_location");
                    lastPoint.setLatitude(segment.getPoints().get(0).latitude);
                    lastPoint.setLongitude(segment.getPoints().get(0).longitude);

                    Location nextPoint = new Location("next_location");
                    nextPoint.setLatitude(segment.getPoints().get(1).latitude);
                    nextPoint.setLongitude(segment.getPoints().get(1).longitude);

                    mileage = mileage + lastPoint.distanceTo(nextPoint);
                }
            }
        }

        return mileage;
    }

    public static String getTimeRoute(double mileage){
        Date date = new Date((long) mileage * 225); // by taking an average speed of 16km/h for a bike
        DateFormat formatter = new SimpleDateFormat("h:mm", Locale.FRANCE);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static void deletePolylineInTheList(Polyline polyline, List<Polyline> route){

        int index = 0;

        if(route!=null){
            if(route.size()>0){
                for(Polyline line : route){
                    if(line.getTag()!=null){
                        if(line.getTag().equals(polyline.getTag())){ // if it's the line to be removed, delete it in the list.
                            route.remove(index);
                            break;
                        }
                    }
                    index++;
                }
            }
        }
    }
}
