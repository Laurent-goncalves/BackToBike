package com.g.laurent.backtobike.Models;


import android.content.SharedPreferences;
import com.g.laurent.backtobike.Controllers.Activities.MainActivity;


public interface CallbackMainActivity {

    void launchFriendsActivity();

    void launchDisplayActivity(String typeDisplay, String id);

    MainActivity getMainActivity();

    SharedPreferences getSharedPref();
}
