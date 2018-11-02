package com.g.laurent.backtobike.Utils.Configurations;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
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
    private Route route;
    private BikeEvent bikeEvent;
    private Context context;
    private String typeDisplay;
    private String userId;
    private DisplayFriendsAdapter adapter;

    public ConfigureDisplayFragment(Context context, View view, String typeDisplay, Route route) {
        this.context = context;
        this.typeDisplay = typeDisplay;
        this.route=route;
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        recoverDatasToDisplay();
        configureDateTime();
        configureMap();
        configureGuests();
    }

    public ConfigureDisplayFragment(Context context, View view, String typeDisplay, BikeEvent bikeEvent) {
        this.context = context;
        this.typeDisplay = typeDisplay;
        this.bikeEvent=bikeEvent;
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        recoverDatasToDisplay();
        configureDateTime();
        configureMap();
        configureGuests();
    }

    // -------------------------------- 1 - SynchronizeWithDatabase datas
    private void recoverDatasToDisplay() {

        if(typeDisplay.equals(DISPLAY_MY_EVENTS)){
            if(UtilsApp.isInternetAvailable(context)){
                try {
                    SynchronizeWithFirebase.synchronizeOneEvent(userId, bikeEvent.getId(), context, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                dateView.setText(bikeEvent.getDate());
                timeView.setText(bikeEvent.getTime());
                break;
        }
    }

    // -------------------------------- 3 - Configure map
    private void configureMap(){
        ConfigureMap configMap = new ConfigureMap(context, mapLayout);

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                configMap.configureMapLayout(route);
                break;
            case DISPLAY_MY_EVENTS:
                configMap.configureMapLayout(bikeEvent.getRoute());
                configMap.configureButtonAddToMyRoutes(context, userId, bikeEvent);
                break;
            case DISPLAY_MY_INVITS:
                configMap.configureMapLayout(bikeEvent.getRoute());
                break;
        }
    }

    // -------------------------------- 4 - Configure guests
    private void configureGuests(){

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                friendsView.setVisibility(View.INVISIBLE);
                break;

            case DISPLAY_MY_EVENTS:
                friendsView.setVisibility(View.VISIBLE);
                // configure recyclerView
                configureGuestsRecyclerView(context, userId, bikeEvent.getListEventFriends());
                break;

            case DISPLAY_MY_INVITS:
                friendsView.setVisibility(View.VISIBLE);
                // configure recyclerView
                configureGuestsRecyclerView(context, userId, bikeEvent.getListEventFriends());
                break;
        }
    }

    private void configureGuestsRecyclerView(Context context, String userId, List<EventFriends> listEventFriends){

        // Set the recyclerView in horizontal direction
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        adapter = new DisplayFriendsAdapter(context, userId, bikeEvent.getOrganizerId(), listEventFriends);
        friendsView.setAdapter(adapter);

        // Set layout manager to position the items
        friendsView.setLayoutManager(layoutManager);
    }

    private static List<Friend> getListFriendsFromEventFriends(List<EventFriends> listEventFriends, String userId, Context context){

        List<Friend> listFriends = new ArrayList<>();

        if(listEventFriends!=null){
            if(listEventFriends.size()!=0){
                for(EventFriends eventFriends : listEventFriends){

                    Friend friend = FriendsHandler.getFriend(context, eventFriends.getIdFriend(),userId);
                    friend.setLogin(eventFriends.getLogin());

                    listFriends.add(friend);
                }
            }
        }
        return listFriends;
    }


}
