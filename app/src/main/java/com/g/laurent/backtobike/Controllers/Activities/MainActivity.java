package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g.laurent.backtobike.Controllers.Fragments.MainFragment;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String SHAREDPREFERENCES_INIT = "database_init_sharedpreferences";
    private SharedPreferences sharedPref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignToolbarViews();

        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    System.out.println("eee  token=" + token);
                });


        if(user!=null) {
            userId = user.getUid();
        }
        //RemoteMessage remoteMessage = new RemoteMessage();
        //FirebaseMessaging.getInstance().send(remoteMessage);
        //clearDatabase(userId,getApplicationContext());

        // recover SharedPreferences
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Check if the database has already been initialized (during the first use of the app on the phone)
        checkInitializationDatabase();

        configureMainActivity();
    }

    private void checkInitializationDatabase(){

        if(!sharedPref.getBoolean(SHAREDPREFERENCES_INIT,false)){

            try {
                SynchronizeWithFirebase.buildDatabaseWithDatasFromFirebase(userId, sharedPref, getApplicationContext(), new CallbackSynchronizeEnd() {
                    @Override
                    public void onCompleted() {
                        startConfigurationMainActivity();
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            startConfigurationMainActivity();
        }
    }

    public void startConfigurationMainActivity(){
        defineCountersAndConfigureToolbar(MENU_MAIN_PAGE);
        checkIfUserHasLoginOnFirebase();
    }

    private void checkIfUserHasLoginOnFirebase(){

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.recoverUserDatas(userId, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean datasOK, String login) {
                if(datasOK) { // if userId is on firebase and login has been provided

                    // Save login in sharedpreferences

                    sharedPref.edit().putString(LOGIN_SHARED,login).apply();

                    // Configure MainActivity
                    configureMainActivity();

                } else { // login needs to be provided by user
                    showDialogToDefineLogin(); // show dialog
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
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
                    configureMainActivity();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configureMainActivity(){

        MainFragment mainFragment = new MainFragment();

        // configure and show the invitFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main, mainFragment);
        fragmentTransaction.commit();
    }

    private void clearDatabase(String userId, Context context){
        AppDatabase.getInstance(context,userId).eventFriendsDao().deleteAllEventFriends();
        AppDatabase.getInstance(context,userId).friendsDao().deleteAllFriends();
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
