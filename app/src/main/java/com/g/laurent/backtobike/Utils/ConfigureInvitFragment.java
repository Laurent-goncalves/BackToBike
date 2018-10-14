package com.g.laurent.backtobike.Utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.AppDatabase;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CalendarDialog;
import com.g.laurent.backtobike.Models.CallbackInvitActivity;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Models.TimePickerFragment;
import com.g.laurent.backtobike.R;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.internal.Utils;


public class ConfigureInvitFragment {

    @BindView(R.id.date_view) TextView dateView;
    @BindView(R.id.button_add_date) ImageButton buttonAddDate;
    @BindView(R.id.time_view) TextView timeView;
    @BindView(R.id.time_button) ImageButton buttonAddTime;
    @BindView(R.id.button_add_guests) ImageButton buttonAddGuests;
    @BindView(R.id.guests_views) LinearLayout guestsLayout;
    @BindView(R.id.spinner_list_routes) Spinner listRoutesView;
    @BindView(R.id.comments_edit_text) EditText commentsView;
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_send) Button buttonSend;
    @BindView(R.id.map_layout) FrameLayout mapLayout;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.layout_time_mileage) RelativeLayout timeMileageLayout;
    @BindView(R.id.placeholder_map) View mapPlaceholder;
    private InvitFragment invitFragment;
    private Context context;
    private View view;
    private List<Route> listRoutes;
    private CallbackInvitActivity callbackInvitActivity;
    private ConfigureMap configMap;

    public ConfigureInvitFragment(View view, InvitFragment invitFragment, CallbackInvitActivity callbackInvitActivity) {
        ButterKnife.bind(this, view);
        this.view = view;
        this.invitFragment=invitFragment;
        this.callbackInvitActivity=callbackInvitActivity;
        context = invitFragment.getContext();
        configMap = new ConfigureMap(context, view);
    }

    public void configureViews(Invitation invitation){
        dateView.setText(invitation.getDate());
        timeView.setText(invitation.getTime());
        commentsView.setText(invitation.getComments());
        configureSpinner(invitation.getIdRoute());
        configureGuestsViews(invitation.getListIdFriends());
    }

    private void configureSpinner(int idRoute){

        listRoutes = RouteHandler.getAllRoutes(context);
        listRoutes.add(0,new Route(-1,"Select a route",false,null));

        // Creating adapter for spinner
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                UtilsApp.transformListRouteIntoListRouteNames(listRoutes));

        // Drop down layout style
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach routeAdapter to spinner
        listRoutesView.setAdapter(routeAdapter);

        // Select item in spinner
        if(idRoute!=-1){ // if a route is already selected

            int index = UtilsApp.findIndexRouteInList(idRoute,listRoutes);

            if(index!=-1){
                displayMap(true);
                listRoutesView.setSelection(index);
                configMap.configureMapLayout(listRoutes.get(index));
            } else {
                listRoutesView.setSelection(0);
                displayMap(false);
            }
        } else { // if no route selected
            listRoutesView.setSelection(0);
            displayMap(false);
        }
    }

    private void configureGuestsViews(ArrayList<String> listIdFriends){

        if(listIdFriends!=null){
            if(listIdFriends.size()>0){
                for(String idFriend : listIdFriends){
                    Friend friend = FriendsHandler.getFriend(context, idFriend);
                    EventFriendsHandler.addGuestView(friend, context, guestsLayout,this);
                }
            }
        }
    }

    @OnItemSelected(R.id.spinner_list_routes)
    public void onRouteSelected(){
        int position = listRoutesView.getSelectedItemPosition();

        if(position!=0){
            displayMap(true);
            callbackInvitActivity.getInvitation().setIdRoute(listRoutes.get(position).getId());
            configMap.configureMapLayout(listRoutes.get(position));
        } else {
            displayMap(false);
        }
    }

    private void displayMap(Boolean display){
        if(display){
            mapView.setVisibility(View.VISIBLE);
            mapPlaceholder.setVisibility(View.GONE);
            timeMileageLayout.setVisibility(View.VISIBLE);
        } else {
            mapView.setVisibility(View.GONE);
            mapPlaceholder.setVisibility(View.VISIBLE);
            timeMileageLayout.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.comments_edit_text)
    public void onCommentsChanged(){
        callbackInvitActivity.getInvitation().setComments(commentsView.getText().toString());
    }

    @OnClick(R.id.button_send)
    public void sendInvitation(){
        new CheckAndSendInvitation(this, view, context);
    }

    @OnClick(R.id.button_add_guests)
    public void addGuests(){
        callbackInvitActivity.configureAndShowFriendFragment();
    }

    @OnClick(R.id.date_picker_layout)
    public void changeDate(){
        FragmentTransaction ft = invitFragment.getFragmentManager().beginTransaction();
        Fragment prev = invitFragment.getFragmentManager().findFragmentByTag("calendarDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        CalendarDialog calendarDialog = CalendarDialog.newInstance();
        calendarDialog.setTargetFragment(invitFragment,0);
        calendarDialog.show(ft, "calendarDialog");
    }

    @OnClick(R.id.time_picker_layout)
    public void showTimePicker() {

        FragmentTransaction ft = invitFragment.getFragmentManager().beginTransaction();
        Fragment prev = invitFragment.getFragmentManager().findFragmentByTag("timePicker");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(invitFragment.getFragmentManager(), "timePicker");
    }

    // -----------------------------------------------------------------------------------------------------
    // ------------------------------------------- GUESTS --------------------------------------------------
    // -----------------------------------------------------------------------------------------------------


    public CallbackInvitActivity getCallbackInvitActivity() {
        return callbackInvitActivity;
    }

    public InvitFragment getInvitFragment() {
        return invitFragment;
    }

    public TextView getDateView() {
        return dateView;
    }

    public TextView getTimeView() {
        return timeView;
    }

    public EditText getCommentsView() {
        return commentsView;
    }

    public void setTimeView(TextView timeView) {
        this.timeView = timeView;
    }
}
