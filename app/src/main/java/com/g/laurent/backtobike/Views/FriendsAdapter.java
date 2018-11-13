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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsApp;
import java.util.ArrayList;
import java.util.List;


public class FriendsAdapter extends BaseAdapter {

    private static final String ONGOING = "ongoing";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private Context context;
    private List<Friend> listFriends;
    private ImageView image;
    private TextView login;
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
            configureClickListener(view, position);
        }

        return view;
    }

    private void associateViews(View view){
        login = view.findViewById(R.id.friend_name);
        login.setVisibility(View.VISIBLE);
        image = view.findViewById(R.id.friend_picture);
        image.setVisibility(View.VISIBLE);
        box = view.findViewById(R.id.checkbox);
    }

    private void configureFriendView(int position){

        // Configure login
        String loginFriend = listFriends.get(position).getLogin();
        if(loginFriend!=null){
            if(loginFriend.length()>12) {
                String loginText = loginFriend.substring(0, 12) + "…";
                login.setText(loginText);
            } else
                login.setText(loginFriend);
        }

        // set color of login view
        if(listFriends.get(position).getHasAgreed().equals(ONGOING)){ // NO ANSWER
            login.setBackgroundColor(context.getResources().getColor(R.color.colorGray));
        } else if(listFriends.get(position).getAccepted().equals(ACCEPTED) && listFriends.get(position).getHasAgreed().equals(ACCEPTED)) // ACCEPT
            login.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else if(listFriends.get(position).getHasAgreed().equals(REJECTED))                   // REJECTED
            login.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        else // NO ANSWER
            login.setBackgroundColor(context.getResources().getColor(R.color.colorGray));

        // Add picture of friend (if exists)
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
            box.setOnCheckedChangeListener((buttonView, isChecked) -> updateListFriendsSelected(isChecked, position));
        } else
            box.setVisibility(View.GONE);
    }

    private void updateListFriendsSelected(Boolean isChecked, int position){

        if (isChecked) { // friend selected
            friendFragment.getListFriendsSelected().add(listFriends.get(position).getId());
        } else { // friend unselected
            ArrayList<String> list = friendFragment.getListFriendsSelected();
            int indexList = UtilsApp.findFriendIndexInListIds(listFriends.get(position).getId(), list);
            if(indexList!=-1)
                friendFragment.getListFriendsSelected().remove(indexList);
        }
    }

    private void configureClickListener(View view, int position){

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

                // check or uncheck box to select or unselect friend
                CheckBox boxView = view.findViewById(R.id.checkbox);
                if(boxView.isChecked()) {
                    boxView.setChecked(false);
                    updateListFriendsSelected(false, position);
                } else {
                    boxView.setChecked(true);
                    updateListFriendsSelected(true, position);
                }
            } else { // show friend's name
                Toast.makeText(context, listFriends.get(position).getName(),Toast.LENGTH_SHORT).show();
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

    // -----------------------------------------------------------------------------------------------------------
    // ----------------------------------------- GETTERS AND SETTERS ---------------------------------------------
    // -----------------------------------------------------------------------------------------------------------

    public void setSelectMode(Boolean selectMode) {
        SelectMode = selectMode;
    }

    public void setListFriends(List<Friend> listFriends) {
        this.listFriends = listFriends;
    }
}
