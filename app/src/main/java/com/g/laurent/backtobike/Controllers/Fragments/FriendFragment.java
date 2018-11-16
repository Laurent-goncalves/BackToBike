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
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackEventActivity;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Views.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.LOGIN_SHARED;


public class FriendFragment extends Fragment {

    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    @BindView(R.id.gridview_check_box) GridView gridView;
    @BindView(R.id.my_id_view) TextView myIdView;
    private Context context;
    private List<Friend> listFriends;
    private ArrayList<String> listFriendsSelected;
    private CallbackFriendActivity callbackFriendActivity;
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

        // Define mode for displaying friends (deletion, display...)
        defineMode(view);

        // Get login of user
        if(context==null)
            context= getActivity().getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpreferences), Context.MODE_PRIVATE);
        myLogin = sharedPref.getString(LOGIN_SHARED,null);

        // Recover firebaseUser
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        // Configure views
        configureViews();

        return view;
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
            CallbackEventActivity callbackEventActivity = (CallbackEventActivity) context;
            listFriendsSelected = callbackEventActivity.getInvitation().getListIdFriends();
            if(listFriendsSelected==null)
                listFriendsSelected = new ArrayList<>();
        }
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------- ACTIONS ALERT DIALOGS ------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public void proceedToFriendsDeletion(){

        // change selectMode status to false
        setSelectMode(false);

        // Delete friends in database and Firebase
        if(listFriendsSelected.size()>0){
            for(String idFriend : listFriendsSelected){

                int index = UtilsApp.findFriendIndexInListFriends(idFriend,listFriends);
                if(index!=-1){
                    // Delete friends in database and Firebase
                    Action.deleteFriend(listFriends.get(index), firebaseUser.getUid(),context);

                    // Delete friends in listFriends
                    listFriends.remove(index);
                }
            }
        }

        // Update adapter
        if(adapter!=null) {
            adapter.setListFriends(listFriends);
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new FriendsAdapter(context, listFriends, SelectMode, this);
            gridView.setAdapter(adapter);
        }

        // Remove all elements in listFriendsSelected
        listFriendsSelected.clear();
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------------ CONFIGURE VIEWS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public void configureViews(){

        // Get list friends on Database
        listFriends = FriendsHandler.getListFriends(context, firebaseUser.getUid());

        // configure gridView
        if(adapter!=null){
            adapter.setListFriends(listFriends);
            adapter.setSelectMode(SelectMode);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new FriendsAdapter(context, listFriends, SelectMode, this);
            gridView.setAdapter(adapter);
        }

        // configure login view
        String myLoginText = context.getResources().getString(R.string.my_login_is) + " " + myLogin;
        myIdView.setText(myLoginText);
    }

    // ------------------------------------------------------------------------------------------------------------
    // -------------------------------------- GETTERS AND SETTERS -------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public void setSelectMode(Boolean selectMode) {
        SelectMode = selectMode;
    }

    public ArrayList<String> getListFriendsSelected() {
        return listFriendsSelected;
    }

    public CallbackFriendActivity getCallbackFriendActivity() {
        return callbackFriendActivity;
    }

    public FriendsAdapter getAdapter() {
        return adapter;
    }
}
