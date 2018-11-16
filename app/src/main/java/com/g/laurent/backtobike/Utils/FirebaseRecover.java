package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackCounters;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnBikeEventDataGetListener;
import com.g.laurent.backtobike.Models.OnChildChecking;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.Models.OnLoginChecked;
import com.g.laurent.backtobike.Models.OnRouteDataGetListener;
import com.g.laurent.backtobike.Models.OnUserDataGetListener;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import static com.g.laurent.backtobike.Utils.UtilsFirebase.buildBikeEvent;
import static com.g.laurent.backtobike.Utils.UtilsFirebase.buildFriend;
import static com.g.laurent.backtobike.Utils.UtilsFirebase.buildListRoute;


public class FirebaseRecover {

    private DatabaseReference databaseReferenceUsers;
    private static final String USERS = "users";
    private static final String TOKEN_DEVICE = "token_device";
    private static final String MY_FRIENDS = "my_friends";
    private static final String MY_EVENTS = "my_events";
    private static final String MY_INVITATIONS = "my_invitations";
    private static final String MY_ROUTES = "my_routes";

    public FirebaseRecover(Context context) {
        FirebaseApp.initializeApp(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUsers= databaseReference.child(USERS);
    }

    public FirebaseRecover(DatabaseReference databaseReferenceUsers) {
        this.databaseReferenceUsers= databaseReferenceUsers;
    }

    // ----------------------------------------------------------------------------------------------
    // -------------------------------- RECOVER USER DATAS ------------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverUserTokenDevice(Context context, String userId, final OnUserDataGetListener onUserDataGetListener){

        databaseReferenceUsers.child(userId).child(TOKEN_DEVICE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = null;
                if(dataSnapshot.getValue()!=null)
                    token = dataSnapshot.getValue().toString();

                if(token!=null)
                    onUserDataGetListener.onSuccess(true, token);
                else
                    onUserDataGetListener.onFailure(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure(context.getResources().getString(R.string.error_access_data) + "\n" + databaseError);
            }
        });
    }

    public void recoverUserDatas(Context context, String userId, final OnUserDataGetListener onUserDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean userIdOK = UtilsFirebase.doesUserIdExists(userId, dataSnapshot);
                String login = UtilsFirebase.getLogin(userId, dataSnapshot);
                Boolean loginOK = UtilsFirebase.isLoginOK(userId, dataSnapshot);

                if(userIdOK && loginOK)
                    onUserDataGetListener.onSuccess(true, login);
                else {
                    onUserDataGetListener.onSuccess(false, login);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure(context.getResources().getString(R.string.error_access_data) + "\n" + databaseError);
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------- RECOVER ALL ROUTES FROM USER ---------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverRoutesUser(String user_id, OnRouteDataGetListener onRouteDataGetListener)  {

        DatabaseReference databaseReferenceRoutes = databaseReferenceUsers.child(user_id).child(MY_ROUTES);
        databaseReferenceRoutes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Route> listRoutes = new ArrayList<>();

                if(dataSnapshot.getChildren()!=null){
                    for (DataSnapshot datas : dataSnapshot.getChildren())
                        listRoutes.add(buildListRoute(datas));
                }
                onRouteDataGetListener.onSuccess(listRoutes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onRouteDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------- RECOVER ALL BIKE EVENTS FROM USER ------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverBikeEventsUser(String user_id, OnBikeEventDataGetListener onBikeEventDataGetListener)  {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_EVENTS);

        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<BikeEvent> listBikeEvents = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    listBikeEvents.add(buildBikeEvent(datas));
                }

                onBikeEventDataGetListener.onSuccess(listBikeEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void recoverSingleBikeEventsUser(String user_id, String idEvent, OnBikeEventDataGetListener onBikeEventDataGetListener)  {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_EVENTS).child(idEvent);
        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BikeEvent bikeEvent = buildBikeEvent(dataSnapshot);
                onBikeEventDataGetListener.onSuccess(bikeEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void checkIfBikeEventExists(String typeChild, String user_id, String child, OnChildChecking onChildChecking){

        DatabaseReference databaseReference = null;

        switch(typeChild){
            case MY_EVENTS:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_EVENTS);
                break;
            case MY_INVITATIONS:
                databaseReference = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS);
                break;
        }

        if(databaseReference!=null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(child))
                        onChildChecking.hasChild(true);
                    else
                        onChildChecking.hasChild(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onChildChecking.hasChild(false);
                }
            });

        } else
            onChildChecking.hasChild(false);
    }

    // ----------------------------------------------------------------------------------------------
    // -------------------------------- RECOVER FRIENDS ---------------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void isLoginNotAmongUserFriends(Context context, String login, String userId, final OnFriendDataGetListener onFriendDataGetListener) {

        DatabaseReference databaseReferenceFriends = databaseReferenceUsers.child(userId).child(MY_FRIENDS);
        databaseReferenceFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!UtilsFirebase.isLoginAmongDatas(login, userId, dataSnapshot)){
                    isLoginOnFirebase(context, login,userId, onFriendDataGetListener); // check if login exists on firebase
                } else {
                    onFriendDataGetListener.onFailure(context.getResources().getString(R.string.friend_already_in_list));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure(context.getResources().getString(R.string.error_access_data) + "\n" + databaseError);
            }
        });
    }

    public void isLoginOnFirebase(Context context, String login, String userId, final OnFriendDataGetListener onFriendDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Friend friend = UtilsFirebase.getFriendWithLoginOnFirebase(login, userId, dataSnapshot);

                // next steps
                if(friend != null){
                    onFriendDataGetListener.onSuccess(friend);
                } else {
                    onFriendDataGetListener.onFailure(context.getResources().getString(R.string.login_not_known));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure(context.getResources().getString(R.string.error_access_data) + "\n" + databaseError);
            }
        });
    }

    public void isLoginOnFirebase(Context context, String login, String userId, final OnUserDataGetListener onUserDataGetListener) {

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean answer = UtilsFirebase.isLoginAmongDatas(login, userId, dataSnapshot);
                onUserDataGetListener.onSuccess(answer, login);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onUserDataGetListener.onFailure(context.getResources().getString(R.string.error_access_data)  + "\n" + databaseError);
            }
        });
    }

    public void recoverFriendsUser(String user_id, OnFriendDataGetListener onFriendDataGetListener) {

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(user_id).child(MY_FRIENDS);
        databaseReferenceFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Friend> listFriend = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if(datas.getKey()!=null){
                        listFriend.add(buildFriend(datas));
                    }
                }

                onFriendDataGetListener.onSuccess(listFriend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFriendDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void checkLogin(Context context, String user_id, String login, OnLoginChecked onLoginChecked) {

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        // Check if login is different than user's login, login is not among friends of the user and if the login exists on Firebase
        firebaseRecover.isLoginNotAmongUserFriends(context, login, user_id, new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {
                isLoginOnFirebase(context, login, user_id, new OnFriendDataGetListener() {
                    @Override
                    public void onSuccess(Friend friend) {
                        onLoginChecked.onSuccess(friend);
                    }

                    @Override
                    public void onSuccess(List<Friend> listFriend) {}

                    @Override
                    public void onFailure(String error) {
                        onLoginChecked.onFailed();
                        Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onSuccess(List<Friend> listFriend) {}

            @Override
            public void onFailure(String error) {
                onLoginChecked.onFailed();
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------ RECOVER ALL INVITATIONS FROM USER -----------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recoverInvitationsUser(String user_id, OnBikeEventDataGetListener onBikeEventDataGetListener) {

        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS);

        databaseReferenceInvitation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BikeEvent> listInvitations = new ArrayList<>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    listInvitations.add(buildBikeEvent(datas));
                }

                onBikeEventDataGetListener.onSuccess(listInvitations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }

    public void recoverDatasForCounters(String userId, Context context, CallbackCounters callbackCounters) {

        DatabaseReference databaseReferenceFriend = databaseReferenceUsers.child(userId);

        databaseReferenceFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counterFriends = UtilsFirebase.getCounterFriends(context, userId, dataSnapshot);
                int counterEvents= UtilsFirebase.getCounterEvents(context, userId, dataSnapshot);
                int counterInvits= UtilsFirebase.getCounterInvits(context, userId, dataSnapshot);
                List<String> fullListDifferences = UtilsFirebase.getFullListDifferences(context, userId, dataSnapshot);
                List<Difference> listDiffEvents = UtilsFirebase.getListDifferencesEvents(context, userId, dataSnapshot);

                callbackCounters.onCompleted(listDiffEvents, fullListDifferences, counterFriends, counterEvents, counterInvits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbackCounters.onFailure(databaseError.toString());
            }
        });
    }

    public void recoverSingleInvitationUser(String user_id, String idInvit, OnBikeEventDataGetListener onBikeEventDataGetListener) {

        DatabaseReference databaseReferenceEvents = databaseReferenceUsers.child(user_id).child(MY_INVITATIONS).child(idInvit);

        databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                BikeEvent bikeEvent = buildBikeEvent(dataSnapshot);
                onBikeEventDataGetListener.onSuccess(bikeEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBikeEventDataGetListener.onFailure(databaseError.toString());
            }
        });
    }
}