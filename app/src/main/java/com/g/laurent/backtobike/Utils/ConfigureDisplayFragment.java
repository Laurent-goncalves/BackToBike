package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.g.laurent.backtobike.Controllers.Fragments.DisplayFragment;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Views.DisplayFriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ConfigureDisplayFragment {

    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    @BindView(R.id.text_date) TextView dateView;
    @BindView(R.id.text_hour) TextView timeView;
    @BindView(R.id.layout_calendar_event) LinearLayout dateLayout;
    @BindView(R.id.map_layout) View mapLayout;
    @BindView(R.id.friends_recyclerview) RecyclerView friendsView;
    private DisplayFragment fragment;
    private Route route;
    private BikeEvent bikeEvent;
    private BikeEvent invitation;
    private Context context;
    private String typeDisplay;
    private int position;
    private String userId;

    public ConfigureDisplayFragment(Context context, View view, String typeDisplay, DisplayFragment fragment) {
        this.context = context;
        this.typeDisplay = typeDisplay;
        this.fragment=fragment;
        position = fragment.getPosition();
        route = fragment.getCallbackInvitActivity().getListRoutes().get(position);
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        recoverDatasToDisplay();
        configureDateTime();
        configureMap();
        configureGuests();
    }

    // -------------------------------- 1 - Recover datas
    private void recoverDatasToDisplay(){

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                route = fragment.getCallbackInvitActivity().getListRoutes().get(position);
                break;
            case DISPLAY_MY_EVENTS:
                bikeEvent= fragment.getCallbackInvitActivity().getListEvents().get(position);
                break;
            case DISPLAY_MY_INVITS:
                invitation= fragment.getCallbackInvitActivity().getListInvitations().get(position);
                break;
        }
    }

    // -------------------------------- 2 - Configure dates
    private void configureDateTime(){

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                dateLayout.setVisibility(View.INVISIBLE);
                break;

            case DISPLAY_MY_EVENTS:
                dateLayout.setVisibility(View.VISIBLE);
                dateView.setText(bikeEvent.getDate());
                timeView.setText(bikeEvent.getTime());
                break;

            case DISPLAY_MY_INVITS:
                dateLayout.setVisibility(View.VISIBLE);
                dateView.setText(invitation.getDate());
                timeView.setText(invitation.getTime());
                break;
        }
    }

    // -------------------------------- 3 - Configure map
    private void configureMap(){
        ConfigureMap configMap = new ConfigureMap(context, mapLayout, userId);
        configMap.configureMapLayout(route);
    }

    // -------------------------------- 4 - Configure guests
    private void configureGuests(){

        List<Friend> listFriends = new ArrayList<>();

        // get list of friends
        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                friendsView.setVisibility(View.INVISIBLE);
                break;

            case DISPLAY_MY_EVENTS:
                listFriends = UtilsApp.getListFriendsFromEventFriends(bikeEvent.getListEventFriends(),userId,context);
                friendsView.setVisibility(View.VISIBLE);
                break;

            case DISPLAY_MY_INVITS:
                listFriends = UtilsApp.getListFriendsFromEventFriends(invitation.getListEventFriends(),userId,context);
                friendsView.setVisibility(View.VISIBLE);
                break;
        }

        // configure recyclerView
        if(listFriends.size()>0){
            DisplayFriendsAdapter adapter = new DisplayFriendsAdapter(context,listFriends);
            friendsView.setAdapter(adapter);
        }
    }
}
