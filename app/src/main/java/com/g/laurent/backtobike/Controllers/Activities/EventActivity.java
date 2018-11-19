package com.g.laurent.backtobike.Controllers.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        setContentView(R.layout.activity_event);
        invitation = new Invitation();
        invitation.setIdRoute(-1);
        userId = FirebaseAuth.getInstance().getUid();
        assignToolbarViews();
        savePreviousPage(MENU_CREATE_EVENT);
        SaveAndRestoreDataInvitActivity.restoreData(savedInstanceState,this);

        if(userId!=null)
            defineCountersAndConfigureToolbar(MENU_CREATE_EVENT);

        configureAndShowInvitFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveAndRestoreDataInvitActivity.saveData(outState, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userId!=null)
            defineCountersAndConfigureToolbar(MENU_CREATE_EVENT);
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------------------- CONFIGURE VIEWS ------------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void configureAndShowInvitFragment(){

        // Remove button OK
        configureButtonToolbar(false);

        // Initialize variables
        invitFragment = new InvitFragment();

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, invitFragment, TAG_INVIT_FRAGMENT);
        fragmentTransaction.commit();
    }

    public void backToInvitFragment(){

        // Remove button OK
        configureButtonToolbar(false);

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, invitFragment, TAG_INVIT_FRAGMENT);
        fragmentTransaction.commit();
    }

    public void configureAndShowFriendFragment(){

        // Configure button OK
        configureButtonToolbar(true);

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

    public void configureButtonToolbar(Boolean buttonVisible){

        Button buttonOK = toolbar.findViewById(R.id.button_toolbar);

        if(!buttonVisible){ // if no button needed, remove it
            buttonOK.setVisibility(View.GONE);
        } else { // if button needed, make it visible and configure click

            buttonOK.setText(getApplicationContext().getResources().getString(R.string.ok));
            buttonOK.setVisibility(View.VISIBLE);

            buttonOK.setOnClickListener(v -> {
                // Display invitfragment and configure guests selected
                getInvitation().setListIdFriends(getFriendFragment().getListFriendsSelected());
                backToInvitFragment();
            });
        }
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- GETTERS AND SETTERS ----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public Invitation getInvitation() {
        return invitation;
    }

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
