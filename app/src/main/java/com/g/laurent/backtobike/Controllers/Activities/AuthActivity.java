package com.g.laurent.backtobike.Controllers.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 121;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        AuthUI.getInstance().signOut(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goToMainActivity();
                }
            }
        };
    }

    @OnClick(R.id.main_activity_button_login_google)
    public void launchSignInWithGoogle(){
        signInWithGoogle();
    }

    @OnClick(R.id.facebook_loginButton)
    public void launchSignInWithFacebook(){
        signInWithFacebook();
    }

    // -------------------------------------------------------------------------------------------------------
    // ---------------------------------- CONNECT WITH FACEBOOK ----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void signInWithFacebook() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if(accessToken!=null && !accessToken.isExpired())
                            handleFacebookAccessToken(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.cancel_connection),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_connection),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Show message to user "connection successful"
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.success_connection), Toast.LENGTH_SHORT).show();

                        // Save current user data in Firebase
                        saveCurrentUserToFirebase();

                        // Go to MainActivity and destroy authActivity
                        goToMainActivity();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_connection),Toast.LENGTH_LONG).show();
                    }
                });
    }

    // -------------------------------------------------------------------------------------------------------
    // ---------------------------------- CONNECT WITH GOOGLE ------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void signInWithGoogle() {

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account!=null && !account.isExpired())
                firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_connection),Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Show message to user "connection successful"
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.success_connection), Toast.LENGTH_SHORT).show();

                        // Save current user data in Firebase
                        saveCurrentUserToFirebase();

                        // Go to MainActivity and destroy authActivity
                        goToMainActivity();
                        finish();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_connection),Toast.LENGTH_LONG).show();
                    }
                });
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- COMMON METHODS -------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void saveCurrentUserToFirebase(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){
            FirebaseUpdate firebaseUpdate = new FirebaseUpdate(getApplicationContext());

            String photoUrl = null;
            if(user.getPhotoUrl()!=null)
                photoUrl = user.getPhotoUrl().toString();

            firebaseUpdate.updateUserData(user.getUid(), user.getDisplayName(), photoUrl);
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
