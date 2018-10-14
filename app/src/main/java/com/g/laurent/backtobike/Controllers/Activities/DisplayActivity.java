package com.g.laurent.backtobike.Controllers.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDisplayActivity;
import com.g.laurent.backtobike.Views.PageAdapter;

import java.util.List;

public class DisplayActivity extends AppCompatActivity implements CallbackDisplayActivity {

    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private String typeDisplay;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Bundle extras = getIntent().getExtras();
        SaveAndRestoreDisplayActivity.restoreData(extras,this);
        configureAndShowDisplayFragmentsInViewPager();
    }



    public void configureAndShowDisplayFragmentsInViewPager(){

        ViewPager pager = findViewById(R.id.activity_display_viewpager);
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), typeDisplay));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDisplayActivity.saveData(outState,this);
    }

    public String getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
