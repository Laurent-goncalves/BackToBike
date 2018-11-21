package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Configurations.ConfigureInvitFragment;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckAndSendInvitation {

    private static final String ACCEPTED = "accepted";
    private final static String DISPLAY_MY_EVENTS ="display_my_events";
    private final static String ONGOING = "ongoing";
    @BindView(R.id.date_view) TextView dateView;
    @BindView(R.id.time_view) TextView timeView;
    @BindView(R.id.comments_edit_text) EditText commentsView;
    @BindView(R.id.spinner_list_routes) Spinner listRoutesView;
    private ConfigureInvitFragment config;
    private FirebaseUser firebaseUser;
    private Context context;

    public CheckAndSendInvitation(ConfigureInvitFragment config, View view, Context context) {

        ButterKnife.bind(this,view);

        // Assign variables
        this.config = config;
        this.context = context;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(areDataOkForInvitation()){

            // Build bike Event
            BikeEvent bikeEvent = buildBikeEvent();

            Route route = RouteHandler.getRoute(context, bikeEvent.getIdRoute(), firebaseUser.getUid());
            List<RouteSegment> listRouteSegments = RouteHandler.getRouteSegments(context,route.getId(), firebaseUser.getUid());
            route.setListRouteSegment(listRouteSegments);
            bikeEvent.setRoute(route);
            bikeEvent.setStatus(ACCEPTED);

            // Add an event in phone database and Firebase, and send invitation to guests
            Action.addBikeEvent(bikeEvent,firebaseUser.getUid(),context);

            // Display message to user
            if(bikeEvent.getListEventFriends().size()>0)
                Toast.makeText(context,context.getResources().getString(R.string.invitation_send),Toast.LENGTH_LONG).show(); // send invitations to friends, if at least one friend
            else
                Toast.makeText(context,context.getResources().getString(R.string.bike_event_saved),Toast.LENGTH_LONG).show(); // bike event saved

            // Set alarms bike event
            config.getCallbackEventActivity().configureAlarmManager(bikeEvent);

            // Launch displayActivity
            config.getCallbackEventActivity().launchDisplayActivity(DISPLAY_MY_EVENTS, bikeEvent.getId());
        }
    }

    private Boolean areDataOkForInvitation(){

        Boolean answer = false;

        // an invitation can be sent if there is at least a date and a time
        if(dateView.getText()!=null){
            if(timeView.getText()!=null){
                if(dateView.getText().length()>0){
                    if(timeView.getText().length()>0){
                        if(UtilsTime.isEventDateAfterNow(dateView.getText().toString(), timeView.getText().toString())){
                            if(listRoutesView.getSelectedItemPosition()!=0){
                                answer = true;
                            } else
                                Toast.makeText(context, context.getResources().getString(R.string.no_route), Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(context, context.getResources().getString(R.string.date_before_now), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, context.getResources().getString(R.string.no_time), Toast.LENGTH_LONG).show();
                }  else
                    Toast.makeText(context, context.getResources().getString(R.string.no_date), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, context.getResources().getString(R.string.no_time), Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(context, context.getResources().getString(R.string.no_date), Toast.LENGTH_LONG).show();

        return answer;
    }

    private BikeEvent buildBikeEvent(){

        // Assign variables
        String user_id = firebaseUser.getUid();
        Invitation invitation = config.getCallbackEventActivity().getInvitation();
        String date = invitation.getDate();
        String time = invitation.getTime();
        String idEvent = user_id + "_" + date + "_" + time;
        idEvent = idEvent.replace("/","_");
        String comments = invitation.getComments();
        Route myRoute = RouteHandler.getRoute(context, invitation.getIdRoute(), user_id);
        int idRoute = RouteHandler.insertRouteEvent(context, myRoute, idEvent, user_id);

        // Create list of event friends
        ArrayList<String> listIdFriends = invitation.getListIdFriends();
        List<EventFriends> listEventFriends = new ArrayList<>();

        if(listIdFriends!=null){
            if(listIdFriends.size()>0){
                for(String idFriend : listIdFriends){
                    String login = FriendsHandler.getFriend(context, idFriend,user_id).getLogin();
                    listEventFriends.add(new EventFriends(0,idEvent,idFriend, login,ONGOING));
                }
            }
        }

        return new BikeEvent(idEvent, user_id, date, time, idRoute, comments, ONGOING, listEventFriends);
    }
}
