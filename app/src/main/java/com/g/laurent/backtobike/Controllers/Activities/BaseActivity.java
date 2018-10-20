package com.g.laurent.backtobike.Controllers.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.ToolbarManager;
import com.g.laurent.backtobike.R;

public class BaseActivity extends AppCompatActivity {

    protected final static String MENU_MAIN_PAGE = "menu_main_page";
    protected final static String MENU_MY_FRIENDS = "menu_my_friends";
    protected final static String DISPLAY_MY_ROUTES ="display_my_routes";
    protected final static String DISPLAY_MY_EVENTS ="display_my_events";
    protected final static String DISPLAY_MY_INVITS ="display_my_invits";
    protected final static String MENU_TRACE_ROUTE = "menu_trace_route";
    protected final static String MENU_CREATE_EVENT = "menu_create_event";
    protected final static String MENU_SIGN_OUT= "menu_sign_out";
    protected static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    protected static final String BUNDLE_ROUTE_ID ="bundle_route_id";
    protected static final String BUNDLE_ID ="bundle_id";
    protected static final String LOGIN_SHARED ="login_shared";
    protected ToolbarManager toolbarManager;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbarManager = new ToolbarManager();
    }

    public void launchMainActivity(){
        Intent intent = new Intent(this,DisplayActivity.class);
        startActivity(intent);
    }

    public void launchFriendsActivity(){
        Intent intent = new Intent(this,FriendsActivity.class);
        startActivity(intent);
    }

    public void launchEventActivity(){
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

    public void launchDisplayActivity(String typeDisplay, String id){
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra(BUNDLE_TYPE_DISPLAY,typeDisplay);
        intent.putExtra(BUNDLE_ID, id);
        startActivity(intent);
    }

    public void launchTraceActivity(Route route){

        Intent intent = new Intent(this, TraceActivity.class);
        if(route!=null)
            intent.putExtra(BUNDLE_ROUTE_ID, route.getId());
        startActivity(intent);
    }

    public static void showSnackBar(BaseActivity baseActivity, String text) {
        //Snackbar.make(baseActivity.findViewById(R.id.fragment_position), text, Snackbar.LENGTH_LONG).show();
    }
}
