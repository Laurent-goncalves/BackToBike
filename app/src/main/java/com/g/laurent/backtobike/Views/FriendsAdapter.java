package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
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
            configureClickListener(view);
        }

        return view;
    }

    private void associateViews(View view){
        name = view.findViewById(R.id.friend_name);
        name.setVisibility(View.VISIBLE);
        image = view.findViewById(R.id.friend_picture);
        image.setVisibility(View.VISIBLE);
        box = view.findViewById(R.id.checkbox);
    }

    private void configureFriendView(int position){

        // Configure name
        String nameFriend = listFriends.get(position).getName();
        if(nameFriend.length()>15)
            name.setText(nameFriend.substring(0,15));
        else
            name.setText(nameFriend);

        // set color of name view
        if(listFriends.get(position).getHasAgreed()==null){ // NO ANSWER
            name.setBackgroundColor(context.getResources().getColor(R.color.colorGray));
        } else if(listFriends.get(position).getAccepted() && listFriends.get(position).getHasAgreed()) // ACCEPT
            name.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else if(!listFriends.get(position).getHasAgreed())                   // REJECTED
            name.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        else // NO ANSWER
            name.setBackgroundColor(context.getResources().getColor(R.color.colorGray));

        Glide.with(context)
                .load(listFriends.get(position).getPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.icon_friend))
                .into(image);
    }

    private void configureCheckbox(int position){

        ArrayList<String> listFriendsId = friendFragment.getListFriendsSelected();

        if(SelectMode){

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
                    int indexList = UtilsApp.findFriendIndexInListIds(listFriends.get(position).getId(), list);
                    if(indexList!=-1)
                        friendFragment.getListFriendsSelected().remove(indexList);
                }
            });
        } else
            box.setVisibility(View.GONE);
    }

    private void configureClickListener(View view){

        // Long click listener : to actuate Select Mode and show checkboxes
        view.setOnLongClickListener(v -> {
            friendFragment.getListFriendsSelected().clear();
            friendFragment.getCallbackFriendActivity().configureButtonToolbar(true);
            friendFragment.setSelectMode(true);
            friendFragment.configureViews();
            return true;
        });

        // Click listener : to deactivate Select mode and remove checkboxes
        view.setOnClickListener(v -> {
            if(friendFragment.getCallbackFriendActivity()!=null && SelectMode){
                if(box.isChecked())
                    box.setChecked(false);
                else
                    box.setChecked(true);
            }
        });
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

    public void setSelectMode(Boolean selectMode) {
        SelectMode = selectMode;
    }

    public void setListFriends(List<Friend> listFriends) {
        this.listFriends = listFriends;
    }
}
