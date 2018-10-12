package com.g.laurent.backtobike.Controllers.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g.laurent.backtobike.Controllers.Activities.FriendsActivity;
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Views.FriendsAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    @BindView(R.id.gridview_check_box) GridView gridView;
    @BindView(R.id.my_id_view) TextView myIdView;
    private FriendsActivity friendsActivity;
    private Context context;
    private List<Friend> listFriends;
    private CallbackFriendActivity callbackFriendActivity;
    private FirebaseUser firebaseUser;
    private String myLogin;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, view);

        friendsActivity = (FriendsActivity) getActivity();
        context = friendsActivity.getApplicationContext();
        myLogin = "lolo91";

        FirebaseApp.initializeApp(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        listFriends = FriendsHandler.getListFriends(context);
        listFriends.add(new Friend()); // add item for "add a friend"

        configureViews();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof CallbackFriendActivity){
            callbackFriendActivity = (CallbackFriendActivity) context;
        }
    }

    private void configureViews(){

        // configure gridView
        gridView.setAdapter(new FriendsAdapter(context, listFriends, this));

        // configure login view
        myIdView.setText("My login : " + myLogin);
    }

    public void showDialogFriendAdd() {
        callbackFriendActivity.showAlertDialogAddNewFriend(this);
    }

    public void checkLogin(String login) {

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        // Check if login is different than user's login, login is not among friends of the user and if the login exists on Firebase
        firebaseRecover.isLoginNotAmongUserFriends(login, firebaseUser.getUid(), new OnDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {
                saveNewFriend(friend);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveNewFriend(Friend friend){

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        // Add friend to user Firebase "my_friends" with status "false" as accepted
        firebaseUpdate.updateFriend(firebaseUser.getUid(), friend);

        // Add user in friend Firebase "my_friends" with status "false" as accepted
        Friend user = new Friend(firebaseUser.getUid(),myLogin,firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString(),false);
        firebaseUpdate.updateFriend(friend.getId(),user);

        // Add friend in phone database
        FriendsHandler.insertNewFriend(context, friend);

        // Update layout
        listFriends.add(listFriends.size()-1, friend); // add new friend before the last item
        configureViews();
    }
}
