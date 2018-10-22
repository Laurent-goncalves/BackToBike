package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackSynchronizeEnd;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.SynchronizeWithFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String SHAREDPREFERENCES_INIT = "database_init_sharedpreferences";
    private SharedPreferences sharedPref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
            userId = user.getUid();

        // recover SharedPreferences
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        // Check if the database has already been initialized (during the first use of the app on the phone)
        checkInitializationDatabase();
    }

    private void checkInitializationDatabase(){

        if(!sharedPref.getBoolean(SHAREDPREFERENCES_INIT,false)){

            try {
                SynchronizeWithFirebase.synchronizeAllDatasFromUser(userId, sharedPref, getApplicationContext(), new CallbackSynchronizeEnd() {
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
        synchronizeDataWithFirebaseAndConfigureToolbar(MENU_MAIN_PAGE,this);
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



    }

    private void clearDatabase(String userId, Context context){
        AppDatabase.getInstance(context,userId).eventFriendsDao().deleteAllEventFriends();
        AppDatabase.getInstance(context,userId).friendsDao().deleteAllFriends();
        AppDatabase.getInstance(context,userId).routeSegmentDao().deleteRouteSegment();
        AppDatabase.getInstance(context,userId).routesDao().deleteAllRoutes();
        AppDatabase.getInstance(context,userId).bikeEventDao().deleteAllBikeEvents();
    }

}
