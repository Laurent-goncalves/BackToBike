package com.g.laurent.backtobike.Models;


public interface CallbackInvitActivity {

    void configureAndShowInvitFragment();

    void configureAndShowFriendFragment();

    Invitation getInvitation();
}
