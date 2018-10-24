package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;
import com.g.laurent.backtobike.Controllers.Activities.EventActivity;
import com.g.laurent.backtobike.R;
import com.google.android.gms.tasks.OnSuccessListener;


public class ToolbarManager implements NavigationView.OnNavigationItemSelectedListener {

    private final static String MENU_MAIN_PAGE = "menu_main_page";
    private final static String MENU_MY_FRIENDS = "menu_my_friends";
    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    private final static String MENU_TRACE_ROUTE = "menu_trace_route";
    private final static String MENU_CREATE_EVENT = "menu_create_event";

    private CallbackBaseActivity callbackBaseActivity;

    public void configureToolbar(CallbackBaseActivity callbackBaseActivity, String currentMenu, int countFriends, int countInvit){

        this.callbackBaseActivity=callbackBaseActivity;

        finalizeToolbarConfiguration(currentMenu, callbackBaseActivity);
        setCounterFriendsRequests(countFriends, callbackBaseActivity.getNavigationView());
        setCounterInvitation(countInvit, callbackBaseActivity.getNavigationView());
    }

    private void finalizeToolbarConfiguration(String currentMenu, CallbackBaseActivity callbackBaseActivity){

        if(callbackBaseActivity.getToolbar()!=null){

            Toolbar toolbar = callbackBaseActivity.getToolbar();

            // configure hamburger menu to open the navigation drawer
            ImageButton hamburger = toolbar.findViewById(R.id.button_hamburger);
            DrawerLayout drawerLayout = callbackBaseActivity.getDrawerLayout();
            NavigationView navigationView = callbackBaseActivity.getNavigationView();

            hamburger.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));
            navigationView.setNavigationItemSelectedListener(this);

            //Assign and edit toolbar title
            TextView title_toolbar = toolbar.findViewById(R.id.title_toolbar);
            setTextTitleToolbar(currentMenu, callbackBaseActivity.getContextBaseActivity(), title_toolbar);
        }
    }

    private static void setTextTitleToolbar(String currentMenu, Context context, TextView title){

        switch(currentMenu){

            case MENU_MAIN_PAGE:
                title.setText(context.getResources().getString(R.string.title_main_page));
                break;

            case MENU_MY_FRIENDS:
                title.setText(context.getResources().getString(R.string.title_my_friends));
                break;

            case DISPLAY_MY_EVENTS:
                title.setText(context.getResources().getString(R.string.title_my_events));
                break;

            case MENU_CREATE_EVENT:
                title.setText(context.getResources().getString(R.string.title_create_event));
                break;

            case DISPLAY_MY_INVITS:
                title.setText(context.getResources().getString(R.string.title_my_invits));
                break;

            case DISPLAY_MY_ROUTES:
                title.setText(context.getResources().getString(R.string.title_my_routes));
                break;

            case MENU_TRACE_ROUTE:
                title.setText(context.getResources().getString(R.string.title_trace_route));
                break;

            default:
        }
    }


    // ------------------------------------------------------------------------------------------------
    // ------------------------------- NAVIGATION DRAWER ----------------------------------------------
    // ------------------------------------------------------------------------------------------------

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (callbackBaseActivity != null) {
            if (callbackBaseActivity.getBaseActivity() != null) {

                switch (id) {

                    case R.id.my_friends_menu_item:
                        callbackBaseActivity.launchFriendsActivity();
                        break;
                    case R.id.my_events_menu_item:
                        callbackBaseActivity.launchDisplayActivity(DISPLAY_MY_EVENTS, null);
                        break;
                    case R.id.my_invitations_menu_item:
                        callbackBaseActivity.launchDisplayActivity(DISPLAY_MY_INVITS, null);
                        break;
                    case R.id.my_routes_menu_item:
                        callbackBaseActivity.launchDisplayActivity(DISPLAY_MY_ROUTES, null);
                        break;
                    case R.id.back_to_main_page_menu_item:
                        callbackBaseActivity.launchMainActivity();
                        break;
                    case R.id.sign_out_menu_item:
                        callbackBaseActivity.signOutUserFromFirebase(callbackBaseActivity.getContextBaseActivity());
                        break;
                    default:
                        break;
                }

                callbackBaseActivity.getDrawerLayout().closeDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    private static void setCounterFriendsRequests(int count, NavigationView navigationView) {
        LinearLayout layoutCounter = (LinearLayout) navigationView.getMenu().findItem(R.id.my_friends_menu_item).getActionView();
        setCounter(count,layoutCounter);
    }

    private static void setCounterInvitation(int count, NavigationView navigationView) {
        LinearLayout layoutCounter = (LinearLayout) navigationView.getMenu().findItem(R.id.my_invitations_menu_item).getActionView();
        setCounter(count,layoutCounter);
    }

    private static void setCounter(int count, LinearLayout layoutCounter) {

        TextView view = (TextView) layoutCounter.findViewById(R.id.count_invitation);

        if(count==0){
            view.setVisibility(View.INVISIBLE);
        } else if (count>9){
            view.setText(String.valueOf("+9"));
        } else {
            view.setText(String.valueOf(count));
        }
    }
}
