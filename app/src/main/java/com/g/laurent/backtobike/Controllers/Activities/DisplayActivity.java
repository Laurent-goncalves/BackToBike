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
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsCounters;
import com.g.laurent.backtobike.Views.PageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DisplayActivity extends BaseActivity implements CallbackDisplayActivity {

    private static final String CANCELLED = "cancelled";
    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<Difference> listDifferences;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private DisplayViewPager pager;
    private PageAdapter adapter;
    private String idSelected;
    private int count;
    private int position;
    private DisplaySwipeToRefresh mySwipeRefreshLayout;
    @BindView(R.id.arrow_next) ImageView arrowNext;
    @BindView(R.id.arrow_back) ImageView arrowBack;
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

        // Configure swipe to refresh
        mySwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        mySwipeRefreshLayout.setOnRefreshListener(this::synchronizeWithFirebaseAndRefreshFragment);


        // Recover datas
        try {
            SaveAndRestoreDisplayActivity.restoreData(extras, userId,this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void synchronizeWithFirebaseAndRefreshFragment(){

        if(typeDisplay!=null){

            if(typeDisplay.equals(DISPLAY_MY_ROUTES))
                mySwipeRefreshLayout.setRefreshing(false);

            else {
                String idEvent;

                try {

                    if (typeDisplay.equals(DISPLAY_MY_EVENTS)) {
                        idEvent = listEvents.get(position).getId();
                        SynchronizeWithFirebase.synchronizeOneEvent(userId, idEvent, getApplicationContext(), new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                configureAndShowDisplayFragmentsInViewPager();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(String error) {
                                configureAndShowDisplayFragmentsInViewPager();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    } else {
                        idEvent = listInvitations.get(position).getId();
                        SynchronizeWithFirebase.synchronizeOneInvitation(userId, idEvent, getApplicationContext(), new CallbackSynchronizeEnd() {
                            @Override
                            public void onCompleted() {
                                configureAndShowDisplayFragmentsInViewPager();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(String error) {
                                configureAndShowDisplayFragmentsInViewPager();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
            }
        } else {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDisplayActivity.saveData(outState,this);
    }

    public void configureAndShowDisplayFragmentsInViewPager(){

        // Define page to display
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

        // Configure pager
        pager = findViewById(R.id.activity_display_viewpager);
        pager.setOffscreenPageLimit(0);
        adapter = new PageAdapter(getSupportFragmentManager(), typeDisplay, count);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                /*if(typeDisplay.equals(DISPLAY_MY_EVENTS))
                    showDifferences(position);*/

                setPosition(position);
                configureArrows();
                configureButtons(count > 0, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Set current page
        if(position!=-1)
            pager.setCurrentItem(position);
        else
            position = 0;

        configureViews(position);
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

        UtilsApp.configureArrows(position, sizeList, arrowBack, arrowNext, this);
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
                    if(listEvents.get(position).getOrganizerId().equals(userId)) { // If user is the organizer
                        buttonLeft.setVisibility(View.VISIBLE);
                        buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.round_cancel_white_48), null, null);
                        iconLeft = buttonLeft.getCompoundDrawables();
                        iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject), PorterDuff.Mode.SRC_IN);
                        buttonLeft.setText(context.getResources().getString(R.string.cancel));
                        buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));
                    } else if(listEvents.get(position).getStatus().equals(CANCELLED)){ // if event cancelled by organizer
                        buttonLeft.setVisibility(View.VISIBLE);
                        buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.baseline_delete_white_48), null, null);
                        iconLeft = buttonLeft.getCompoundDrawables();
                        iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject), PorterDuff.Mode.SRC_IN);
                        buttonLeft.setText(context.getResources().getString(R.string.delete));
                        buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));
                    } else
                        buttonLeft.setVisibility(View.GONE);

                    buttonRight.setVisibility(View.GONE);

                    break;

                case DISPLAY_MY_INVITS:

                    // REJECT INVITATION
                    buttonLeft.setVisibility(View.VISIBLE);
                    buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.round_cancel_white_48),null,null);
                    iconLeft = buttonLeft.getCompoundDrawables();
                    iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                    buttonLeft.setText(context.getResources().getString(R.string.delete));
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
                    Action.showAlertDialogDeleteRoute(listRoutes.get(position), position, userId, this);
                });
                // CHANGE ROUTE
                buttonRight.setOnClickListener(v -> launchTraceActivity(listRoutes.get(position)));
                break;

            case DISPLAY_MY_EVENTS:
                // CANCEL EVENT
                buttonLeft.setOnClickListener(v -> {
                    if(listEvents.get(position).getOrganizerId().equals(userId)) // If user is the organizer
                        Action.showAlertDialogCancelBikeEvent(listEvents.get(position), position, userId,this);
                    else if(listEvents.get(position).getStatus().equals(CANCELLED)) // if event cancelled by organizer
                        Action.showAlertDialogRejectEvent(listEvents.get(position),position,userId,this);
                });
                break;

            case DISPLAY_MY_INVITS:
                // REJECT INVITATION
                buttonLeft.setOnClickListener(v -> {
                    Action.showAlertDialogRejectInvitation(listInvitations.get(position),position,userId,this);
                });

                // ACCEPT INVITATION
                buttonRight.setOnClickListener(v -> {
                    Action.acceptInvitation(listInvitations.get(position),userId,getApplicationContext());
                    removeItemListInvits(position, getApplicationContext().getResources().getString(R.string.accept_invitation));
                });
                break;
        }
    }

    public void removeItemListRoutes(int position, String message){
        count--;
        listRoutes.remove(position);
        configureAndShowDisplayFragmentsInViewPager();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void removeItemListEvent(int position, String message){
        count--;
        listEvents.remove(position);
        configureAndShowDisplayFragmentsInViewPager();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void removeItemListInvits(int position, String message){
        count--;
        listInvitations.remove(position);
        configureAndShowDisplayFragmentsInViewPager();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /*private void showDifferences(int position){

        StringBuilder diffText = new StringBuilder();

        List<Difference> listDiffEvent = UtilsCounters.getListDifferencesFromBikeEvent(listEvents.get(position).getId(), listDifferences);

        if(listDiffEvent.size()>0){
            for(Difference diff : listDiffEvent){
                diffText.append(diff.getDifference());
                diffText.append("\n");
            }
        }

        // Remove all differences from bike event selected
        String idEvent = listEvents.get(position).getId();
        removeDifferences(idEvent);

        // Show text
        if(diffText.length()>0)
            Toast.makeText(getApplicationContext(), diffText, Toast.LENGTH_LONG).show();
    }

    private void removeDifferences(String idEvent){

        List<Integer> listPositionsToDelete = new ArrayList<>();

        if(listDifferences!=null){
            if(listDifferences.size()>0){
                for(int i = 0; i < listDifferences.size(); i++){
                    if(listDifferences.get(i).getIdEvent().equals(idEvent)){
                        listPositionsToDelete.add(i);
                    }
                }
            }
        }

        if(listPositionsToDelete.size()>0 && listDifferences!=null){
            for(int i : listPositionsToDelete){
                listDifferences.remove(i);
            }
        }
    }*/

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

    public List<Difference> getListDifferences() {
        return listDifferences;
    }

    public void setListDifferences(List<Difference> listDifferences) {
        this.listDifferences = listDifferences;
    }
}
