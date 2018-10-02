package com.g.laurent.backtobike.Utils;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;


public class SegmentsHandler {

    private GraphicsHandler graphicsHandler;
    private GoogleMap map;
    private int index1;
    private int index2;
    private int routeNumber;

    public SegmentsHandler(GraphicsHandler graphicsHandler, GoogleMap map) {
        this.graphicsHandler = graphicsHandler;
        this.map=map;
    }

    public void handleClickSegmentToDelete(LatLng fingerPosition) {

        if(graphicsHandler.getRouteAlt()!=null){ // ----------------- if a segment has already been deleted

            // find nearest point on route and delete it
            if(UtilsGoogleMaps.isNearestPointOnMainRoute(fingerPosition,graphicsHandler.getRoute(),graphicsHandler.getRouteAlt(),map)){
                if(graphicsHandler.getRoute().size()>=2)
                    graphicsHandler.getRoute().remove(graphicsHandler.getRoute().size()-1); // remove last point of main route
            } else {
                if(graphicsHandler.getRouteAlt().size()>=2)
                    graphicsHandler.getRouteAlt().remove(0); // remove first point of routeAlt
            }

            // draw the 2 polylines
            graphicsHandler.draw2PolyLines();

            // set new last point
            graphicsHandler.setLastPoint(graphicsHandler.getRoute().get(graphicsHandler.getRoute().size()-1));

        } else { // ----------------------------------------- if it's the first segment to be deleted

            if(graphicsHandler.getRoute().size()>3){ // there should be at least 3 segments in the route

                findSegmentToDelete(fingerPosition,graphicsHandler.getRoute(),map);

                if(index1!=-1 && index2!=-1){ // if a segment has been found

                    graphicsHandler.setRouteAlt(new ArrayList<LatLng>());

                    // divide the polyline in 2
                    divideRoutes(index1,index2);

                    // draw the 2 polylines
                    graphicsHandler.draw2PolyLines();

                    // set new last point
                    graphicsHandler.setLastPoint(graphicsHandler.getRoute().get(index1));

                    // Enable button Add segment
                    if(graphicsHandler.getConfig()!=null){ // if a segment has been deleted, enable button add segment
                        graphicsHandler.getConfig().getButtonAddSegment().setEnabled(true);
                        graphicsHandler.setButtonPressed(graphicsHandler.getConfig().getButtonDelete()); // update button state
                    }
                }
            }
        }
    }

    private void findSegmentToDelete(LatLng fingerPoint, List<LatLng> route, GoogleMap map){

        index1 = -1;
        index2 = -1;
        double distanceMin = 999999;
        double distanceTest;

        if(route.size()>1){ // if at least one segment
            for(int i = 0;i<=route.size()-2;i++){

                // get the orthogonal projection distance of finger position to each polyline segment
                distanceTest = PolyUtil.distanceToLine(fingerPoint,route.get(i),route.get(i+1));

                if( distanceTest < distanceMin && distanceTest < 10){
                    index1 = i;
                    index2 = i+1;
                    routeNumber = 1; // routeNumber = 1 means " main route "
                    distanceMin = distanceTest;
                }
            }
        }
    }

    private void divideRoutes(int index1, int index2){

        List<LatLng> route1 = new ArrayList<>();
        List<LatLng> route2 = new ArrayList<>();

        for(int i = 0; i<= index1; i++){
            route1.add(graphicsHandler.getRoute().get(i));
        }

        for(int i = index2; i < graphicsHandler.getRoute().size(); i++){
            route2.add(graphicsHandler.getRoute().get(i));
        }

        graphicsHandler.setRoute(route1);
        graphicsHandler.setRouteAlt(route2);
    }

    public void closeRouteAlt() {

        List<LatLng> route1 = new ArrayList<>();
        route1.addAll(graphicsHandler.getRoute());
        route1.addAll(graphicsHandler.getRouteAlt());

        graphicsHandler.setRoute(route1);
        graphicsHandler.setRouteAlt(null);

        graphicsHandler.draw2PolyLines();


        graphicsHandler.setStop(1);
        graphicsHandler.setLastPoint(route1.get(route1.size()-1));
    }
}
/*

                // find nearest point to latlng
                int index1 = UtilsGoogleMaps.findIndexNearestPolyLinePoint(latLng,graphicsHandler.getRoute(),graphicsHandler.getHasEndPoint(),map);



                    // find the other extremity of the segment to be deleted
                    int index2 = UtilsGoogleMaps.findSecondNearestPointOnPolyLine(latLng,index1,graphicsHandler.getRoute(),map);

                    if(index2!=-1){
                        // divide the polyline in 2
                        divideRoutes(index1,index2);

                        // draw the 2 polylines
                        graphicsHandler.draw2PolyLines();

                        // set new last point
                        if(index1<index2)
                            graphicsHandler.setLastPoint(graphicsHandler.getRoute().get(index1));
                        else
                            graphicsHandler.setLastPoint(graphicsHandler.getRoute().get(index2));
                    }


 */