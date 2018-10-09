package com.g.laurent.backtobike.Utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Models.CalendarDialog;
import com.g.laurent.backtobike.Models.TimePickerFragment;
import com.g.laurent.backtobike.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConfigureInvitFragment {

    @BindView(R.id.date_view) TextView dateView;
    @BindView(R.id.button_add_date) ImageButton buttonAddDate;
    @BindView(R.id.time_view) TextView timeView;
    @BindView(R.id.time_button) ImageButton buttonAddTime;
    @BindView(R.id.button_add_guests) ImageButton buttonAddGuests;
    @BindView(R.id.guests_views) LinearLayout guestsView;
    @BindView(R.id.spinner_list_routes) Spinner listRoutes;
    @BindView(R.id.mileage_time_estimation) TextView mileageView;
    @BindView(R.id.comments_edit_text) EditText commentsView;
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_send) Button buttonSend;
    private InvitFragment invitFragment;
    private Context context;

    public ConfigureInvitFragment(View view, InvitFragment invitFragment) {
        ButterKnife.bind(this, view);
        this.invitFragment=invitFragment;
    }

    private void configureViews(){


    }

    @OnClick(R.id.button_add_guests)
    public void addGuests(){

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

}
