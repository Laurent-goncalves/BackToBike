package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Configurations.ConfigureDisplayFragment;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;

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
    private CallbackDisplayActivity callbackDisplayActivity;
    private Context context;
    private int position;
    private MapView mapView;

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
        mapView = view.findViewById(R.id.map_layout).findViewById(R.id.map);

        if(getArguments()!=null){
            position = getArguments().getInt(BUNDLE_POSITION);
            String typeDisplay = getArguments().getString(BUNDLE_TYPE_DISPLAY);

            if(typeDisplay !=null){
                switch(typeDisplay){
                    case DISPLAY_MY_ROUTES:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay, callbackDisplayActivity.getListRoutes().get(position));
                        break;

                    case DISPLAY_MY_EVENTS:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay, FirebaseAuth.getInstance().getCurrentUser(), callbackDisplayActivity.getListEvents().get(position));
                        break;

                    case DISPLAY_MY_INVITS:
                        config = new ConfigureDisplayFragment(context, view, typeDisplay, FirebaseAuth.getInstance().getCurrentUser(), callbackDisplayActivity.getListInvitations().get(position));
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
            callbackDisplayActivity = (CallbackDisplayActivity) context;
        }
    }

    public int getPosition() {
        return position;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }
}
