package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.AnswerListener;
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;


public class FriendsActivity extends BaseActivity implements CallbackFriendActivity {

    private static final String ACCEPTED = "accepted";
    private static final String ONGOING = "ongoing";
    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    private FriendFragment friendFragment;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        assignToolbarViews();
        savePreviousPage(MENU_MY_FRIENDS);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            userId = user.getUid();

            // Define counters
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);

            // Configure swipe to refresh
            mySwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
            mySwipeRefreshLayout.setOnRefreshListener(this::synchronizeWithFirebaseAndRefreshFragment);

            // Configure and show fragment
            synchronizeWithFirebaseAndRefreshFragment();

            // Configure toolbar
            configureButtonToolbar(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userId!=null && UtilsApp.isInternetAvailable(getApplicationContext()))
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
    }

    @Override
    protected void refreshActivity(){
        defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------------- CONFIGURE UI ---------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public void synchronizeWithFirebaseAndRefreshFragment(){
        if(UtilsApp.isInternetAvailable(getApplicationContext())){
            SynchronizeWithFirebase.synchronizeFriends(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
                @Override
                public void onCompleted() {
                    checkForFriendRequests();

                    // Configure friendFragment
                    if(friendFragment!=null) {
                        friendFragment.configureViews();
                    } else
                        configureAndShowFriendFragment();

                    mySwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(String error) {
                    checkForFriendRequests();

                    // Configure friendFragment
                    if(friendFragment!=null)
                        friendFragment.getAdapter().notifyDataSetChanged();
                    else
                        configureAndShowFriendFragment();

                    mySwipeRefreshLayout.setRefreshing(false);
                }
            });

        } else {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void configureButtonToolbar(Boolean selectMode){

        Button buttonSuppr = toolbar.findViewById(R.id.button_toolbar);
        ImageButton buttonAddFriend = toolbar.findViewById(R.id.button_add_person);

        if(!selectMode){ // if no select mode
            buttonSuppr.setVisibility(View.GONE);
            buttonAddFriend.setVisibility(View.VISIBLE);

            // configure onClickListener
            buttonAddFriend.setOnClickListener(v -> showAlertDialogAddNewFriend(friendFragment));

        } else { // if select mode

            buttonSuppr.setVisibility(View.VISIBLE);
            buttonSuppr.setText(getApplicationContext().getResources().getString(R.string.suppr_button));
            buttonAddFriend.setVisibility(View.GONE);

            buttonSuppr.setOnClickListener(v -> {
                // Delete friends selected and update fragment
                if(friendFragment!=null)
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

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------- ACTIONS ALERT DIALOGS ------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    @Override
    public void showAlertDialogAddNewFriend(FriendFragment friendFragment) {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_friend);

        EditText login = dialog.findViewById(R.id.edittext_login);

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {
            if(UtilsApp.isInternetAvailable(getApplicationContext())) {
                if (login.getText().length() > 0)
                    checkLogin(friendFragment, login.getText().toString());
                else
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_login_format), Toast.LENGTH_LONG).show();
            } else {
                if (login.getText().length() > 0) {
                    Action.saveLoginsInSharedPref(sharedPref, login.getText().toString());
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.friend_added_later), Toast.LENGTH_LONG).show();
                }  else
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_login_format), Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        // Button CANCEL
        Button dialogButtonCancel = dialog.findViewById(R.id.button_cancel);
        dialogButtonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void checkLogin(FriendFragment friendFragment, String login){
        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.checkLogin(getApplicationContext(), userId, login, new OnLoginChecked() {
            @Override
            public void onSuccess(Friend friend) {
                // Add friend to database and Firebase
                friend.setHasAgreed(ONGOING);
                friend.setAccepted(ACCEPTED);
                Action.addNewFriend(friend, UtilsApp.getUserFromFirebaseUser(sharedPref.getString(LOGIN_SHARED,null),
                        FirebaseAuth.getInstance().getCurrentUser()), userId, getApplicationContext());

                if(friendFragment!=null)
                    friendFragment.configureViews();
                else
                    configureAndShowFriendFragment();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_check_login), Toast.LENGTH_SHORT).show();
            }
        });
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

        // Create question to show
        String question = getApplicationContext().getResources().getString(R.string.question_friend_request_1) + " " + name + " " + getApplicationContext().getResources().getString(R.string.question_friend_request_2);
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
            friendFragment.configureViews();
            dialog.dismiss();
            answerListener.onAnswer();
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
        });

        // Button NO
        Button dialogButtonNo = dialog.findViewById(R.id.button_no);
        dialogButtonNo.setOnClickListener(v -> {
            Action.rejectFriend(friend,userId,getApplicationContext());
            friendFragment.configureViews();
            dialog.dismiss();
            answerListener.onAnswer();
            defineCountersAndConfigureToolbar(MENU_MY_FRIENDS);
        });

        dialog.show();
    }

    private void checkForFriendRequests(){

        // Recover friends list
        List<Friend> listFriend = FriendsHandler.getListFriends(getApplicationContext(), userId);

        // Check if there are friends who are in status "ongoing"
        if(listFriend!=null){
            if(listFriend.size()>0){
                for(Friend friend : listFriend){
                    if(friend.getAccepted().equals(ONGOING)){
                        showAlertDialogFriendRequest(friend, () -> {});
                    }
                }
            }
        }
    }
}
