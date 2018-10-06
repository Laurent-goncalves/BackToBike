package com.g.laurent.backtobike.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.RouteSegmentContentProvider;
import com.g.laurent.backtobike.Models.RoutesContentProvider;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;


public class RouteHandler {

    private static Route buildRoute(int idRoute, String routeTitle, Boolean valid) {
        return new Route(idRoute, routeTitle, valid);
    }

    private static List<RouteSegment> buildListRouteSegments(int idRoute, List<LatLng> listPoints) {

        List<RouteSegment> listRouteSegments = new ArrayList<>();

        if(listPoints.size()>2){ // if at least 1 segment
            for(int i = 0; i < listPoints.size(); i++){
                RouteSegment routeSegment = new RouteSegment(0, i, listPoints.get(i).latitude, listPoints.get(i).longitude, idRoute);
                listRouteSegments.add(routeSegment);
            }
        }

        return listRouteSegments;
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static int insertNewRoute(Context context, List<LatLng> listPoints, String routeTitle, Boolean valid){

        // Build new route
        Route route = buildRoute(0,routeTitle,valid);

        // Insert route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(route));

        // Recover idRoute just created
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);

        // Build list route segments
        List<RouteSegment> listRouteSegments = buildListRouteSegments(idRoute,listPoints);

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);

        // Add route segments to database
        if(listRouteSegments.size()>0){
            for(RouteSegment segment : listRouteSegments){
                segment.setIdRoute(idRoute);
                routeSegmentContentProvider.insert(RouteSegmentContentProvider.URI_ITEM, RouteSegment.createContentValuesFromRouteSegmentInsert(segment));
            }
        }

        return idRoute;
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateRoute(Context context,int idRoute, List<LatLng> listPoints, String routeTitle, Boolean valid){

        // Build new route
        Route route = buildRoute(idRoute,routeTitle,valid);

        // Update route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        Uri uriUpdate = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, idRoute);
        routesContentProvider.update(uriUpdate,Route.createContentValuesFromRouteUpdate(route),null,null);

        // Build list route segments
        List<RouteSegment> listRouteSegments = buildListRouteSegments(idRoute,listPoints);

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);

        // Delete routes segments related to this idRoute
        Uri uriDelete = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, idRoute);
        routeSegmentContentProvider.delete(uriDelete,null,null);

        // Add route segments to database
        if(listRouteSegments.size()>0){
            for(RouteSegment segment : listRouteSegments){
                segment.setIdRoute(idRoute);
                routeSegmentContentProvider.insert(RouteSegmentContentProvider.URI_ITEM, RouteSegment.createContentValuesFromRouteSegmentInsert(segment));
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static Route getRoute(Context context,int idRoute){

        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, idRoute);
        final Cursor cursor = routesContentProvider.query(uriQuery, null, null, null, null);

        return Route.getRouteFromCursor(cursor);
    }

    public static List<RouteSegment> getRouteSegments(Context context,int idRoute){

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, idRoute);
        final Cursor cursor = routeSegmentContentProvider.query(uriQuery, null, null, null, null);

        return RouteSegment.getRouteSegmentFromCursor(cursor);
    }
}

