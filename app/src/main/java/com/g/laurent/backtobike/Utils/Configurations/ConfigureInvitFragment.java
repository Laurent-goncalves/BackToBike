package com.g.laurent.backtobike.Utils.Configurations;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.CalendarDialog;
import com.g.laurent.backtobike.Models.CallbackEventActivity;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Invitation;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.TimePickerFragment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.CheckAndSendInvitation;
import com.g.laurent.backtobike.Utils.EventFriendsHandler;
import com.g.laurent.backtobike.Utils.FriendsHandler;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.MY_ROUTE_TYPE;


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
    @BindView(R.id.map_layout) View mapLayout;
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private InvitFragment invitFragment;
    private Context context;
    private View view;
    private List<Route> listRoutes;
    private CallbackEventActivity mCallbackEventActivity;
    private ConfigureMap configMap;

    public ConfigureInvitFragment(View view, InvitFragment invitFragment, CallbackEventActivity callbackEventActivity) {
        ButterKnife.bind(this, view);
        listRoutes = new ArrayList<>();
        this.view = view;
        this.invitFragment=invitFragment;
        this.mCallbackEventActivity = callbackEventActivity;
        context = invitFragment.getContext();
        configMap = new ConfigureMap(context,mapLayout);
        view.findViewById(R.id.map_layout).findViewById(R.id.title_route).setVisibility(View.GONE); // remove title route
        view.findViewById(R.id.map_layout).findViewById(R.id.button_add_my_routes).setVisibility(View.GONE); // remove button add to my routes
    }

    // -----------------------------------------------------------------------------------------------------
    // ----------------------------------------- CONFIGURE VIEWS -------------------------------------------
    // -----------------------------------------------------------------------------------------------------

    public void configureViews(Invitation invitation){
        dateView.setText(invitation.getDate());
        timeView.setText(invitation.getTime());
        commentsView.setText(invitation.getComments());
        configureSpinner(invitation.getIdRoute());
        configureGuestsViews(invitation.getListIdFriends());
    }

    private void configureSpinner(int idRoute){

        if(mCallbackEventActivity!=null)
            listRoutes = RouteHandler.getMyRoutes(context, mCallbackEventActivity.getUserId());
        listRoutes.add(0,new Route(-1, context.getResources().getString(R.string.select_a_route_spinner), null, MY_ROUTE_TYPE));

        // Creating adapter for spinner
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                UtilsApp.transformListRouteIntoListRouteNames(listRoutes));

        // Drop down layout style
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach routeAdapter to spinner
        listRoutesView.setAdapter(routeAdapter);

        // Select item in spinner
        if(idRoute!=-1){ // if a route is already selected

            int index = UtilsApp.findIndexRouteInList(String.valueOf(idRoute) ,listRoutes);

            if(index!=-1){
                listRoutesView.setSelection(index);
                configMap.configureMapLayout(listRoutes.get(index));
            } else {
                listRoutesView.setSelection(0);
            }
        } else { // if no route selected
            listRoutesView.setSelection(0);
        }
    }

    private void configureGuestsViews(ArrayList<String> listIdFriends){

        if(listIdFriends!=null && mCallbackEventActivity!=null){
            if(listIdFriends.size()>0){
                for(String idFriend : listIdFriends){
                    Friend friend = FriendsHandler.getFriend(context, idFriend, mCallbackEventActivity.getUserId());
                    EventFriendsHandler.addGuestView(friend, context, guestsLayout,this);
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------
    // ------------------------------------------- LISTENERS -----------------------------------------------
    // -----------------------------------------------------------------------------------------------------

    @OnItemSelected(R.id.spinner_list_routes)
    public void onRouteSelected(){
        int position = listRoutesView.getSelectedItemPosition();

        if(position!=0 && mCallbackEventActivity!=null){
            mCallbackEventActivity.getInvitation().setIdRoute(listRoutes.get(position).getId());
            configMap.configureMapLayout(listRoutes.get(position));
        }
    }

    @OnTextChanged(R.id.comments_edit_text)
    public void onCommentsChanged(){
        if(mCallbackEventActivity!=null)
            mCallbackEventActivity.getInvitation().setComments(commentsView.getText().toString());
    }

    @OnClick(R.id.button_send)
    public void sendInvitation(){
        new CheckAndSendInvitation(this, view, context);
    }

    @OnClick(R.id.button_cancel)
    public void cancel(){
        if(mCallbackEventActivity!=null)
            mCallbackEventActivity.launchDisplayActivity(DISPLAY_MY_EVENTS, null);
    }

    @OnClick(R.id.button_add_guests)
    public void addGuests(){
        if(mCallbackEventActivity!=null)
            mCallbackEventActivity.configureAndShowFriendFragment();
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

    public CallbackEventActivity getCallbackEventActivity() {
        return mCallbackEventActivity;
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

    public void setTimeView(TextView timeView) {
        this.timeView = timeView;
    }
}
