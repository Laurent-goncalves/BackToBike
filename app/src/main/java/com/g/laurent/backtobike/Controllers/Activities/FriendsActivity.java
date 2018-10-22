package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.EditText;
import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FriendsActivity extends BaseActivity implements CallbackFriendActivity {

    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    private FriendFragment friendFragment;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            userId = user.getUid();
            synchronizeDataWithFirebaseAndConfigureToolbar(MENU_MY_FRIENDS,this);

            SynchronizeWithFirebase.synchronizeFriends(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
                @Override
                public void onCompleted() {
                    configureAndShowFriendFragment();
                }

                @Override
                public void onFailure(String error) {
                    configureAndShowFriendFragment();
                }
            });
        }

        SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        mySwipeRefreshLayout.setOnRefreshListener(() -> SynchronizeWithFirebase.synchronizeFriends(userId, getApplicationContext(), new CallbackSynchronizeEnd() {
            @Override
            public void onCompleted() {
                configureAndShowFriendFragment();
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                configureAndShowFriendFragment();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        }));
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


}
