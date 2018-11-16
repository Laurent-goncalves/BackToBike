package com.g.laurent.backtobike.Models;

import android.support.v4.view.ViewPager;
import com.g.laurent.backtobike.Views.PageAdapter;
import java.util.List;


public interface CallbackDisplayActivity {

    List<Route> getListRoutes();

    List<BikeEvent> getListEvents();

    List<BikeEvent> getListInvitations();

    ViewPager getPager();

    PageAdapter getAdapter();
}
