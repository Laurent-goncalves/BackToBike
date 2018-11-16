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
import com.g.laurent.backtobike.Models.CallbackEventActivity;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Configurations.ConfigureInvitFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


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
    private CallbackEventActivity mCallbackEventActivity;
    private ConfigureInvitFragment config;
    private Context context;

    public InvitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context=context;
        if(context instanceof CallbackEventActivity){
            mCallbackEventActivity = (CallbackEventActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, view);

        if(context==null)
            context= getActivity().getApplicationContext();

        config = new ConfigureInvitFragment(view,this, mCallbackEventActivity);
        config.configureViews(mCallbackEventActivity.getInvitation());
        return view;
    }

    public CallbackEventActivity getCallbackEventActivity() {
        return mCallbackEventActivity;
    }

    public ConfigureInvitFragment getConfig() {
        return config;
    }

    public Context getContext() {
        return context;
    }
}
