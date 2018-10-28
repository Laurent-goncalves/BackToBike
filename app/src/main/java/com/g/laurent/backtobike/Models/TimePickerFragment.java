package com.g.laurent.backtobike.Models;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsTime;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final static String TAG_INVIT_FRAGMENT = "tag_invit_fragment";
    InvitFragment invitFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        invitFragment = (InvitFragment) getActivity().getFragmentManager().findFragmentByTag(TAG_INVIT_FRAGMENT);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = UtilsTime.createStringTime(hourOfDay, minute);
        invitFragment.getConfig().getTimeView().setText(time); // change time selected
        invitFragment.getCallbackEventActivity().getInvitation().setTime(time);
    }
}
