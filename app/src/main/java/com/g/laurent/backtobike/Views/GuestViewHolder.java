package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FriendsHandler;

public class GuestViewHolder extends RecyclerView.ViewHolder{

    private static final String ONGOING = "ongoing";
    private static final String ACCEPTED = "accepted";

    public GuestViewHolder(View itemView) {
        super(itemView);
    }

    public void configureImagesViews(EventFriends eventFriend, Context context, String userId) {

        ImageView photoView = itemView.findViewById(R.id.guest_picture);
        TextView nameView = itemView.findViewById(R.id.guest_name);

        // set login
        if(eventFriend.getLogin().length()<=15)
            nameView.setText(eventFriend.getLogin());
        else {
            String name = eventFriend.getLogin().substring(0,15) + "...";
            nameView.setText(name);
        }

        // set picture
        Friend friend = FriendsHandler.getFriend(context, eventFriend.getIdFriend(), userId);
        Glide.with(context)
                .load(friend.getPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.icon_friend))
                .into(photoView);

        // set border of image
        if(eventFriend.getAccepted().equals(ONGOING))             // NO ANSWER
            nameView.setBackgroundColor(context.getResources().getColor(R.color.colorGray));
        else if(eventFriend.getAccepted().equals(ACCEPTED))       // ACCEPT
            nameView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else // REJECTED
            nameView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
    }
}
