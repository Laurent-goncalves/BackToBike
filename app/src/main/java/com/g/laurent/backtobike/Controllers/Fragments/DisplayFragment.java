package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.ConfigureDisplayFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {

    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    private static final String BUNDLE_POSITION ="bundle_position";
    private static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    private ConfigureDisplayFragment config;
    private CallbackDisplayActivity callbackInvitActivity;
    private Context context;
    private int position;

    public DisplayFragment() {
        // Required empty public constructor
    }

    public static DisplayFragment newInstance(int position, String typeDisplay) {

        // Create new display fragment
        DisplayFragment frag = new DisplayFragment();

        // Create bundle
        Bundle args = new Bundle();
        args.putInt(BUNDLE_POSITION, position);
        args.putString(BUNDLE_TYPE_DISPLAY, typeDisplay);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);
        if(getArguments()!=null){
            position = getArguments().getInt(BUNDLE_POSITION);
            String typeDisplay = getArguments().getString(BUNDLE_TYPE_DISPLAY);

            if(typeDisplay !=null){
                switch(typeDisplay){
                    case DISPLAY_MY_ROUTES:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay, callbackInvitActivity.getListRoutes().get(position));
                        break;

                    case DISPLAY_MY_EVENTS:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay,callbackInvitActivity.getListEvents().get(position));
                        break;

                    case DISPLAY_MY_INVITS:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay,callbackInvitActivity.getListInvitations().get(position));
                        break;
                }
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        if(context instanceof CallbackDisplayActivity){
            callbackInvitActivity = (CallbackDisplayActivity) context;
        }
    }

    public int getPosition() {
        return position;
    }
}
