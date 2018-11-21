package com.g.laurent.backtobike.Utils.MapTools;

import android.content.Context;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;

public class SegmentsHandler {

    private GraphicsHandler graphicsHandler;
    private GoogleMap map;
    private int index1;
    private int index2;
    private List<LatLng> route;
    private List<LatLng> routeAlt;
    private Polyline polyline;
    private Polyline polylineAlt;
    private Context context;

    public SegmentsHandler(GraphicsHandler graphicsHandler, GoogleMap map, Context context) {
        this.graphicsHandler = graphicsHandler;
        this.map=map;
        this.context=context;
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ DELETING SEGMENTS ---------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public void handleClickSegmentToDelete(LatLng fingerPosition) {

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        if(routeAlt!=null){ // ----------------- if a segment has already been deleted

            // find nearest point on route and delete it
            if(UtilsGoogleMaps.isNearestPointOnMainRoute(fingerPosition,route,routeAlt)){
                if(route.size()>=2)
                    route.remove(route.size()-1); // remove last point of main route
            } else {
                if(routeAlt.size()>=2)
                    routeAlt.remove(0); // remove first point of routeAlt
            }

            if(routeAlt.size()==0 || (routeAlt.size()==1 && graphicsHandler.getMarkersHandler().getEndPoint()==null))
                routeAlt = null;

            if(route.size()==1)
                route = new ArrayList<>();

            graphicsHandler.setRoute(route);
            graphicsHandler.setRouteAlt(routeAlt);

        } else { // ----------------------------------------- if it's the first segment to be deleted

            if(route.size()>3){ // there should be at least 3 segments in the route

                findSegmentToDelete(fingerPosition,route);

                if(index1!=-1 && index2!=-1){ // if a segment has been found

                    graphicsHandler.setRouteAlt(new ArrayList<>());

                    // divide the polyline in 2
                    divideRoutes(index1,index2);
                }
            }
        }
    }

    private void findSegmentToDelete(LatLng fingerPoint, List<LatLng> route){

        index1 = -1;
        index2 = -1;
        double distanceMin = 999999;
        double distanceTest;

        if(route.size()>1){ // if at least one segment
            for(int i = 0;i<=route.size()-2;i++){

                // get the orthogonal projection distance of finger position to each polyline segment
                distanceTest = PolyUtil.distanceToLine(fingerPoint,route.get(i),route.get(i+1));

                if(distanceTest < distanceMin && distanceTest < 10){
                    index1 = i;
                    index2 = i+1;
                    distanceMin = distanceTest;
                }
            }
        }
    }

    private void divideRoutes(int index1, int index2){

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        List<LatLng> route1 = new ArrayList<>();
        List<LatLng> route2 = new ArrayList<>();

        for(int i = 0; i<= index1; i++){
            route1.add(route.get(i));
        }

        for(int i = index2; i < route.size(); i++){
            route2.add(route.get(i));
        }

        graphicsHandler.setRoute(route1);
        graphicsHandler.setRouteAlt(route2);
    }

    public void closeRouteAlt() {

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        List<LatLng> route1 = new ArrayList<>(route);

        if(routeAlt!=null)
            route1.addAll(routeAlt);

        graphicsHandler.setRoute(route1);
        graphicsHandler.setRouteAlt(null);
    }

    // ----------------------------------------------------------------------------------------------------------
    // ---------------------------------------- DRAW UTILS ------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    public void drawSegments(boolean routeFinished) {

        route = graphicsHandler.getRoute();
        routeAlt = graphicsHandler.getRouteAlt();

        if(polyline!=null)
            polyline.remove();
        if(polylineAlt!=null)
            polylineAlt.remove();

        if(routeFinished){

            PolylineOptions rectOptions = new PolylineOptions()
                    .width(8)
                    .color(context.getResources().getColor(R.color.colorPolylineComplete))
                    .addAll(route);

            polyline = map.addPolyline(rectOptions);

        } else {

            PolylineOptions rectOptions = new PolylineOptions()
                    .width(8)
                    .color(context.getResources().getColor(R.color.colorPolylineNotComplete))
                    .addAll(route);

            polyline = map.addPolyline(rectOptions);

            if (routeAlt != null) {
                PolylineOptions rectOptionsAlt = new PolylineOptions()
                        .width(8)
                        .color(context.getResources().getColor(R.color.colorPolylineNotComplete))
                        .addAll(routeAlt);

                polylineAlt = map.addPolyline(rectOptionsAlt);
            }
        }
    }
}
