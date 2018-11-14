package com.g.laurent.backtobike.Controllers.Activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.DisplaySwipeToRefresh;
import com.g.laurent.backtobike.Models.DisplayViewPager;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.PageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DisplayActivity extends BaseActivity implements CallbackDisplayActivity, View.OnClickListener {

    private static final String CANCELLED = "cancelled";
    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private DisplayViewPager pager;
    private PageAdapter adapter;
    private String idSelected;
    private int count;
    private int position;
    @BindView(R.id.arrow_next) ImageButton arrowNext;
    @BindView(R.id.arrow_back) ImageButton arrowBack;
    @BindView(R.id.left_button)  Button buttonLeft;
    @BindView(R.id.right_button) Button buttonRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ButterKnife.bind(this);

        // Initialization
        userId = FirebaseAuth.getInstance().getUid();
        Bundle extras = getIntent().getExtras();
        assignToolbarViews();

        // Recover datas
        try {
            SaveAndRestoreDisplayActivity.restoreData(extras, userId,this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            configureArrows();
            configureButtons(count > 0,position);
        } else
            configureAndShowDisplayFragmentsInViewPager();
    }

    public void synchronizeWithFirebaseAndRefreshFragment(){

        if(typeDisplay!=null && UtilsApp.isInternetAvailable(getApplicationContext())){

            if(typeDisplay.equals(DISPLAY_MY_ROUTES)) {

                try {
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

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    configureAndShowDisplayFragmentsInViewPager();
                }

            } else {
                try {
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

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    configureAndShowDisplayFragmentsInViewPager();
                }
            }
        } else
            configureAndShowDisplayFragmentsInViewPager();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDisplayActivity.saveData(outState,this);
    }

    private void definePositionToDisplay(){
        switch (typeDisplay) {
            case DISPLAY_MY_ROUTES:
                position = UtilsApp.findIndexRouteInList(idSelected, listRoutes);
                break;
            case DISPLAY_MY_EVENTS:
                position = UtilsApp.findIndexEventInList(idSelected, listEvents);
                break;
            case DISPLAY_MY_INVITS:
                position = UtilsApp.findIndexEventInList(idSelected, listInvitations);
                break;
        }
    }

    public void configureAndShowDisplayFragmentsInViewPager(){

        if(adapter!=null){
            adapter.notifyDataSetChanged();

        } else {
            // Define page to display
            definePositionToDisplay();

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
                configureArrows();
                configureButtons(count > 0, position);
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

    public void configureViews(int position){

        defineCountersAndConfigureToolbar(typeDisplay);

        // Configure views
        configureArrows();
        configureButtons(count > 0, position);
        configureAddButton();
    }

    private void configureArrows(){

        int sizeList = 0;

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                if(listRoutes.size()>0)
                    setIdSelected(String.valueOf(listRoutes.get(position).getId()));
                sizeList = getListRoutes().size();
                break;
            case DISPLAY_MY_EVENTS:
                if(listEvents.size()>0)
                    setIdSelected(listEvents.get(position).getId());
                sizeList = getListEvents().size();
                break;
            case DISPLAY_MY_INVITS:
                if(listInvitations.size()>0)
                    setIdSelected(listInvitations.get(position).getId());
                sizeList = getListInvitations().size();
                break;
        }

        configureArrows(position, sizeList);
    }

    public void configureArrows(int position, int sizeList){

        if(UtilsApp.needLeftArrow(position,sizeList)) {
            arrowBack.setVisibility(View.VISIBLE);
            arrowBack.setOnClickListener(v -> {
                pager.setCurrentItem(position-1);
                adapter.notifyDataSetChanged();
            });
        } else
            arrowBack.setVisibility(View.INVISIBLE);

        if(UtilsApp.needRightArrow(position,sizeList)) {
            arrowNext.setVisibility(View.VISIBLE);
            arrowNext.setOnClickListener(v -> {
                pager.setCurrentItem(position+1);
                adapter.notifyDataSetChanged();
            });
        } else
            arrowNext.setVisibility(View.INVISIBLE);
    }

    private void configureButtons(Boolean showButtons, int position){

        Drawable iconRight[];
        Drawable iconLeft[];
        Context context = getApplicationContext();

        if(!showButtons){ // HIDE BUTTONS
            buttonLeft.setVisibility(View.GONE);
            buttonRight.setVisibility(View.GONE);

        } else { // SHOW BUTTONS
            switch(typeDisplay){
                case DISPLAY_MY_ROUTES:

                    // DELETE ROUTE
                    buttonLeft.setVisibility(View.VISIBLE);
                    iconLeft = buttonLeft.getCompoundDrawables();
                    iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                    buttonLeft.setText(context.getResources().getString(R.string.delete));
                    buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                    // CHANGE ROUTE
                    buttonRight.setVisibility(View.VISIBLE);
                    buttonRight.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.baseline_edit_white_48),null,null);
                    iconRight = buttonRight.getCompoundDrawables();
                    iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineNotComplete),PorterDuff.Mode.SRC_IN);
                    buttonRight.setText(context.getResources().getString(R.string.change));
                    buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineNotComplete));

                    break;

                case DISPLAY_MY_EVENTS:

                    // CANCEL EVENT
                    Boolean isEventCancelled = listEvents.get(position).getStatus().equals(CANCELLED);

                    if(!isEventCancelled) { // If event not cancelled
                        buttonLeft.setVisibility(View.VISIBLE);
                        buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.round_cancel_white_48), null, null);
                        iconLeft = buttonLeft.getCompoundDrawables();
                        iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject), PorterDuff.Mode.SRC_IN);
                        buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                        if(listEvents.get(position).getOrganizerId().equals(userId))
                            buttonLeft.setText(context.getResources().getString(R.string.cancel));
                        else
                            buttonLeft.setText(context.getResources().getString(R.string.reject));

                    } else { // if event cancelled by organizer
                        buttonLeft.setVisibility(View.VISIBLE);
                        buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.baseline_delete_white_48), null, null);
                        iconLeft = buttonLeft.getCompoundDrawables();
                        iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject), PorterDuff.Mode.SRC_IN);
                        buttonLeft.setText(context.getResources().getString(R.string.delete));
                        buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));
                    }

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    buttonLeft.setLayoutParams(lp);

                    buttonRight.setVisibility(View.GONE);

                    break;

                case DISPLAY_MY_INVITS:

                    // REJECT INVITATION
                    buttonLeft.setVisibility(View.VISIBLE);
                    buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.round_cancel_white_48),null,null);
                    iconLeft = buttonLeft.getCompoundDrawables();
                    iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                    buttonLeft.setText(context.getResources().getString(R.string.reject));
                    buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                    // ACCEPT INVITATION
                    buttonRight.setVisibility(View.VISIBLE);
                    buttonRight.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.baseline_check_circle_white_48),null,null);
                    iconRight = buttonRight.getCompoundDrawables();
                    iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineComplete),PorterDuff.Mode.SRC_IN);
                    buttonRight.setText(context.getResources().getString(R.string.accept));
                    buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineComplete));
                    break;
            }

            setOnClickListenersButtons();
        }
    }

    private void setOnClickListenersButtons(){
        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                // DELETE ROUTE
                buttonLeft.setOnClickListener(v -> {
                    Action.showAlertDialogDeleteRoute(listRoutes.get(position), userId, this);
                });
                // CHANGE ROUTE
                buttonRight.setOnClickListener(v -> launchTraceActivity(listRoutes.get(position)));
                break;

            case DISPLAY_MY_EVENTS:
                // CANCEL EVENT
                buttonLeft.setOnClickListener(v -> {

                    Boolean isEventFromOrganizer = listEvents.get(position).getOrganizerId().equals(userId);
                    Boolean isEventCancelled = listEvents.get(position).getStatus().equals(CANCELLED);

                    if(!isEventCancelled) { // If event not cancelled
                        if(isEventFromOrganizer) // If user is the organizer
                            Action.showAlertDialogCancelBikeEvent(listEvents.get(position), userId, this);
                        else // If user is NOT the organizer
                            Action.showAlertDialogRejectEvent(listEvents.get(position),userId,this);
                    } else // if event cancelled by organizer, delete it
                        Action.deleteBikeEvent(listEvents.get(position), userId, getApplicationContext());

                    // cancel alarm bikeEvent
                    cancelAlarmEvent(listEvents.get(position));
                });
                break;

            case DISPLAY_MY_INVITS:
                // REJECT INVITATION
                buttonLeft.setOnClickListener(v -> {
                    Action.showAlertDialogRejectInvitation(listInvitations.get(position),userId,this);
                });

                // ACCEPT INVITATION
                buttonRight.setOnClickListener(v -> {
                    Action.acceptInvitation(listInvitations.get(position),userId,getApplicationContext());
                    // set alarm for event
                    configureAlarmManager(listInvitations.get(position));
                    updateListAfterDeletion(getApplicationContext().getResources().getString(R.string.accept_invitation));
                });
                break;
        }
    }

    private void configureAddButton(){

        if(typeDisplay.equals(DISPLAY_MY_ROUTES) || typeDisplay.equals(DISPLAY_MY_EVENTS)){
            ImageButton buttonAdd = findViewById(R.id.button_add);
            buttonAdd.setOnClickListener(v -> {
                switch(typeDisplay){

                    case DISPLAY_MY_ROUTES:
                        launchTraceActivity(null);
                        break;

                    case DISPLAY_MY_EVENTS:
                        launchEventActivity();
                        break;
                }
            });
        } else if (typeDisplay.equals(DISPLAY_MY_INVITS)){
            ImageButton buttonAdd = findViewById(R.id.button_add);
            buttonAdd.setVisibility(View.GONE);
        }
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        System.out.println("eee " + v.toString());
    }
}
