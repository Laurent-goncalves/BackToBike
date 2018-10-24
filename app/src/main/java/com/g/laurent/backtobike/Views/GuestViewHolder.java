package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;

public class GuestViewHolder extends RecyclerView.ViewHolder{


    public GuestViewHolder(View itemView) {
        super(itemView);
    }

    public void configureImagesViews(Friend friend, Context context) {

        ImageView photoView = itemView.findViewById(R.id.guest_picture);
        TextView nameView = itemView.findViewById(R.id.guest_name);

        // set name
        if(friend.getName().length()>15)
            nameView.setText(friend.getName());
        else {
            String name = friend.getName().substring(0,15) + "...";
            nameView.setText(name);
        }

        // set picture
        Glide.with(context)
                .load(friend.getPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.icon_friend))
                .into(photoView);

        // set border of image
        if(friend.getAccepted() && friend.getHasAgreed()) // ACCEPT
            photoView.setBackground(context.getResources().getDrawable(R.drawable.background_guest_accept));
        else if(!friend.getHasAgreed())                   // REJECTED
            photoView.setBackground(context.getResources().getDrawable(R.drawable.background_guest_reject));
        else // NO ANSWER
            photoView.setBackground(context.getResources().getDrawable(R.drawable.background_guest_no_answer));
    }
}
