package com.g.laurent.backtobike.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.RouteSegmentContentProvider;
import com.g.laurent.backtobike.Models.RoutesContentProvider;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;


public class RouteHandler {

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

    public static int insertNewRoute(Context context, Route route){

        // Insert route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(route));

        // Recover idRoute just created
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);

        // Add route segments to database
        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);
        if(route.getListRouteSegment()!=null){
            if(route.getListRouteSegment().size()>0){
                for(RouteSegment segment : route.getListRouteSegment()){
                    segment.setIdRoute(idRoute);
                    routeSegmentContentProvider.insert(RouteSegmentContentProvider.URI_ITEM, RouteSegment.createContentValuesFromRouteSegmentInsert(segment));
                }
            }
        }

        return idRoute;
    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- UPDATE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void updateRoute(Context context, Route route){

        // Update route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        Uri uriUpdate = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, route.getId());
        routesContentProvider.update(uriUpdate,Route.createContentValuesFromRouteUpdate(route),null,null);

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);

        // Delete routes segments related to this idRoute
        Uri uriDelete = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, route.getId());
        routeSegmentContentProvider.delete(uriDelete,null,null);

        // Add route segments to database
        if(route.getListRouteSegment()!=null){
            if(route.getListRouteSegment().size()>0){
                for(RouteSegment segment : route.getListRouteSegment()){
                    segment.setIdRoute(route.getId());
                    routeSegmentContentProvider.insert(RouteSegmentContentProvider.URI_ITEM, RouteSegment.createContentValuesFromRouteSegmentInsert(segment));
                }
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

    public static List<Route> getAllRoutes(Context context){

        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context);

        final Cursor cursor = AppDatabase.getInstance(context).routesDao().getAllRoutes();

        return Route.getListRoutesFromCursor(cursor);
    }

    public static List<RouteSegment> getRouteSegments(Context context,int idRoute){

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context);

        Uri uriQuery = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, idRoute);
        final Cursor cursor = routeSegmentContentProvider.query(uriQuery, null, null, null, null);

        return RouteSegment.getRouteSegmentFromCursor(cursor);
    }
}

