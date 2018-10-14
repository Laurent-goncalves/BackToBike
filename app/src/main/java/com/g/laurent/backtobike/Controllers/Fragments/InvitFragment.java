package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.CallbackInvitActivity;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.ConfigureInvitFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvitFragment extends Fragment {

    @BindView(R.id.date_view) TextView dateView;
    @BindView(R.id.button_add_date) ImageButton buttonAddDate;
    @BindView(R.id.time_view) TextView timeView;
    @BindView(R.id.time_button) ImageButton buttonAddTime;
    @BindView(R.id.button_add_guests) ImageButton buttonAddGuests;
    @BindView(R.id.guests_views) LinearLayout guestsView;
    @BindView(R.id.spinner_list_routes) Spinner listRoutes;
    @BindView(R.id.comments_edit_text) EditText commentsView;
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_send) Button buttonSend;
    private CallbackInvitActivity callbackInvitActivity;
    private ConfigureInvitFragment config;
    private Context context;

    public InvitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context=context;
        if(context instanceof CallbackInvitActivity){
            callbackInvitActivity = (CallbackInvitActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invit, container, false);
        ButterKnife.bind(this, view);
        config = new ConfigureInvitFragment(view,this, callbackInvitActivity);
        config.configureViews(callbackInvitActivity.getInvitation());
        return view;
    }

    public CallbackInvitActivity getCallbackInvitActivity() {
        return callbackInvitActivity;
    }

    public ConfigureInvitFragment getConfig() {
        return config;
    }

    public Context getContext() {
        return context;
    }
}
