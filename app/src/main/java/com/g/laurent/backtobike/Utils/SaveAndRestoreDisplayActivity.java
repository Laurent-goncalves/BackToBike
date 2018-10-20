package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.os.Bundle;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import java.util.List;


public class SaveAndRestoreDisplayActivity {

    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    private static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    private static final String BUNDLE_ID ="bundle_id";

    // ----------------------------------- SAVE DATA
    public static void saveData(Bundle bundle, DisplayActivity displayActivity){
        if(bundle!=null && displayActivity!=null) {
            bundle.putString(BUNDLE_TYPE_DISPLAY, displayActivity.getTypeDisplay());
            bundle.putString(BUNDLE_ID, displayActivity.getIdSelected());
        }
    }

    // ----------------------------------- RESTORE DATA
    public static void restoreData(Bundle saveInstantState, String user_id, DisplayActivity displayActivity){

        if(saveInstantState!=null && displayActivity!=null){

            String typeDisplay = saveInstantState.getString(BUNDLE_TYPE_DISPLAY);
            String id = saveInstantState.getString(BUNDLE_ID, null);

            displayActivity.setTypeDisplay(typeDisplay);
            displayActivity.setIdSelected(id);

            defineListToShow(typeDisplay, user_id, displayActivity);
        }
    }

    private static void defineListToShow(String typeDisplay, String user_id, DisplayActivity displayActivity){

        Context context = displayActivity.getApplicationContext();

        if(typeDisplay!=null){

            switch(typeDisplay){
                case DISPLAY_MY_ROUTES:
                    List<Route> listRoutes = RouteHandler.getAllRoutes(context, user_id);
                    displayActivity.setListRoutes(listRoutes);
                    displayActivity.setCount(listRoutes.size());
                    break;

                case DISPLAY_MY_EVENTS:

                    // Get Bike Events
                    List<BikeEvent> listEvents = BikeEventHandler.getAllFutureBikeEvents(context,user_id,user_id);

                    // Find event friends and route for each bikeEvent
                    if(listEvents!=null){
                        if(listEvents.size()>0){
                            for(BikeEvent event : listEvents){

                                // Event friends
                                List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,event.getId(),user_id);
                                event.setListEventFriends(listEventFriends);

                                // Route and RouteSegments
                                Route route = RouteHandler.getRoute(context,event.getIdRoute(),user_id);
                                List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,event.getIdRoute(),user_id);
                                route.setListRouteSegment(listSegments);
                                event.setRoute(route);
                            }
                        }
                    }

                    displayActivity.setListEvents(listEvents);

                    if(listEvents!=null)
                        displayActivity.setCount(listEvents.size());
                    else
                        displayActivity.setCount(0);

                    break;

                case DISPLAY_MY_INVITS:

                    // Get Invitations
                    List<BikeEvent> listInvits = BikeEventHandler.getAllInvitiations(context,user_id,user_id);

                    // Find event friends and route for each invitations
                    if(listInvits!=null){
                        if(listInvits.size()>0){
                            for(BikeEvent event : listInvits){

                                // Event friends
                                List<EventFriends> listEventFriends = BikeEventHandler.getEventFriends(context,event.getId(),user_id);
                                event.setListEventFriends(listEventFriends);

                                // Route and RouteSegments
                                Route route = RouteHandler.getRoute(context,event.getIdRoute(),user_id);
                                List<RouteSegment> listSegments = RouteHandler.getRouteSegments(context,event.getIdRoute(),user_id);
                                route.setListRouteSegment(listSegments);
                                event.setRoute(route);
                            }
                        }
                    }

                    displayActivity.setListInvitations(listInvits);
                    if(listInvits!=null)
                        displayActivity.setCount(listInvits.size());
                    else
                        displayActivity.setCount(0);

                    break;
            }
        }
    }

}
