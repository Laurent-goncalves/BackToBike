package com.g.laurent.backtobike.Models;

import android.content.Context;

import com.g.laurent.backtobike.Controllers.Activities.MainActivity;

public interface CallbackMainActivity {

    void launchFriendsActivity();

    void launchDisplayActivity(String typeDisplay, String id);

    void signOutUserFromFirebase(Context context);

    MainActivity getMainActivity();
}
