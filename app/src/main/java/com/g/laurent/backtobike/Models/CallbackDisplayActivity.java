package com.g.laurent.backtobike.Models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.g.laurent.backtobike.Views.PageAdapter;

import java.util.Calendar;
import java.util.List;

public interface CallbackDisplayActivity {

    List<Route> getListRoutes();

    List<BikeEvent> getListEvents();

    List<BikeEvent> getListInvitations();

    ViewPager getPager();

    PageAdapter getAdapter();

    void launchTraceActivity(Route route);
}
