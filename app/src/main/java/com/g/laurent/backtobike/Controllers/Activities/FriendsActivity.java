package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.AnswerListener;
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class FriendsActivity extends BaseActivity implements CallbackFriendActivity {

    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    private FriendFragment friendFragment;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        assignToolbarViews();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            userId = user.getUid();

            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);

            // Configure swipe to refresh
            mySwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
            mySwipeRefreshLayout.setOnRefreshListener(this::synchronizeWithFirebaseAndRefreshFragment);

            // Check for friends requests
            checkForFriendRequests();

            // Configure and show fragment
            synchronizeWithFirebaseAndRefreshFragment();

            // Configure toolbar
            configureButtonToolbar(false);
        }
    }

    public void synchronizeWithFirebaseAndRefreshFragment(){
        SynchronizeWithFirebase.synchronizeFriends(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
            @Override
            public void onCompleted() {
                checkForFriendRequests();

                if(friendFragment!=null) {
                    friendFragment.configureListFriendsAndView();
                } else
                    configureAndShowFriendFragment();

                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                checkForFriendRequests();

                if(friendFragment!=null)
                    friendFragment.getAdapter().notifyDataSetChanged();
                else
                    configureAndShowFriendFragment();

                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void configureButtonToolbar(Boolean selectMode){

        Button buttonSuppr = toolbar.findViewById(R.id.button_toolbar);
        ImageButton buttonAddFriend = toolbar.findViewById(R.id.button_add_person);

        if(!selectMode){ // if no select mode
            buttonSuppr.setVisibility(View.GONE);
            buttonAddFriend.setVisibility(View.VISIBLE);

            // configure onClickListener
            buttonAddFriend.setOnClickListener(v -> {
                friendFragment.showDialogFriendAdd();
            });

        } else { // if select mode

            buttonSuppr.setVisibility(View.VISIBLE);
            buttonSuppr.setText(getApplicationContext().getResources().getString(R.string.suppr_button));
            buttonAddFriend.setVisibility(View.GONE);

            buttonSuppr.setOnClickListener(v -> {
                // Delete friends selected and update fragment
                friendFragment.proceedToFriendsDeletion();

                // Configure toolbar buttons
                configureButtonToolbar(false);
            });
        }
    }

    public void configureAndShowFriendFragment(){

        // Initialize variables
        friendFragment = new FriendFragment();

        // add a bundle
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_SELECT_MODE, false); // to open fragment NOT in select mode
        friendFragment.setArguments(bundle);

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_friend, friendFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showAlertDialogAddNewFriend(FriendFragment friendFragment) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_friend);

        EditText login = dialog.findViewById(R.id.edittext_login);

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {
            if(login.getText().length()>0)
                friendFragment.checkLogin(login.getText().toString());

            dialog.dismiss();
        });

        // Button CANCEL
        Button dialogButtonCancel = dialog.findViewById(R.id.button_cancel);
        dialogButtonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void checkForFriendRequests(){

        // Recover friends list
        List<Friend> listFriend = FriendsHandler.getListFriends(getApplicationContext(), userId);

        // Check if there are friends who are not accepted
        if(listFriend!=null){
            if(listFriend.size()>0){
                for(Friend friend : listFriend){
                    if(!friend.getAccepted()){
                        showAlertDialogFriendRequest(friend, () -> {});
                    }
                }
            }
        }
    }

    private void showAlertDialogFriendRequest(Friend friend, AnswerListener answerListener) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_friend_request);

        // Define question to ask
        TextView questionView = dialog.findViewById(R.id.question_add_friend);

        String name;
        if(friend.getName()!=null)
            name = friend.getName();
        else
            name = friend.getId();

        String question = getApplicationContext().getResources().getString(R.string.question_friend_request_1) + " " + name +
                " " + getApplicationContext().getResources().getString(R.string.question_friend_request_2);

        questionView.setText(question);

        // Add friend picture
        ImageView imageFriend = dialog.findViewById(R.id.picture_friend);
        if(friend.getPhotoUrl()!=null){
            Glide.with(getApplicationContext())
                    .load(friend.getPhotoUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.icon_friend))
                    .into(imageFriend);
        }

        // Button YES
        Button dialogButtonYes = dialog.findViewById(R.id.button_yes);
        dialogButtonYes.setOnClickListener(v -> {
            Action.acceptFriend(friend, userId, getApplicationContext());
            friendFragment.getListFriendsAccepted().add(friend);
            friendFragment.getAdapter().setListFriends(friendFragment.getListFriendsAccepted());
            friendFragment.getAdapter().notifyDataSetChanged();
            dialog.dismiss();
            answerListener.onAnswer();
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
        });

        // Button NO
        Button dialogButtonNo = dialog.findViewById(R.id.button_no);
        dialogButtonNo.setOnClickListener(v -> {
            Action.rejectFriend(friend,userId,getApplicationContext());
            dialog.dismiss();
            answerListener.onAnswer();
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
        });

        dialog.show();
    }

}
