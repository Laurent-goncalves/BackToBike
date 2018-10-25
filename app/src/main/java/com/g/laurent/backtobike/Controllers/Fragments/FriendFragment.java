package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackEventActivity;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnFriendDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.FriendsAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private static final String LOGIN_SHARED ="login_shared";
    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    @BindView(R.id.gridview_check_box) GridView gridView;
    @BindView(R.id.my_id_view) TextView myIdView;
    private Context context;
    private List<Friend> listFriends;
    private List<Friend> listFriendsAccepted;
    private ArrayList<String> listFriendsSelected;
    private CallbackFriendActivity callbackFriendActivity;
    private CallbackEventActivity mCallbackEventActivity;
    private FirebaseUser firebaseUser;
    private String myLogin;
    private Boolean SelectMode;
    private FriendsAdapter adapter;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, view);
        defineMode(view);

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpreferences), Context.MODE_PRIVATE);

        myLogin = sharedPref.getString(LOGIN_SHARED,null);

        FirebaseApp.initializeApp(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        configureListFriendsAndView();

        return view;
    }

    public void configureListFriendsAndView(){

        listFriendsAccepted = new ArrayList<>();
        listFriends = FriendsHandler.getListFriends(context, firebaseUser.getUid());

        if(listFriends!=null){
            if(listFriends.size()>0){
                for(Friend friend : listFriends){
                    if(friend.getAccepted()!=null){
                        if (friend.getAccepted())
                            listFriendsAccepted.add(friend);
                    }
                }
            }
        }
        configureViews();
    }

    private void defineMode(View view){
        if(getArguments()!=null)
            SelectMode = getArguments().getBoolean(BUNDLE_SELECT_MODE);

        view.setOnClickListener(v -> {

            if(adapter!=null && callbackFriendActivity!=null) {
                SelectMode = false;
                adapter.setSelectMode(false);
                adapter.notifyDataSetChanged();
                callbackFriendActivity.configureButtonToolbar(false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if(context instanceof CallbackFriendActivity){
            callbackFriendActivity = (CallbackFriendActivity) context;
            listFriendsSelected = new ArrayList<>();
        }

        if(context instanceof CallbackEventActivity){
            mCallbackEventActivity = (CallbackEventActivity) context;
            listFriendsSelected = mCallbackEventActivity.getInvitation().getListIdFriends();
            if(listFriendsSelected==null)
                listFriendsSelected = new ArrayList<>();
        }
    }

    public void proceedToFriendsDeletion(){

        // change selectMode status to false
        setSelectMode(false);
        adapter.setSelectMode(false);

        // Delete friends in database and Firebase
        if(listFriendsSelected.size()>0){
            for(String idFriend : listFriendsSelected){

                int index = UtilsApp.findFriendIndexInListFriends(idFriend,listFriends);
                if(index!=-1){
                    // Delete friends in database and Firebase
                    Action.deleteFriend(listFriends.get(index), FirebaseAuth.getInstance().getUid(),context);

                    // Delete friends in listFriends
                    listFriends.remove(index);
                }

                index = UtilsApp.findFriendIndexInListFriends(idFriend,listFriendsAccepted);
                if(index!=-1){
                    // Delete friends in listFriendsAccepted
                    listFriendsAccepted.remove(index);
                }
            }
        }

        // Update adapter
        if(adapter!=null) {
            adapter.setListFriends(listFriendsAccepted);
            adapter.notifyDataSetChanged();
        }

        // Remove all elements in listFriendsSelected
        listFriendsSelected.clear();
    }

    public void configureViews(){

        // configure gridView
        adapter = new FriendsAdapter(context, listFriendsAccepted, SelectMode, this);
        gridView.setAdapter(adapter);

        // configure login view
        String myLoginText = context.getResources().getString(R.string.my_login_is) + " " + myLogin;
        myIdView.setText(myLoginText);
    }

    public void showDialogFriendAdd() {
        callbackFriendActivity.showAlertDialogAddNewFriend(this);
    }

    public void checkLogin(String login) {

        FirebaseRecover firebaseRecover = new FirebaseRecover(context);

        // Check if login is different than user's login, login is not among friends of the user and if the login exists on Firebase
        firebaseRecover.isLoginNotAmongUserFriends(login, firebaseUser.getUid(), new OnFriendDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {
                saveNewFriend(friend);
            }

            @Override
            public void onSuccess(List<Friend> listFriend) {}

            @Override
            public void onFailure(String error) {
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveNewFriend(Friend friend){

        // Add friend to database and Firebase
        Action.addNewFriend(friend, UtilsApp.getUserFromFirebaseUser(myLogin, firebaseUser),firebaseUser.getUid(), context);

        // Update layout
        listFriends.add(friend);
        listFriendsAccepted.add(listFriends.size()-1, friend); // add new friend before the last item

        adapter.setListFriends(listFriendsAccepted);

        // Update adapter
        adapter.notifyDataSetChanged();
    }

    public void setSelectMode(Boolean selectMode) {
        SelectMode = selectMode;
    }

    public ArrayList<String> getListFriendsSelected() {
        return listFriendsSelected;
    }

    public CallbackEventActivity getCallbackEventActivity() {
        return mCallbackEventActivity;
    }

    public CallbackFriendActivity getCallbackFriendActivity() {
        return callbackFriendActivity;
    }

    public List<Friend> getListFriends() {
        return listFriends;
    }

    public List<Friend> getListFriendsAccepted() {
        return listFriendsAccepted;
    }

    public FriendsAdapter getAdapter() {
        return adapter;
    }
}
