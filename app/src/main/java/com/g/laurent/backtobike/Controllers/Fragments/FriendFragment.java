package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.CallbackFriendActivity;
import com.g.laurent.backtobike.Models.CallbackInvitActivity;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.OnDataGetListener;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.FirebaseUpdate;
import com.g.laurent.backtobike.Utils.FriendsHandler;
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

    private final static String BUNDLE_SELECT_MODE = "bundle_select_mode";
    @BindView(R.id.gridview_check_box) GridView gridView;
    @BindView(R.id.my_id_view) TextView myIdView;
    private Context context;
    private List<Friend> listFriends;
    private ArrayList<String> listFriendsSelected;
    private CallbackFriendActivity callbackFriendActivity;
    private CallbackInvitActivity callbackInvitActivity;
    private FirebaseUser firebaseUser;
    private String myLogin;
    private Boolean SelectMode;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, view);
        defineMode();

        myLogin = "lolo91";

        FirebaseApp.initializeApp(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        listFriends = FriendsHandler.getListFriends(context);
        listFriends.add(new Friend()); // add item for "add a friend"

        configureViews();
        return view;
    }

    private void defineMode(){
        if(getArguments()!=null)
            SelectMode = getArguments().getBoolean(BUNDLE_SELECT_MODE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if(context instanceof CallbackFriendActivity){
            callbackFriendActivity = (CallbackFriendActivity) context;
            listFriendsSelected = new ArrayList<>();
        }

        if(context instanceof CallbackInvitActivity){
            callbackInvitActivity = (CallbackInvitActivity) context;
            listFriendsSelected = callbackInvitActivity.getInvitation().getListIdFriends();
            if(listFriendsSelected==null)
                listFriendsSelected = new ArrayList<>();
        }
    }

    private void configureViews(){

        // configure gridView
        gridView.setAdapter(new FriendsAdapter(context, listFriends, SelectMode, this));

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
        firebaseRecover.isLoginNotAmongUserFriends(login, firebaseUser.getUid(), new OnDataGetListener() {
            @Override
            public void onSuccess(Friend friend) {
                saveNewFriend(friend);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveNewFriend(Friend friend){

        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

        // Add friend to user Firebase "my_friends" with status "false" as accepted
        firebaseUpdate.updateFriend(firebaseUser.getUid(), friend);

        // Add user in friend Firebase "my_friends" with status "false" as accepted
        Friend user = new Friend(firebaseUser.getUid(),myLogin,firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString(),false);
        firebaseUpdate.updateFriend(friend.getId(),user);

        // Add friend in phone database
        FriendsHandler.insertNewFriend(context, friend);

        // Update layout
        listFriends.add(listFriends.size()-1, friend); // add new friend before the last item
        configureViews();
    }

    public ArrayList<String> getListFriendsSelected() {
        return listFriendsSelected;
    }

    public CallbackInvitActivity getCallbackInvitActivity() {
        return callbackInvitActivity;
    }
}
