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
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.MapTools.GetCurrentLocation;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsCounters;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements CallbackMainActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final String SHAREDPREFERENCES_INIT = "database_init_sharedpreferences";
    private static final String BUNDLE_COUNTER_EVENTS = "bundle_counter_events";
    private static final String BUNDLE_COUNTER_INVITS = "bundle_counter_invits";
    private static final String BUNDLE_COUNTER_FRIENDS = "bundle_counter_friends";
    private static final String BUNDLE_LATITUDE = "bundle_latitude";
    private static final String BUNDLE_LONGITUDE = "bundle_longitude";
    private static final String BUNDLE_DIFFERENCES = "bundle_differences";
    private SharedPreferences sharedPref;
    private MainFragment mainFragment;
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

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    System.out.println("eee  token=" + token);
                });


        //RemoteMessage remoteMessage = new RemoteMessage();
        //FirebaseMessaging.getInstance().send(remoteMessage);

        clearDatabase(userId,getApplicationContext());

        //FirebaseUpdate firebaseUpdate = new FirebaseUpdate(getApplicationContext());
        //firebaseUpdate.setTestData(getApplicationContext(), userId);



        // recover SharedPreferences
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Check if the database has already been initialized (during the first use of the app on the phone)
        checkInitializationDatabase();

    }

    public  String send(String to,  String body) {
        try {

            final String apiKey = FirebaseAuth.getInstance().getUid();

            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", to);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            // notification.put("title", title);
            notification.put("body", body);
            message.put("data", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                checkIfUserHasLoginOnFirebase();
            }
        } else {
            checkIfUserHasLoginOnFirebase();
        }
    }

    private void checkIfUserHasLoginOnFirebase(){

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.recoverUserDatas(userId, new OnUserDataGetListener() {
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


    public void getCurrentLocationForWeather(){
        GetCurrentLocation getCurrentLocation = new GetCurrentLocation();
        getCurrentLocation.getLocationPermission(this, sharedPref, currentLocation -> defineCountersAndConfigureToolbar(currentLocation, MENU_MAIN_PAGE));
    }

    public void defineCountersAndConfigureToolbar(LatLng currentLocation, String typeDisplay){

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
    }

    private void showDialogToDefineLogin(){

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_get_login);

        EditText login = dialog.findViewById(R.id.edittext_login);

        // Button SAVE
        Button dialogButtonSave = dialog.findViewById(R.id.button_save);
        dialogButtonSave.setOnClickListener(v -> {
            if(login.getText().length()>0)
                checkLogin(login.getText().toString(), dialog);
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
        firebaseRecover.isLoginOnFirebase(login, userId, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean loginOnFirebase, String login) {
                if(loginOnFirebase){
                    Toast.makeText(getApplicationContext(),getApplicationContext()
                            .getResources().getString(R.string.login_already_on_firebase),Toast.LENGTH_LONG).show();
                } else {
                    FirebaseUpdate firebaseUpdate = new FirebaseUpdate(getApplicationContext());
                    firebaseUpdate.updateUserData(userId, user.getDisplayName(), user.getPhotoUrl().toString(),login);
                    dialog.dismiss();
                    defineCountersAndConfigureToolbar(MENU_MAIN_PAGE);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configureMainFragment(String differences, int countEvent, int countFriends, int countInvits, LatLng currentLocation){

        mainFragment = new MainFragment();

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



    public MainActivity getMainActivity(){
        return this;
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
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


    private void clearDatabase(String userId, Context context){
        AppDatabase.getInstance(context,userId).eventFriendsDao().deleteAllEventFriends();
        //AppDatabase.getInstance(context,userId).friendsDao().deleteAllFriends();
        //AppDatabase.getInstance(context,userId).routeSegmentDao().deleteRouteSegment();
        //AppDatabase.getInstance(context,userId).routesDao().deleteAllRoutes();
        AppDatabase.getInstance(context,userId).bikeEventDao().deleteAllBikeEvents();

        /*setFriendsDatabase(getApplicationContext(),userId);

        Route route = new Route(0,"Trip to Paris",true);
        route.setListRouteSegment(getListRouteSegments());

        // ----------------------------------------------------- INSERT ROUTE
        int idRoute = Action.addNewRoute(route, userId,context);

        BikeEvent bikeEvent = new BikeEvent("id1_01_01_2000_14:00", "id1", "01/01/2000", "14:00", idRoute, "Comments : take good shoes", "ongoing");
        bikeEvent.setRoute(route);
        bikeEvent.setListEventFriends(getListEventFriends());

        // ----------------------------------------------------- INSERT BIKE EVENT
        Action.addBikeEvent(bikeEvent,"id1", getApplicationContext());*/
    }

    private List<RouteSegment> getListRouteSegments(){

        List<RouteSegment> listRouteSegments = new ArrayList<>();
        RouteSegment ROUTE_SEG1_DEMO = new RouteSegment(0,1,48.819446, 2.344624,999);
        RouteSegment ROUTE_SEG2_DEMO = new RouteSegment(0,2,48.885412, 2.336589,999);
        RouteSegment ROUTE_SEG3_DEMO = new RouteSegment(0,3,48.874563, 2.312778,999);
        RouteSegment ROUTE_SEG4_DEMO = new RouteSegment(0,4,48.858933, 2.321511,999);

        listRouteSegments.add(ROUTE_SEG1_DEMO);
        listRouteSegments.add(ROUTE_SEG2_DEMO);
        listRouteSegments.add(ROUTE_SEG3_DEMO);
        listRouteSegments.add(ROUTE_SEG4_DEMO);

        return listRouteSegments;
    }

    private List<EventFriends> getListEventFriends(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"id1_01_01_2000_14:00","id2","id2","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(0,"id1_01_01_2000_14:00","id3","id3","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);

        return listEventFriends;
    }

    private void setFriendsDatabase(Context context, String userId){

        Friend friend1 = new Friend("id1","id1","Mat","photoUrl",true, true);
        Friend friend2 = new Friend("id2","id2","Seb","photoUrl",true,true);
        Friend friend3 = new Friend("id3","id3","Camille","photoUrl",true,true);

        FriendsHandler.insertNewFriend(context,friend1, userId);
        FriendsHandler.insertNewFriend(context,friend2, userId);
        FriendsHandler.insertNewFriend(context,friend3, userId);
    }
}
