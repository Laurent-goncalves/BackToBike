package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.g.laurent.backtobike.Controllers.Fragments.MainFragment;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.CallbackCounters;
import com.g.laurent.backtobike.Models.CallbackMainActivity;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.MapTools.GetCurrentLocation;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsCounters;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import static com.g.laurent.backtobike.Utils.UtilsApp.areCharactersAllowed;


public class MainActivity extends BaseActivity implements CallbackMainActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final String SHAREDPREFERENCES_INIT = "database_init_sharedpreferences";
    private static final String BUNDLE_COUNTER_EVENTS = "bundle_counter_events";
    private static final String BUNDLE_COUNTER_INVITS = "bundle_counter_invits";
    private static final String BUNDLE_COUNTER_FRIENDS = "bundle_counter_friends";
    private static final String BUNDLE_LATITUDE = "bundle_latitude";
    private static final String BUNDLE_LONGITUDE = "bundle_longitude";
    private static final String BUNDLE_DIFFERENCES = "bundle_differences";
    private FirebaseUser user;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignToolbarViews();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // open progressBar

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            userId = user.getUid();
        }

        savePreviousPage(MENU_MAIN_PAGE);

        //clearDatabase(userId, getApplicationContext());

        // Delete overdue events on Firebase and Database
        UtilsTime.deleteOverdueEvents(getApplicationContext(), userId);

        // Check if the database has already been initialized (during the first use of the app on the phone)
        if(userId!=null)
            checkInitializationDatabase();
        else
            getCurrentLocationForWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userId!=null)
            UtilsTime.deleteOverdueEvents(getApplicationContext(), userId);
        defineCountersAndConfigureToolbar(MENU_MAIN_PAGE);
    }

    @Override
    protected void refreshActivity(){
        defineCountersAndConfigureToolbar(MENU_MAIN_PAGE);
    }

    // --------------------------------------------------------------------------------------------------------
    // -------------------------------------- LOGIN CHECKING --------------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    private void checkInitializationDatabase(){

        if(!sharedPref.getBoolean(SHAREDPREFERENCES_INIT,false)){

            try {
                SynchronizeWithFirebase.buildDatabaseWithDatasFromFirebase(userId, sharedPref, getApplicationContext(), new CallbackSynchronizeEnd() {
                    @Override
                    public void onCompleted() {
                        checkIfUserHasLoginOnFirebase();
                    }

                    @Override
                    public void onFailure(String error) {
                        checkIfUserHasLoginOnFirebase();
                    }
                });
            } finally {
                checkIfUserHasLoginOnFirebase();
            }
        } else {
            checkIfUserHasLoginOnFirebase();
        }
    }

    private void checkIfUserHasLoginOnFirebase(){

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.recoverUserDatas(getApplicationContext(), userId, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean datasOK, String login) {
                if(datasOK) { // if userId is on firebase and login has been provided

                    // Save login in sharedpreferences
                    sharedPref.edit().putString(LOGIN_SHARED,login).apply();

                    // Get current location
                    getCurrentLocationForWeather();

                } else { // login needs to be provided by user
                    showDialogToDefineLogin(); // show dialog
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                defineCountersAndConfigureToolbar(MENU_MAIN_PAGE);
            }
        });
    }

    private void showDialogToDefineLogin(){

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_get_login);

        EditText login = dialog.findViewById(R.id.edittext_login);

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {
            if(login.getText().length()>0) {
                if(areCharactersAllowed(login.getText().toString()))
                    checkLogin(login.getText().toString(), dialog);
                else
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_forbidden_characters), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_login),Toast.LENGTH_LONG).show();
        });

        // Button CANCEL
        Button dialogButtonCancel = dialog.findViewById(R.id.button_cancel);
        dialogButtonCancel.setOnClickListener(v -> Toast.makeText(getApplicationContext(),getApplicationContext()
                .getResources().getString(R.string.error_login),Toast.LENGTH_LONG).show());

        dialog.show();
    }

    public void checkLogin(String login, Dialog dialog) {

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());

        // Check if login is different than user's login, login is not among friends of the user and if the login exists on Firebase
        firebaseRecover.isLoginOnFirebase(getApplicationContext(), login, userId, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean loginOnFirebase, String login) {
                if(loginOnFirebase){
                    Toast.makeText(getApplicationContext(),getApplicationContext()
                            .getResources().getString(R.string.login_already_on_firebase),Toast.LENGTH_LONG).show();
                } else {
                    FirebaseUpdate firebaseUpdate = new FirebaseUpdate(getApplicationContext());
                    firebaseUpdate.updateUserData(userId, user.getDisplayName(), user.getPhotoUrl().toString(),login);
                    dialog.dismiss();

                    // Save login in sharedpreferences
                    sharedPref.edit().putString(LOGIN_SHARED,login).apply();

                    // Get current location
                    getCurrentLocationForWeather();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            }
        });
    }

    // --------------------------------------------------------------------------------------------------------
    // -------------------------------------- WEATHER METHODS -------------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void getCurrentLocationForWeather(){
        GetCurrentLocation getCurrentLocation = new GetCurrentLocation();
        getCurrentLocation.getLocationPermission(this, sharedPref, currentLocation -> defineCountersAndConfigureToolbar(currentLocation, MENU_MAIN_PAGE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocationForWeather();
                } else {
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.give_permission),Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    // -------------------------------------- CONFIGURE VIEWS -------------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public void defineCountersAndConfigureToolbar(LatLng currentLocation, String typeDisplay){

        if(userId!=null && UtilsApp.isInternetAvailable(getApplicationContext())){
            FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
            firebaseRecover.recoverDatasForCounters(userId, getApplicationContext(), new CallbackCounters() {
                @Override
                public void onCompleted(List<Difference> differenceList, List<String> listDifferencesFriendsAndInvits, int counterFriend, int counterEvents, int counterInvits) {
                    toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, counterFriend, counterEvents, counterInvits);
                    int count = counterFriend + counterEvents + counterInvits;
                    UtilsApp.setBadge(getApplicationContext(), count);

                    configureMainFragment(UtilsCounters.transformListDifferencesToString(differenceList, listDifferencesFriendsAndInvits),
                            counterEvents, counterFriend, counterInvits, currentLocation);
                }

                @Override
                public void onFailure(String error) {
                    toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, 0, 0,0);
                    UtilsApp.setBadge(getApplicationContext(), 0);
                    configureMainFragment(null,0,0,0, currentLocation);
                }
            });
        } else {
            configureMainFragment(null,0,0,0, currentLocation);
        }

    }

    private void configureMainFragment(String differences, int countEvent, int countFriends, int countInvits, LatLng currentLocation){

        MainFragment mainFragment = new MainFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_COUNTER_EVENTS, countEvent);
        bundle.putInt(BUNDLE_COUNTER_INVITS, countInvits);
        bundle.putInt(BUNDLE_COUNTER_FRIENDS, countFriends);
        bundle.putDouble(BUNDLE_LATITUDE, currentLocation.latitude);
        bundle.putDouble(BUNDLE_LONGITUDE, currentLocation.longitude);
        bundle.putString(BUNDLE_DIFFERENCES, differences);

        mainFragment.setArguments(bundle);

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main, mainFragment);
        fragmentTransaction.commit();

        progressBar.setVisibility(View.GONE); // close progressBar
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------ GETTERS AND SETTERS -----------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    public MainActivity getMainActivity(){
        return this;
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }


    private void clearDatabase(String userId, Context context){

        AppDatabase.getInstance(context,userId).eventFriendsDao().deleteAllEventFriends();
        //AppDatabase.getInstance(context,userId).friendsDao().deleteAllFriends();
        AppDatabase.getInstance(context,userId).routeSegmentDao().deleteRouteSegment();
        AppDatabase.getInstance(context,userId).routesDao().deleteAllRoutes();
        AppDatabase.getInstance(context,userId).bikeEventDao().deleteAllBikeEvents();

    }
}
