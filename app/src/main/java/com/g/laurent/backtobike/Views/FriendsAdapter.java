package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.g.laurent.backtobike.Utils.UtilsApp;

import java.util.ArrayList;
import java.util.List;


public class FriendsAdapter extends BaseAdapter {

    private Context context;
    private List<Friend> listFriends;
    private ImageView image;
    private TextView name;
    private LinearLayout addFriend;
    private CheckBox box;
    private FriendFragment friendFragment;
    private Boolean SelectMode;

    public FriendsAdapter(Context context, List<Friend> listFriends, Boolean SelectMode, FriendFragment friendFragment) {
        this.context = context;
        this.listFriends=listFriends;
        this.SelectMode=SelectMode;
        this.friendFragment=friendFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(inflater!=null) {
            view = inflater.inflate(R.layout.friend_item, parent,false);

            associateViews(view);

            configureFriendView(position);

            configureCheckbox(position);

            if(SelectMode && position==listFriends.size()-1)
                view.setVisibility(View.GONE);
        }

        return view;
    }

    private void associateViews(View view){
        name = view.findViewById(R.id.friend_name);
        name.setVisibility(View.VISIBLE);
        image = view.findViewById(R.id.friend_picture);
        image.setVisibility(View.VISIBLE);
        addFriend = view.findViewById(R.id.button_add_friend);
        box = view.findViewById(R.id.checkbox);
    }

    private void configureFriendView(int position){

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

    private void configureCheckbox(int position){

        ArrayList<String> listFriendsId = friendFragment.getListFriendsSelected();

        if(SelectMode && position!=listFriends.size()-1){

            // if friend is among friend already selected, check the box
            int index = UtilsApp.findFriendIndexInListIds(listFriends.get(position).getId(), listFriendsId);
            if(index!=-1)
                box.setChecked(true);

            // create onCheckedChangedListener for the box
            box.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) { // friend selected
                    friendFragment.getListFriendsSelected().add(listFriends.get(position).getId());
                } else { // friend unselected
                    ArrayList<String> list = friendFragment.getListFriendsSelected();
                    int indexList = UtilsApp.findFriendIndexInListIds(list.get(position), list);
                    if(indexList!=-1)
                        friendFragment.getListFriendsSelected().remove(indexList);
                }
            });
        } else
            box.setVisibility(View.GONE);
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
