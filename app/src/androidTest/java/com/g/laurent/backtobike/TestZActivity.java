package com.g.laurent.backtobike;


import android.Manifest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.g.laurent.backtobike.Controllers.Activities.BaseActivity;
import com.g.laurent.backtobike.Controllers.Activities.MainActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;



@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestZActivity extends AndroidTestCase {

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;

    @Override
    public void setUp() throws InterruptedException {
        authSignal = new CountDownLatch(30);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("develop.lgon2@gmail.com", "ABC123").addOnCompleteListener(
                    task -> {
                        final AuthResult result = task.getResult();
                        final FirebaseUser user = result.getUser();
                        authSignal.countDown();
                    });
        } else {
            authSignal.countDown();
        }
        authSignal.await(60, TimeUnit.SECONDS);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(auth != null) {
            auth.signOut();
            auth = null;
        }
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testActivities(){

        waiting_time(5000);
        mActivityTestRule.getActivity().launchFriendsActivity();
        waiting_time(5000);
        mActivityTestRule.getActivity().launchEventActivity();
        waiting_time(5000);
        mActivityTestRule.getActivity().launchDisplayActivity(BaseActivity.DISPLAY_MY_ROUTES, null);
        waiting_time(5000);
        mActivityTestRule.getActivity().launchDisplayActivity(BaseActivity.DISPLAY_MY_EVENTS, null);
        waiting_time(5000);
        mActivityTestRule.getActivity().launchDisplayActivity(BaseActivity.DISPLAY_MY_INVITS, null);
        waiting_time(5000);
        mActivityTestRule.getActivity().launchTraceActivity(null);
        waiting_time(5000);
        
        Assert.assertEquals(1, 1);
    }

    private void waiting_time(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
