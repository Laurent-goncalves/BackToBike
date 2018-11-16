package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;


public interface CallbackBaseActivity {

    BaseActivity getBaseActivity();

    void signOutUserFromFirebase(Context context);

    void launchMainActivity();

    void launchFriendsActivity();

    void launchEventActivity();

    void launchDisplayActivity(String typeDisplay, String id);

    void launchTraceActivity(Route route);

    Context getContextBaseActivity();

    Toolbar getToolbar();

    DrawerLayout getDrawerLayout();

    NavigationView getNavigationView();
}
