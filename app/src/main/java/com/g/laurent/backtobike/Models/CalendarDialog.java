package com.g.laurent.backtobike.Models;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import com.g.laurent.backtobike.Controllers.Fragments.InvitFragment;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsApp;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarDialog extends DialogFragment {

    @BindView(R.id.calendar_view_dialog)
    CalendarView calendarView;

    public CalendarDialog() {
    }

    public static CalendarDialog newInstance() {
        return new CalendarDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_dialog, container, false);
        ButterKnife.bind(this,v);
        configureCalendarView();
        return v;
    }

    private void configureCalendarView() {

        calendarView.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {

            String date = UtilsApp.createStringDate(year, month, dayOfMonth);

            InvitFragment invitFragment = (InvitFragment) getTargetFragment();
            invitFragment.getDateView().setText(date); // change date selected into string

            this.dismiss();
        }));
    }
}
