package com.g.laurent.backtobike.Controllers.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.g.laurent.backtobike.Controllers.Fragments.FriendFragment;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.CallbackInvitActivity;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.Models.ToolbarManager;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SaveAndRestoreDataInvitActivity;

public class InvitActivity extends AppCompatActivity implements CallbackInvitActivity {

    private final static String TAG_INVIT_FRAGMENT = "tag_invit_fragment";
    private final static String TAG_FRIEND_FRAGMENT = "tag_friend_fragment";
    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    private InvitFragment invitFragment;
    private ToolbarManager toolbarManager;
    private FriendFragment friendFragment;
    private Invitation invitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invit);
        invitation = new Invitation();
        SaveAndRestoreDataInvitActivity.restoreData(savedInstanceState,this);
        toolbarManager = new ToolbarManager();
        toolbarManager.configureToolbar(this);
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

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public FriendFragment getFriendFragment() {
        return friendFragment;
    }
}
