package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import java.util.List;


public class DisplayFriendsAdapter extends RecyclerView.Adapter<GuestViewHolder> {

    private Context context;
    private List<Friend> listFriends;

    public DisplayFriendsAdapter(Context context, List<Friend> listFriends) {
        this.context = context;
        this.listFriends = listFriends;
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
        holder.configureImagesViews(listFriends.get(holder.getAdapterPosition()), context);
    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }
}
