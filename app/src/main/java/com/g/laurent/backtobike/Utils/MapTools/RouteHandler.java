package com.g.laurent.backtobike.Utils.MapTools;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.RouteSegmentContentProvider;
import com.g.laurent.backtobike.Models.RoutesContentProvider;
import java.util.List;


public class RouteHandler {

    public static final String MY_ROUTE_TYPE = "my_route_type";
    public static final String EVENT_ROUTE_TYPE = "event_route_type";

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- INSERT ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static int insertInMyRoute(Context context, Route route, String userId){

        // Insert route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, null, MY_ROUTE_TYPE);
        route.setTypeRoute(MY_ROUTE_TYPE);

        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(route));

        // Recover idRoute just created
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);

        // Add route segments to database
        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context, userId);
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

    public static int insertRouteEvent(Context context, Route route, String idEvent, String userId){

        // Insert route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, idEvent, EVENT_ROUTE_TYPE);
        route.setTypeRoute(EVENT_ROUTE_TYPE);

        Uri uriRouteInsert = routesContentProvider.insert(RoutesContentProvider.URI_ITEM, Route.createContentValuesFromRouteInsert(route));

        // Recover idRoute just created
        int idRoute = (int) ContentUris.parseId(uriRouteInsert);

        // Add route segments to database
        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context, userId);
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

    public static void updateRoute(Context context, Route route, String userId){

        // Update route in database
        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, null, null);

        Uri uriUpdate = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, route.getId());
        routesContentProvider.update(uriUpdate,Route.createContentValuesFromRouteUpdate(route),null,null);

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context, userId);

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
    // --------------------------------------------- DELETE ---------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static void deleteRoute(Context context, Route route, String userId){

        if(route.getTypeRoute()!=null){

            // Delete routes segments related to this idRoute
            RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
            routeSegmentContentProvider.setUtils(context, userId);

            Uri uriDelete = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, route.getId());
            routeSegmentContentProvider.delete(uriDelete,null,null);

            // Delete routes segments related to this idRoute
            RoutesContentProvider routesContentProvider = new RoutesContentProvider();
            routesContentProvider.setUtils(context, userId, null,MY_ROUTE_TYPE);
            routesContentProvider.delete(uriDelete,null,null);
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- GET ----------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    public static Route getRoute(Context context, int idRoute, String userId){

        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, null,MY_ROUTE_TYPE);

        Uri uriQuery = ContentUris.withAppendedId(RoutesContentProvider.URI_ITEM, idRoute);
        final Cursor cursor = routesContentProvider.query(uriQuery, null, null, null, null);

        Route route = Route.getRouteFromCursor(cursor);
        route.setListRouteSegment(getRouteSegments(context, route.getId(), userId));

        return route;
    }

    public static Route getRouteEvent(Context context, String idEvent, String userId){

        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, idEvent,EVENT_ROUTE_TYPE);

        final Cursor cursor = routesContentProvider.query(null, null, null, null, null);

        Route route = Route.getRouteFromCursor(cursor);
        route.setListRouteSegment(getRouteSegments(context, route.getId(), userId));

        return route;
    }

    public static List<Route> getMyRoutes(Context context, String userId){

        RoutesContentProvider routesContentProvider = new RoutesContentProvider();
        routesContentProvider.setUtils(context, userId, null,MY_ROUTE_TYPE);

        final Cursor cursor = AppDatabase.getInstance(context, userId).routesDao().getMyRoutes(MY_ROUTE_TYPE);

        List<Route> listRoutes = Route.getListRoutesValidFromCursor(cursor);

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(Route route : listRoutes){
                    route.setListRouteSegment(getRouteSegments(context, route.getId(), userId));
                }
            }
        }

        return listRoutes;
    }

    public static List<Route> getAllRoutesForSynchronization(Context context, String userId){

        final Cursor cursor = AppDatabase.getInstance(context, userId).routesDao().getAllRoutes();

        List<Route> listRoutes = Route.getListRoutesFromCursor(cursor);

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(Route route : listRoutes){
                    route.setListRouteSegment(getRouteSegments(context, route.getId(), userId));
                }
            }
        }

        return listRoutes;
    }

    public static List<RouteSegment> getRouteSegments(Context context,int idRoute, String userId){

        RouteSegmentContentProvider routeSegmentContentProvider = new RouteSegmentContentProvider();
        routeSegmentContentProvider.setUtils(context, userId);

        Uri uriQuery = ContentUris.withAppendedId(RouteSegmentContentProvider.URI_ITEM, idRoute);
        final Cursor cursor = routeSegmentContentProvider.query(uriQuery, null, null, null, null);

        return RouteSegment.getRouteSegmentFromCursor(cursor);
    }
}

