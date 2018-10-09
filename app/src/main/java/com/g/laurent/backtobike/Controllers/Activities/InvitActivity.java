package com.g.laurent.backtobike.Controllers.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.R;

public class InvitActivity extends AppCompatActivity {

    private final static String TAG_INVIT_FRAGMENT = "tag_invit_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invit);
        configureAndShowInvitFragment();
    }

    public void configureAndShowInvitFragment(){

        // Initialize variables
        InvitFragment invitFragment = new InvitFragment();

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_invit, invitFragment, TAG_INVIT_FRAGMENT);
        fragmentTransaction.commit();
    }
}
