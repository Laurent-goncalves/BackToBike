package com.g.laurent.backtobike.Controllers.Activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.PageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DisplayActivity extends BaseActivity implements CallbackDisplayActivity {

    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private ViewPager pager;
    private PageAdapter adapter;
    private String idSelected;
    private int count;
    private int position;
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

        // Recover datas
        try {
            SaveAndRestoreDisplayActivity.restoreData(extras, userId,this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void configureViews(){

        synchronizeDataWithFirebaseAndConfigureToolbar(typeDisplay,this);

        // Configure views
        configureAndShowDisplayFragmentsInViewPager();
        configureArrows();
        configureButtons();
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
        adapter = new PageAdapter(getSupportFragmentManager(), typeDisplay, count);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setPosition(position);
                configureArrows();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Set current page
        if(position!=-1)
            pager.setCurrentItem(position);
        else
            position = 0;

        configureArrows();
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

    private void configureButtons(){

        Drawable iconRight[];
        Drawable iconLeft[];
        Context context = getApplicationContext();

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:

                // DELETE ROUTE
                iconLeft = buttonLeft.getCompoundDrawables();
                iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                // CHANGE ROUTE
                buttonRight.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.baseline_edit_white_48),null,null);
                iconRight = buttonRight.getCompoundDrawables();
                iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineNotComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.change));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineNotComplete));

                break;

            case DISPLAY_MY_EVENTS:

                // CANCEL EVENT
                buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.round_cancel_white_48),null,null);
                iconLeft = buttonLeft.getCompoundDrawables();
                iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorGray),PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.cancel));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorGray));

                buttonRight.setVisibility(View.GONE);

                break;

            case DISPLAY_MY_INVITS:

                // REJECT INVITATION
                buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.round_cancel_white_48),null,null);
                iconLeft = buttonLeft.getCompoundDrawables();
                iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                // ACCEPT INVITATION
                buttonRight.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.baseline_check_circle_white_48),null,null);
                iconRight = buttonRight.getCompoundDrawables();
                iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.accept));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineComplete));
                break;
        }

        setOnClickListenersButtons();
    }

    private void setOnClickListenersButtons(){
        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                // DELETE ROUTE
                buttonLeft.setOnClickListener(v -> {
                    Action.deleteRoute(listRoutes.get(position), userId, getApplicationContext());
                    listRoutes.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.delete_route), Toast.LENGTH_LONG).show();
                });
                // CHANGE ROUTE
                buttonRight.setOnClickListener(v -> launchTraceActivity(listRoutes.get(position)));
                break;

            case DISPLAY_MY_EVENTS:
                // CANCEL EVENT
                buttonLeft.setOnClickListener(v -> Action.cancelBikeEvent(listEvents.get(position),userId,getApplicationContext()));
                break;

            case DISPLAY_MY_INVITS:
                // REJECT INVITATION
                buttonLeft.setOnClickListener(v -> Action.rejectInvitation(listInvitations.get(position),userId,getApplicationContext()));
                // ACCEPT INVITATION
                buttonRight.setOnClickListener(v -> Action.acceptInvitation(listInvitations.get(position),userId,getApplicationContext()));
                break;
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
}
