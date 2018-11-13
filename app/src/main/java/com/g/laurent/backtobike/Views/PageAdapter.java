package com.g.laurent.backtobike.Views;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import com.g.laurent.backtobike.Controllers.Fragments.DisplayFragment;


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
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
