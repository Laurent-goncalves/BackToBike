package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Controllers.Activities.EventActivity;
import com.g.laurent.backtobike.Controllers.Activities.FriendsActivity;

public interface CallbackBaseActivity {

    BaseActivity getBaseActivity();

    void signOutUserFromFirebase(Context context);

    public void launchMainActivity();

    public void launchFriendsActivity();

    public void launchEventActivity();

    public void launchDisplayActivity(String typeDisplay, String id);

    public void launchTraceActivity(Route route);

    Context getContextBaseActivity();

    Toolbar getToolbar();

    DrawerLayout getDrawerLayout();

    NavigationView getNavigationView();

}
