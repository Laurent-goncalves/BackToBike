package com.g.laurent.backtobike.Controllers.Activities;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.DisplayViewPager;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.Configurations.ConfigureDisplayActivity;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.PageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;


public class DisplayActivity extends BaseActivity implements CallbackDisplayActivity {

    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private DisplayViewPager pager;
    private PageAdapter adapter;
    private String idSelected;
    private int count;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Initialization
        userId = FirebaseAuth.getInstance().getUid();
        Bundle extras = getIntent().getExtras();
        assignToolbarViews();

        // Recover datas
        try {
            SaveAndRestoreDisplayActivity.restoreData(extras, userId,this);
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_data_restoration) + "\n" + e.toString(),Toast.LENGTH_LONG).show();
            configureAndShowDisplayFragmentsInViewPager();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDisplayActivity.saveData(outState,this);
    }

    public void synchronizeWithFirebaseAndRefreshFragment(){

        if(typeDisplay!=null && UtilsApp.isInternetAvailable(getApplicationContext())){

            if(typeDisplay.equals(DISPLAY_MY_ROUTES)) {

                SynchronizeWithFirebase.synchronizeMyRoutes(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
                    @Override
                    public void onCompleted() {
                        listRoutes = RouteHandler.getAllRoutes(getApplicationContext(), userId);
                        configureAndShowDisplayFragmentsInViewPager();
                    }

                    @Override
                    public void onFailure(String error) {
                        configureAndShowDisplayFragmentsInViewPager();
                    }
                });

            } else {
                if( listEvents.size()>0){
                    if (typeDisplay.equals(DISPLAY_MY_EVENTS)) {
                        SynchronizeWithFirebase.synchronizeMyEvents(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                listEvents = BikeEventHandler.getAllFutureBikeEvents(getApplicationContext(),userId);
                                configureAndShowDisplayFragmentsInViewPager();
                            }

                            @Override
                            public void onFailure(String error) {
                                configureAndShowDisplayFragmentsInViewPager();
                            }
                        });
                    } else {
                        SynchronizeWithFirebase.synchronizeInvitations(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                listInvitations = BikeEventHandler.getAllInvitations(getApplicationContext(),userId);
                                configureAndShowDisplayFragmentsInViewPager();
                            }

                            @Override
                            public void onFailure(String error) {
                                configureAndShowDisplayFragmentsInViewPager();
                            }
                        });
                    }
                } else
                    configureAndShowDisplayFragmentsInViewPager();

            }
        } else
            configureAndShowDisplayFragmentsInViewPager();

    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------ CONFIGURE VIEWPAGER -----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void configureViews(int position){

        // Define counters
        if(userId!=null)
            defineCountersAndConfigureToolbar(typeDisplay);

        // Configure views
        new ConfigureDisplayActivity(findViewById(R.id.displayactivity_xml), position, count, userId, typeDisplay,this);
    }

    public void updateListAfterDeletion(String message){

        position = 0;

        // Show message to user
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        // Update viewpager
        switch (typeDisplay) {
            case DISPLAY_MY_ROUTES:
                listRoutes = RouteHandler.getAllRoutes(getApplicationContext(), userId);
                count = listRoutes.size();
                break;
            case DISPLAY_MY_EVENTS:
                listEvents = BikeEventHandler.getAllFutureBikeEvents(getApplicationContext(),userId);
                count = listEvents.size();
                break;
            case DISPLAY_MY_INVITS:
                listInvitations = BikeEventHandler.getAllInvitations(getApplicationContext(),userId);
                count = listInvitations.size();
                break;
        }

        if(adapter!=null) {
            adapter.setCount(count);
            adapter.notifyDataSetChanged();
            new ConfigureDisplayActivity(findViewById(R.id.displayactivity_xml), position, count, userId, typeDisplay,this);
        } else
            configureAndShowDisplayFragmentsInViewPager();
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------ CONFIGURE VIEWPAGER -----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void configureAndShowDisplayFragmentsInViewPager(){

        if(adapter!=null){
            adapter.notifyDataSetChanged();

            // Configure other views (arrows, buttons, ... )
            configureViews(position);

        } else {
            // Define page to display
            position = UtilsApp.definePositionToDisplay(typeDisplay, idSelected, this);

            // Configure pager
            configureViewPager();

            // Configure other views (arrows, buttons, ... )
            configureViews(position);
        }
    }

    private void configureViewPager(){
        pager = findViewById(R.id.activity_display_viewpager);
        pager.setOffscreenPageLimit(0);
        adapter = new PageAdapter(getSupportFragmentManager(), typeDisplay, count);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setPosition(position);
                configureViews(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == count-1))
                    synchronizeWithFirebaseAndRefreshFragment();
            }
        });

        // Set current page
        if(position!=-1)
            pager.setCurrentItem(position);
        else
            position = 0;
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public String getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public String getIdSelected() {
        return idSelected;
    }

    public void setIdSelected(String idSelected) {
        this.idSelected = idSelected;
    }

    public void setListRoutes(List<Route> listRoutes) {
        this.listRoutes = listRoutes;
    }

    public void setListEvents(List<BikeEvent> listEvents) {
        this.listEvents = listEvents;
    }

    public void setListInvitations(List<BikeEvent> listInvitations) {
        this.listInvitations = listInvitations;
    }

    public List<Route> getListRoutes() {
        return listRoutes;
    }

    public List<BikeEvent> getListEvents() {
        return listEvents;
    }

    public List<BikeEvent> getListInvitations() {
        return listInvitations;
    }

    public ViewPager getPager() {
        return pager;
    }

    public PageAdapter getAdapter() {
        return adapter;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
