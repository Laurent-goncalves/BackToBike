package com.g.laurent.backtobike.Controllers.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.g.laurent.backtobike.Models.CallbackBaseActivity;
import com.g.laurent.backtobike.Models.CallbackCounters;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.ToolbarManager;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.google.android.gms.tasks.OnSuccessListener;

public class BaseActivity extends AppCompatActivity implements CallbackBaseActivity{

    protected final static String MENU_MAIN_PAGE = "menu_main_page";
    protected final static String MENU_MY_FRIENDS = "menu_my_friends";
    protected final static String DISPLAY_MY_ROUTES ="display_my_routes";
    protected final static String DISPLAY_MY_EVENTS ="display_my_events";
    protected final static String DISPLAY_MY_INVITS ="display_my_invits";
    protected final static String MENU_TRACE_ROUTE = "menu_trace_route";
    protected final static String MENU_CREATE_EVENT = "menu_create_event";
    protected static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    protected static final String BUNDLE_ROUTE_ID ="bundle_route_id";
    protected static final String BUNDLE_ID ="bundle_id";
    protected static final String LOGIN_SHARED ="login_shared";
    protected static final int SIGN_OUT_TASK = 10;
    protected final static String MENU_SIGN_OUT= "menu_sign_out";
    protected CallbackBaseActivity callbackBaseActivity;
    protected ToolbarManager toolbarManager;
    protected String userId;
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbarManager = new ToolbarManager();
        callbackBaseActivity = this;
    }

    protected void assignToolbarViews(){

        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activity_drawer_layout);
        navigationView = findViewById(R.id.activity_nav_view);
    }

    public void launchMainActivity(){
        Intent intent = new Intent(this,DisplayActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchFriendsActivity(){
        Intent intent = new Intent(this,FriendsActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchEventActivity(){
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchDisplayActivity(String typeDisplay, String id){
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra(BUNDLE_TYPE_DISPLAY,typeDisplay);
        intent.putExtra(BUNDLE_ID, id);
        startActivity(intent);
        finish();
    }

    public void launchTraceActivity(Route route){

        Intent intent = new Intent(this, TraceActivity.class);
        if(route!=null)
            intent.putExtra(BUNDLE_ROUTE_ID, route.getId());
        startActivity(intent);
        finish();
    }

    public void defineCountersAndConfigureToolbar(String typeDisplay){

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.recoverDatasForCounters(userId, getApplicationContext(), new CallbackCounters() {
            @Override
            public void onCompleted(int counterFriend, int counterEvents, int counterInvits) {
                toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, counterFriend, counterEvents, counterInvits);
                int count = counterFriend + counterEvents + counterInvits;
                UtilsApp.setBadge(getApplicationContext(), count);
            }

            @Override
            public void onFailure(String error) {
                toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, 0, 0,0);
                UtilsApp.setBadge(getApplicationContext(), 0);
            }
        });
    }

    public static void showSnackBar(BaseActivity baseActivity, String text) {
        //Snackbar.make(baseActivity.findViewById(R.id.fragment_position), text, Snackbar.LENGTH_LONG).show();
    }

    public void signOutUserFromFirebase(Context context) {
        AuthUI.getInstance()
                .signOut(context)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    protected OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }

    public BaseActivity getBaseActivity(){
        return this;
    }

    public Context getContextBaseActivity(){
        return getApplicationContext();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }
}
