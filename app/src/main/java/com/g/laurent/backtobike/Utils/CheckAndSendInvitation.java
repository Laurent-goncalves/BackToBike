package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckAndSendInvitation {

    private static final String ONGOING = "ongoing";
    @BindView(R.id.date_view) TextView dateView;
    @BindView(R.id.time_view) TextView timeView;
    @BindView(R.id.comments_edit_text) EditText commentsView;
    private ConfigureInvitFragment config;
    private FirebaseUser firebaseUser;
    private Context context;

    public CheckAndSendInvitation(ConfigureInvitFragment config, View view, Context context) {

        ButterKnife.bind(this,view);
        this.config = config;
        this.context = context;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(areDataOkForInvitation()){

            // Build bike Event
            BikeEvent bikeEvent = buildBikeEvent();

            // Add an event in phone database
            BikeEventHandler.insertNewBikeEvent(context, bikeEvent);

            // Add an event on Firebase in user's "my_events"
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
            firebaseUpdate.updateMyBikeEvent(firebaseUser.getUid(), bikeEvent);

            // Add an event on Firebase in friend's "my_invitations"
            Route route = RouteHandler.getRoute(context, bikeEvent.getIdRoute());
            List<RouteSegment> listRouteSegments = RouteHandler.getRouteSegments(context,route.getId());
            route.setListRouteSegment(listRouteSegments);

            bikeEvent.setRoute(route);

            firebaseUpdate.addInvitationGuests(bikeEvent);

            // Display message to user
            if(bikeEvent.getListEventFriends().size()>0)
                Toast.makeText(context,context.getResources().getString(R.string.invitation_send),Toast.LENGTH_LONG).show(); // send invitations to friends, if at least one friend
            else
                Toast.makeText(context,context.getResources().getString(R.string.bike_event_saved),Toast.LENGTH_LONG).show(); // bike event saved
        }
    }

    private Boolean areDataOkForInvitation(){

        Boolean answer = false;

        // an invitation can be sent if there is at least a date and a time
        if(dateView.getText()!=null && timeView.getText()!=null){
            if(dateView.getText().length()>0 && timeView.getText().length()>0){
                answer = true;
            }
        }

        return answer;
    }

    private BikeEvent buildBikeEvent(){

        String user_id = firebaseUser.getUid();
        Invitation invitation = config.getCallbackInvitActivity().getInvitation();

        String date = invitation.getDate();
        String time = invitation.getTime();
        String idEvent = user_id + "_" + date + "_" + time;
        idEvent = idEvent.replace("/","_");
        String comments = invitation.getComments();
        int idRoute = invitation.getIdRoute();
        ArrayList<String> listIdFriends = invitation.getListIdFriends();

        List<EventFriends> listEventFriends = new ArrayList<>();

        if(listIdFriends!=null){
            if(listIdFriends.size()>0){
                for(String idFriend : listIdFriends)
                    listEventFriends.add(new EventFriends(0,idEvent,idFriend,ONGOING));
            }
        }

        return new BikeEvent(idEvent, user_id, date, time, idRoute, comments, ONGOING, listEventFriends);
    }
}
