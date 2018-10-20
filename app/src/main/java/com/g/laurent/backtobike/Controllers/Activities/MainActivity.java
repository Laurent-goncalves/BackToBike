package com.g.laurent.backtobike.Controllers.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

       // clearDatabase();

        userId = user.getUid();
        toolbarManager.configureToolbar(this, MENU_MAIN_PAGE);
        checkIfUserHasLoginOnFirebase();
    }

    private void checkIfUserHasLoginOnFirebase(){

        FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
        firebaseRecover.recoverUserDatas(userId, new OnUserDataGetListener() {
            @Override
            public void onSuccess(Boolean datasOK, String login) {
                if(datasOK) { // if userId is on firebase and login has been provided

                    // Save login in sharedpreferences
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                            getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
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

    private void clearDatabase(){

        AppDatabase.getInstance(getApplicationContext(),user.getUid()).friendsDao().deleteAllFriends();
        AppDatabase.getInstance(getApplicationContext(),user.getUid()).routeSegmentDao().deleteRouteSegment();
        AppDatabase.getInstance(getApplicationContext(),user.getUid()).routesDao().deleteAllRoutes();
        AppDatabase.getInstance(getApplicationContext(),user.getUid()).eventFriendsDao().deleteAllEventFriends();
        AppDatabase.getInstance(getApplicationContext(),user.getUid()).bikeEventDao().deleteAllBikeEvents();

    }
}
