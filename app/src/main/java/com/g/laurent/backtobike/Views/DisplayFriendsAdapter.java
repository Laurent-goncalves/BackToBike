package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import java.util.List;


public class DisplayFriendsAdapter extends RecyclerView.Adapter<GuestViewHolder> {

    private Context context;
    private List<EventFriends> listEventFriends;
    private String userId;

    public DisplayFriendsAdapter(Context context, String userId, List<EventFriends> listEventFriends) {
        this.context = context;
        this.userId=userId;
        this.listEventFriends = listEventFriends;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_viewholder, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        holder.configureImagesViews(listEventFriends.get(holder.getAdapterPosition()), context, userId);
    }

    @Override
    public int getItemCount() {
        return listEventFriends.size();
    }
}
