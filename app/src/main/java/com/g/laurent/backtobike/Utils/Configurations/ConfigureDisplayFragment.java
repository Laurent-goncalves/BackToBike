package com.g.laurent.backtobike.Utils.Configurations;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.GuestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ConfigureDisplayFragment {

    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    private static final String CANCELLED = "cancelled";
    private static final String LOGIN_SHARED ="login_shared";
    @BindView(R.id.text_date) TextView dateView;
    @BindView(R.id.text_hour) TextView timeView;
    @BindView(R.id.layout_calendar_event) LinearLayout dateLayout;
    @BindView(R.id.map_layout) View mapLayout;
    @BindView(R.id.comments_view) TextView commentView;
    @BindView(R.id.friends_recyclerview) RecyclerView friendsView;
    @BindView(R.id.cancelled_text_rotated) TextView cancelText;
    private Route route;
    private BikeEvent bikeEvent;
    private Context context;
    private String typeDisplay;
    private String userId;
    private FirebaseUser user;

    public ConfigureDisplayFragment(Context context, View view, String typeDisplay, Route route) {
        this.context = context;
        this.typeDisplay = typeDisplay;
        this.route=route;
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        configureViews();
    }

    public ConfigureDisplayFragment(Context context, View view, String typeDisplay, FirebaseUser user, BikeEvent bikeEvent) {
        this.context = context;
        this.typeDisplay = typeDisplay;
        this.bikeEvent=bikeEvent;
        this.user=user;
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        configureViews();
    }

    private void configureViews(){
        configureDateTime();
        configureMap();
        configureGuests();
        configureCommentView();
        configureCancelText();
    }

    // -------------------------------- 1 - Configure dates
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

    // -------------------------------- 2 - Configure map
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

    // -------------------------------- 3 - Configure comments for bikeEvent and invitation

    private void configureCommentView(){

        commentView.setMovementMethod(new ScrollingMovementMethod());

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                commentView.setVisibility(View.GONE);
                break;

            case DISPLAY_MY_EVENTS:
                commentView.setVisibility(View.VISIBLE);
                commentView.setText(bikeEvent.getComments());
                UtilsApp.resizeTextView(commentView);
                commentView.setMovementMethod(new ScrollingMovementMethod());
                break;

            case DISPLAY_MY_INVITS:
                commentView.setVisibility(View.VISIBLE);
                // configure recyclerView
                commentView.setText(bikeEvent.getComments());
                UtilsApp.resizeTextView(commentView);
                commentView.setMovementMethod(new ScrollingMovementMethod());
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

        // Configure list event friends
        List<EventFriends> listEventFriendsToShow = UtilsApp.positionOrganizerAtStartList(context, listEventFriends, bikeEvent, getUserAsEventFriend(listEventFriends), bikeEvent.getOrganizerId());

        // Set the recyclerView in horizontal direction
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        GuestsAdapter adapter = new GuestsAdapter(context, userId, bikeEvent.getOrganizerId(), listEventFriendsToShow);
        friendsView.setAdapter(adapter);

        // Set layout manager to position the items
        friendsView.setLayoutManager(layoutManager);
    }

    private EventFriends getUserAsEventFriend(List<EventFriends> listEventFriends){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
        String myLogin = sharedPreferences.getString(LOGIN_SHARED,null);
        return new EventFriends(0,null, user.getUid(), myLogin, UtilsApp.getAcceptanceEventUser(user.getUid(), listEventFriends));
    }

    // -------------------------------- 5 - Configure cancel text

    private void configureCancelText(){

        switch(typeDisplay){

            case DISPLAY_MY_EVENTS:
                if(bikeEvent.getStatus().equals(CANCELLED)){
                    cancelText.setVisibility(View.VISIBLE);
                }
                break;

            case DISPLAY_MY_INVITS:
                if(bikeEvent.getStatus().equals(CANCELLED)){
                    cancelText.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
