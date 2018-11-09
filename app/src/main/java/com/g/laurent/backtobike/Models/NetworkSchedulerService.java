package com.g.laurent.backtobike.Models;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.SynchronizeWithDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String NEED_SYNCHRONIZATION = "need_synchronization";
    private static final String LOGIN_SHARED ="login_shared";
    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(mConnectivityReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getResources().getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        if(isConnected && sharedPref.getBoolean(NEED_SYNCHRONIZATION, false)){

            // Build friend from user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String login = sharedPref.getString(LOGIN_SHARED, null);
            Friend user = new Friend(firebaseUser.getUid(), login, firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(),true, true);

            // Launch synchronization (after completion, change status of NEED_SYNCHRONIZATION to false)
            SynchronizeWithDatabase.synchronizeFriends(getApplicationContext(), user, () -> sharedPref.edit().putBoolean(NEED_SYNCHRONIZATION, false).apply());
        }
    }
}
