package com.g.laurent.backtobike.Views;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.g.laurent.backtobike.Controllers.Fragments.DisplayFragment;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Route;
import java.util.List;


public class PageAdapter extends FragmentPagerAdapter {

    private String typeDisplay;
    private int count;

    public PageAdapter(FragmentManager fm, String typeDisplay, int count) {
        super(fm);
        this.count=count;
        this.typeDisplay=typeDisplay;
    }

    @Override
    public Fragment getItem(int position) {
        return(DisplayFragment.newInstance(position, typeDisplay));
    }

    @Override
    public int getCount() {
        return count;
    }
}
