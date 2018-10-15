package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;
import com.g.laurent.backtobike.Controllers.Activities.EventActivity;
import com.g.laurent.backtobike.R;
import com.google.android.gms.tasks.OnSuccessListener;


public class ToolbarManager implements NavigationView.OnNavigationItemSelectedListener {

    private final static String MENU_MAIN_PAGE = "menu_main_page";
    private final static String MENU_MY_FRIENDS = "menu_my_friends";
    private final static String MENU_MY_EVENTS = "menu_my_events";
    private final static String MENU_MY_INVITS = "menu_my_invits";
    private final static String MENU_MY_ROUTES = "menu_my_routes";
    private final static String MENU_TRACE_ROUTE = "menu_trace_route";
    private final static String MENU_CREATE_EVENT = "menu_create_event";
    private final static String MENU_SIGN_OUT= "menu_sign_out";
    private static final int SIGN_OUT_TASK = 10;
    private Toolbar toolbar;
    private Context context;
    private ImageButton hamburger;
    private Button buttonToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BaseActivity baseActivity;

    public void configureToolbar(BaseActivity baseActivity, String currentMenu){

        this.baseActivity=baseActivity;
        context = baseActivity.getApplicationContext();

        if(baseActivity!=null) {
            toolbar = baseActivity.findViewById(R.id.activity_main_toolbar);
            baseActivity.setSupportActionBar(toolbar);
        }

        drawerLayout = baseActivity.findViewById(R.id.activity_drawer_layout);
        navigationView = baseActivity.findViewById(R.id.activity_nav_view);

        finalizeToolbarConfiguration(currentMenu);
        configureButtonToolbar(baseActivity, currentMenu);
        configureNavigationDrawerMenus(currentMenu);
    }

    private void finalizeToolbarConfiguration(String currentMenu){

        if(toolbar!=null){

            // configure hamburger menu to open the navigation drawer
            hamburger = toolbar.findViewById(R.id.button_hamburger);
            hamburger.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));
            navigationView.setNavigationItemSelectedListener(this);

            // Assign button from toolbar
            buttonToolbar = toolbar.findViewById(R.id.button_toolbar);

            //Assign and edit toolbar title
            TextView title_toolbar = toolbar.findViewById(R.id.title_toolbar);
            setTextTitleToolbar(currentMenu, title_toolbar);
        }
    }

    private void setTextTitleToolbar(String currentMenu, TextView title){

        switch(currentMenu){

            case MENU_MAIN_PAGE:
                title.setText(context.getResources().getString(R.string.title_main_page));
                break;

            case MENU_MY_FRIENDS:
                title.setText(context.getResources().getString(R.string.title_my_friends));
                break;

            case MENU_MY_EVENTS:
                title.setText(context.getResources().getString(R.string.title_my_events));
                break;

            case MENU_CREATE_EVENT:
                title.setText(context.getResources().getString(R.string.title_create_event));
                break;

            case MENU_MY_INVITS:
                title.setText(context.getResources().getString(R.string.title_my_invits));
                break;

            case MENU_MY_ROUTES:
                title.setText(context.getResources().getString(R.string.title_my_routes));
                break;

            case MENU_TRACE_ROUTE:
                title.setText(context.getResources().getString(R.string.title_trace_route));
                break;

            default:
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ---------------------------------- RIGHT BUTTON ------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void configureButtonToolbar(BaseActivity baseActivity, String currentMenu){


    }

    public void configureButtonToolbar(Boolean buttonVisible, EventActivity eventActivity){

        if(!buttonVisible){ // if no button needed, remove it
            buttonToolbar.setVisibility(View.GONE);
        } else { // if button needed, make it visible and configure click

            buttonToolbar.setText(context.getResources().getString(R.string.ok));
            buttonToolbar.setVisibility(View.VISIBLE);

            buttonToolbar.setOnClickListener(v -> {
                // Display invitfragment and configure guests selected
                eventActivity.getInvitation().setListIdFriends(eventActivity.getFriendFragment().getListFriendsSelected());
                eventActivity.backToInvitFragment();
            });
        }
    }

    // ------------------------------------------------------------------------------------------------
    // ------------------------------- NAVIGATION DRAWER ----------------------------------------------
    // ------------------------------------------------------------------------------------------------

    public void configureNavigationDrawerMenus(String currentMenu){

        navigationView.getMenu().findItem(R.id.my_friends_menu_item).setVisible(!currentMenu.equals(MENU_MY_FRIENDS));
        navigationView.getMenu().findItem(R.id.my_events_menu_item).setVisible(!currentMenu.equals(MENU_MY_EVENTS));
        navigationView.getMenu().findItem(R.id.my_invitations_menu_item).setVisible(!currentMenu.equals(MENU_MY_INVITS));
        navigationView.getMenu().findItem(R.id.my_routes_menu_item).setVisible(!currentMenu.equals(MENU_MY_ROUTES) && !currentMenu.equals(MENU_TRACE_ROUTE));
        navigationView.getMenu().findItem(R.id.back_to_main_page_menu_item).setVisible(!currentMenu.equals(MENU_MAIN_PAGE));
        navigationView.getMenu().findItem(R.id.sign_out_menu_item).setVisible(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(baseActivity!=null) {

            switch (id) {
                case R.id.my_friends_menu_item:
                    baseActivity.launchFriendsActivity();
                    break;
                case R.id.my_events_menu_item:
                    baseActivity.launchDisplayActivity(MENU_MY_EVENTS);
                    break;
                case R.id.my_invitations_menu_item:
                    baseActivity.launchDisplayActivity(MENU_MY_INVITS);
                    break;
                case R.id.my_routes_menu_item:
                    baseActivity.launchDisplayActivity(MENU_MY_ROUTES);
                    break;
                case R.id.back_to_main_page_menu_item:
                    baseActivity.launchMainActivity();
                    break;
                case R.id.sign_out_menu_item:
                    signOutUserFromFirebase();
                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(context)
                .addOnSuccessListener(baseActivity, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    baseActivity.finish();
                    break;
                default:
                    break;
            }
        };
    }
}
