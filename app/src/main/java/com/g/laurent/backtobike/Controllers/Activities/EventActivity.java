package com.g.laurent.backtobike.Controllers.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.CallbackEventActivity;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDataInvitActivity;
import com.google.firebase.auth.FirebaseAuth;

public class EventActivity extends BaseActivity implements CallbackEventActivity {

    private final static String TAG_INVIT_FRAGMENT = "tag_invit_fragment";
    private final static String TAG_FRIEND_FRAGMENT = "tag_friend_fragment";
    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    private InvitFragment invitFragment;
    private FriendFragment friendFragment;
    private Invitation invitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invit);
        invitation = new Invitation();
        userId = FirebaseAuth.getInstance().getUid();

        SaveAndRestoreDataInvitActivity.restoreData(savedInstanceState,this);
        toolbarManager.configureToolbar(this, MENU_CREATE_EVENT);

        configureAndShowInvitFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDataInvitActivity.saveData(outState, this);
    }

    public void configureAndShowInvitFragment(){

        toolbarManager.configureButtonToolbar(false,this);

        // Initialize variables
        invitFragment = new InvitFragment();

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, invitFragment, TAG_INVIT_FRAGMENT);
        fragmentTransaction.commit();
    }

    public void backToInvitFragment(){

        toolbarManager.configureButtonToolbar(false,this);

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, invitFragment, TAG_INVIT_FRAGMENT);
        fragmentTransaction.commit();
    }

    public void configureAndShowFriendFragment(){

        toolbarManager.configureButtonToolbar(true,this);

        // Initialize variables
        friendFragment = new FriendFragment();

        // add a bundle
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_SELECT_MODE, true); // to open fragment in select mode
        friendFragment.setArguments(bundle);

        // configure and show the friendFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, friendFragment, TAG_FRIEND_FRAGMENT);
        fragmentTransaction.commit();
    }

    public Invitation getInvitation() {
        return invitation;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public FriendFragment getFriendFragment() {
        return friendFragment;
    }
}
