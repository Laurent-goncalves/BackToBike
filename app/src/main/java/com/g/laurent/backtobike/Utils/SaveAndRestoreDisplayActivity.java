package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.os.Bundle;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;

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
    public static void restoreData(Bundle saveInstantState, String user_id, DisplayActivity displayActivity) throws InterruptedException {

        if(saveInstantState!=null && displayActivity!=null){

            String typeDisplay = saveInstantState.getString(BUNDLE_TYPE_DISPLAY);
            String id = saveInstantState.getString(BUNDLE_ID, null);

            displayActivity.savePreviousPage(typeDisplay);
            displayActivity.setTypeDisplay(typeDisplay);
            displayActivity.setIdSelected(id);

            defineListToShow(typeDisplay, user_id, displayActivity);
        }
    }

    private static void defineListToShow(String typeDisplay, String user_id, DisplayActivity displayActivity) throws InterruptedException {

        Context context = displayActivity.getApplicationContext();

        if(typeDisplay!=null){

            switch(typeDisplay){
                case DISPLAY_MY_ROUTES:

                    if(UtilsApp.isInternetAvailable(context)) {

                        SynchronizeWithFirebase.synchronizeMyRoutes(user_id, context, new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                configureDisplayActivityForMyRoutes(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }

                            @Override
                            public void onFailure(String error) {
                                configureDisplayActivityForMyRoutes(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }
                        });
                    } else {
                        configureDisplayActivityForMyRoutes(user_id, displayActivity);
                        displayActivity.configureAndShowDisplayFragmentsInViewPager();
                    }

                    break;

                case DISPLAY_MY_EVENTS:
                    // SynchronizeWithDatabase with Firebase if invitations are displayed
                    if(UtilsApp.isInternetAvailable(context)) {

                        SynchronizeWithFirebase.synchronizeMyEvents(user_id, context, new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                configureDisplayActivityForMyEvents(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }

                            @Override
                            public void onFailure(String error) {
                                configureDisplayActivityForMyEvents(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }
                        });
                    } else {
                        configureDisplayActivityForMyEvents(user_id, displayActivity);
                        displayActivity.configureAndShowDisplayFragmentsInViewPager();
                    }

                    break;

                case DISPLAY_MY_INVITS:

                    // SynchronizeWithDatabase with Firebase if invitations are displayed
                    if(UtilsApp.isInternetAvailable(context)) {

                        SynchronizeWithFirebase.synchronizeInvitations(user_id, context, new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                configureDisplayActivityForMyInvits(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }

                            @Override
                            public void onFailure(String error) {
                                configureDisplayActivityForMyInvits(user_id, displayActivity);
                                displayActivity.configureAndShowDisplayFragmentsInViewPager();
                            }
                        });
                    } else {
                        configureDisplayActivityForMyInvits(user_id, displayActivity);
                        displayActivity.configureAndShowDisplayFragmentsInViewPager();
                    }

                    break;
            }
        }
    }

    private static void configureDisplayActivityForMyRoutes(String user_id, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        // Get listRoutes
        List<Route> listRoutes = RouteHandler.getAllRoutes(context, user_id);
        displayActivity.setListRoutes(listRoutes);

        // Set count list Events
        if(listRoutes!=null)
            displayActivity.setCount(listRoutes.size());
        else
            displayActivity.setCount(0);
    }

    private static void configureDisplayActivityForMyEvents(String user_id, DisplayActivity displayActivity) {

        Context context = displayActivity.getApplicationContext();

        // Get Bike Events
        List<BikeEvent> listEvents = BikeEventHandler.getAllFutureBikeEvents(context, user_id);
        displayActivity.setListEvents(listEvents);

        // Set count list Events
        if(listEvents!=null)
            displayActivity.setCount(listEvents.size());
        else
            displayActivity.setCount(0);
    }

    private static void configureDisplayActivityForMyInvits(String user_id, DisplayActivity displayActivity){

        Context context = displayActivity.getApplicationContext();

        // Get Invitations
        List<BikeEvent> listInvits = BikeEventHandler.getAllInvitations(context,user_id);

        displayActivity.setListInvitations(listInvits);

        if(listInvits!=null)
            displayActivity.setCount(listInvits.size());
        else
            displayActivity.setCount(0);
    }

}
