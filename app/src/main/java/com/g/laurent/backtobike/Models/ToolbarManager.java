package com.g.laurent.backtobike.Models;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.g.laurent.backtobike.Controllers.Activities.FriendsActivity;
import com.g.laurent.backtobike.Controllers.Activities.InvitActivity;
import com.g.laurent.backtobike.R;



public class ToolbarManager {

    private Toolbar toolbar;
    private Context context;
    private ImageButton hamburger;
    private Button buttonToolbar;

    public void configureToolbar(FriendsActivity friendsActivity){

        context = friendsActivity.getApplicationContext();

        if(friendsActivity!=null) {
            toolbar = friendsActivity.findViewById(R.id.activity_main_toolbar);
            friendsActivity.setSupportActionBar(toolbar);
        }

        finalizeToolbarConfiguration();
    }

    public void configureToolbar(InvitActivity invitActivity){

        context = invitActivity.getApplicationContext();

        if(invitActivity!=null) {
            toolbar = invitActivity.findViewById(R.id.activity_main_toolbar);
            invitActivity.setSupportActionBar(toolbar);
        }

        finalizeToolbarConfiguration();
    }

    private void finalizeToolbarConfiguration(){

        if(toolbar!=null){

            // configure hamburger menu to open the navigation drawer
            hamburger = toolbar.findViewById(R.id.button_hamburger);
            // TODO configure : hamburger.setOnClickListener(v -> friendsActivity.getDrawerLayout().openDrawer(Gravity.START));
            // TODO configure :  friendsActivity.getNavigationView().setNavigationItemSelectedListener(this);

            // Assign button from toolbar
            buttonToolbar = toolbar.findViewById(R.id.button_toolbar);


            //Assign and edit toolbar title
            TextView title_toolbar = toolbar.findViewById(R.id.title_toolbar);
            title_toolbar.setText(context.getResources().getString(R.string.app_name));

        }
    }


    public void configureButtonToolbar(Boolean buttonVisible, FriendsActivity friendsActivity){

        if(!buttonVisible){ // if no button needed, remove it
            buttonToolbar.setVisibility(View.GONE);
        } else { // if button needed, make it visible and configure click

            buttonToolbar.setVisibility(View.VISIBLE);

            buttonToolbar.setOnClickListener(v -> {
                // TODO : DELETE friends

            });
        }
    }

    public void configureButtonToolbar(Boolean buttonVisible, InvitActivity invitActivity){

        if(!buttonVisible){ // if no button needed, remove it
            buttonToolbar.setVisibility(View.GONE);
        } else { // if button needed, make it visible and configure click

            buttonToolbar.setText(context.getResources().getString(R.string.ok));
            buttonToolbar.setVisibility(View.VISIBLE);

            buttonToolbar.setOnClickListener(v -> {
                // Display invitfragment and configure guests selected
                invitActivity.getInvitation().setListIdFriends(invitActivity.getFriendFragment().getListFriendsSelected());
                invitActivity.backToInvitFragment();
            });
        }
    }
}
