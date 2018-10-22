package com.g.laurent.backtobike.Models;


public interface CallbackEventActivity {

    void configureAndShowInvitFragment();

    void configureAndShowFriendFragment();

    Invitation getInvitation();

    String getUserId();

    void launchDisplayActivity(String typeDisplay, String id);
}
