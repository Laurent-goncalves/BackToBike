package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private static final String BUNDLE_TYPE_ROUTES ="bundle_type_routes";
    private static final String BUNDLE_TYPE_EVENTS ="bundle_type_events";
    private static final String BUNDLE_TYPE_INVITS ="bundle_type_invits";
    @BindView(R.id.text_date) TextView dateView;
    @BindView(R.id.text_hour) TextView timeView;
    @BindView(R.id.layout_calendar_event) LinearLayout dateLayout;
    @BindView(R.id.map_layout) View mapLayout;
    @BindView(R.id.arrow_back) ImageView arrowBack;
    @BindView(R.id.arrow_next) ImageView arrowNext;
    @BindView(R.id.friends_recyclerview) RecyclerView friendsView;
    @BindView(R.id.left_button) Button buttonLeft;
    @BindView(R.id.right_button) Button buttonRight;
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
        userId = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(this,view);
        recoverDatasToDisplay();
        configureDateTime();
        configureMap();
        configureArrows();
        configureGuests();
        configureButtons();
    }

    // -------------------------------- 1 - Recover datas
    private void recoverDatasToDisplay(){

        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:
                route = fragment.getCallbackInvitActivity().getListRoutes().get(position);
                break;
            case BUNDLE_TYPE_EVENTS:
                bikeEvent= fragment.getCallbackInvitActivity().getListEvents().get(position);
                break;
            case BUNDLE_TYPE_INVITS:
                invitation= fragment.getCallbackInvitActivity().getListInvitations().get(position);
                break;
        }
    }

    // -------------------------------- 2 - Configure dates
    private void configureDateTime(){

        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:
                dateLayout.setVisibility(View.INVISIBLE);
                break;

            case BUNDLE_TYPE_EVENTS:
                dateLayout.setVisibility(View.VISIBLE);
                dateView.setText(bikeEvent.getDate());
                timeView.setText(bikeEvent.getTime());
                break;

            case BUNDLE_TYPE_INVITS:
                dateLayout.setVisibility(View.VISIBLE);
                dateView.setText(invitation.getDate());
                timeView.setText(invitation.getTime());
                break;
        }
    }

    // -------------------------------- 3 - Configure map
    private void configureMap(){
        ConfigureMap configMap = new ConfigureMap(context, mapLayout);
        configMap.configureMapLayout(route);
    }

    // -------------------------------- 4 - Configure arrows
    private void configureArrows(){

        int sizeList = 0;

        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:
                sizeList = fragment.getCallbackInvitActivity().getListRoutes().size();
                break;
            case BUNDLE_TYPE_EVENTS:
                sizeList = fragment.getCallbackInvitActivity().getListEvents().size();
                break;
            case BUNDLE_TYPE_INVITS:
                sizeList = fragment.getCallbackInvitActivity().getListInvitations().size();
                break;
        }

        UtilsApp.configureArrows(position, sizeList, arrowBack, arrowNext, fragment);
    }

    // -------------------------------- 5 - Configure guests
    private void configureGuests(){

        List<Friend> listFriends = new ArrayList<>();

        // get list of friends
        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:
                friendsView.setVisibility(View.INVISIBLE);
                break;

            case BUNDLE_TYPE_EVENTS:
                listFriends = UtilsApp.getListFriendsFromEventFriends(bikeEvent.getListEventFriends(),context);
                friendsView.setVisibility(View.VISIBLE);
                break;

            case BUNDLE_TYPE_INVITS:
                listFriends = UtilsApp.getListFriendsFromEventFriends(invitation.getListEventFriends(),context);
                friendsView.setVisibility(View.VISIBLE);
                break;
        }

        // configure recyclerView
        if(listFriends.size()>0){
            DisplayFriendsAdapter adapter = new DisplayFriendsAdapter(context,listFriends);
            friendsView.setAdapter(adapter);
        }
    }

    // -------------------------------- 6 - Configure buttons
    private void configureButtons(){
        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:

                // DELETE ROUTE
                buttonLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                // CHANGE ROUTE
                buttonRight.setCompoundDrawables(null,context.getResources().getDrawable(R.drawable.baseline_edit_white_48),null,null);
                buttonLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPolylineNotComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.change));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineNotComplete));

                break;

            case BUNDLE_TYPE_EVENTS:

                // CANCEL EVENT
                buttonLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                buttonRight.setVisibility(View.GONE);
                break;

            case BUNDLE_TYPE_INVITS:

                // REJECT INVITATION
                buttonLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.colorReject), PorterDuff.Mode.SRC_IN);
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

                // ACCEPT INVITATION
                buttonRight.setCompoundDrawables(null,context.getResources().getDrawable(R.drawable.baseline_check_circle_white_48),null,null);
                buttonLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPolylineNotComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.change));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineNotComplete));

                break;
        }

        setOnClickListenersButtons();
    }

    private void setOnClickListenersButtons(){
        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:
                // DELETE ROUTE
                buttonLeft.setOnClickListener(v -> Action.deleteRoute(route, userId, context));
                // CHANGE ROUTE
                buttonRight.setOnClickListener(v -> fragment.getCallbackInvitActivity().launchTraceActivity(route));
                break;

            case BUNDLE_TYPE_EVENTS:
                // CANCEL EVENT
                buttonLeft.setOnClickListener(v -> Action.cancelBikeEvent(bikeEvent,userId,context));
                break;

            case BUNDLE_TYPE_INVITS:
                // REJECT INVITATION
                buttonLeft.setOnClickListener(v -> Action.rejectInvitation(bikeEvent,userId,context));
                // ACCEPT INVITATION
                buttonRight.setOnClickListener(v -> Action.acceptInvitation(bikeEvent,userId,context));
                break;
        }
    }
}
