package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import java.util.List;


public class FriendsAdapter extends BaseAdapter {

    private Context context;
    private List<Friend> listFriends;
    private ImageView image;
    private TextView name;
    private LinearLayout addFriend;
    private FriendFragment friendFragment;

    public FriendsAdapter(Context context, List<Friend> listFriends, FriendFragment friendFragment) {
        this.context = context;
        this.listFriends=listFriends;
        this.friendFragment=friendFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(inflater!=null) {
            view = inflater.inflate(R.layout.friend_item, parent,false);

            name = view.findViewById(R.id.friend_name);
            name.setVisibility(View.VISIBLE);
            image = view.findViewById(R.id.friend_picture);
            image.setVisibility(View.VISIBLE);
            addFriend = view.findViewById(R.id.button_add_friend);

            if(position==listFriends.size()-1){ // if last item of the list

                // configure views
                name.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
                addFriend.setVisibility(View.VISIBLE);

                // add onclicklistener
                addFriend.setOnClickListener(v -> friendFragment.showDialogFriendAdd());

            } else {

                // configure views
                addFriend.setVisibility(View.GONE);
                name.setText(listFriends.get(position).getName());

                Glide.with(context)
                        .load(listFriends.get(position).getPhotoUrl())
                        //.apply(new RequestOptions().placeholder(R.drawable.placeholder))
                        .into(image);

            }
        }

        return view;
    }

    @Override
    public int getCount() {
        if(listFriends!=null){
            return listFriends.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
