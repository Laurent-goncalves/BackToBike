package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;


public class EventFriendsHandler {


    public static void addGuestView(Friend friend, Context context, LinearLayout guestsLayout, ConfigureInvitFragment config) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View guestView = inflater.inflate(R.layout.event_friend_item, null);

        // Name guest
        TextView guestName = guestView.findViewById(R.id.name_event_friend);

        if(friend.getName().length()<20)
            guestName.setText(friend.getName());
        else
            guestName.setText(friend.getName().substring(0,20)); // cut the name if too long

        // Button delete
        ImageView buttonDelete = guestView.findViewById(R.id.button_delete);
        buttonDelete.setColorFilter(context.getResources().getColor(R.color.colorButtonDelete));
        buttonDelete.setOnClickListener(v -> deleteGuestView(friend,guestView,guestsLayout,config));

        // add view
        guestsLayout.addView(guestView);
    }

    private static void deleteGuestView(Friend friend, View guestView, LinearLayout guestsLayout, ConfigureInvitFragment config){

        // remove friend from listEventFriend
        int index = UtilsApp.findFriendIndexInListIds(friend.getId(), config.getInvitFragment().getCallbackEventActivity().getInvitation().getListIdFriends());
        if(index!=-1)
            config.getInvitFragment().getCallbackEventActivity().getInvitation().getListIdFriends().remove(index);

        // remove view in linearLayout
        guestsLayout.removeView(guestView);
    }
}
