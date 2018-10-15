package com.g.laurent.backtobike.Controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Views.PageAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;



public class DisplayActivity extends AppCompatActivity implements CallbackDisplayActivity {

    private static final String BUNDLE_ROUTE_ID ="bundle_route_id";
    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private String user_id;
    private ViewPager pager;
    private PageAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        user_id = FirebaseAuth.getInstance().getUid();
        Bundle extras = getIntent().getExtras();
        SaveAndRestoreDisplayActivity.restoreData(extras,user_id,this);
        configureAndShowDisplayFragmentsInViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDisplayActivity.saveData(outState,this);
    }

    public void configureAndShowDisplayFragmentsInViewPager(){

        pager = findViewById(R.id.activity_display_viewpager);
        adapter = new PageAdapter(getSupportFragmentManager(), typeDisplay);
        pager.setAdapter(adapter);
    }

    public void launchTraceActivity(Route route){

        Intent intent = new Intent(this, TraceActivity.class);
        intent.getExtras().putInt(BUNDLE_ROUTE_ID, route.getId());
        startActivity(intent);
    }

    public String getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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


}
