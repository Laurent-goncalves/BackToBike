package com.g.laurent.backtobike.Controllers.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.g.laurent.backtobike.Models.CallbackDisplayActivity;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Configurations.ConfigureDisplayFragment;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;

import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_EVENTS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_INVITS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_ROUTES;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {

    private static final String BUNDLE_POSITION ="bundle_position";
    private static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);
        mapView = view.findViewById(R.id.map_layout).findViewById(R.id.map);

        if(getArguments()!=null){
            position = getArguments().getInt(BUNDLE_POSITION);
            String typeDisplay = getArguments().getString(BUNDLE_TYPE_DISPLAY);

            if(typeDisplay !=null){
                switch(typeDisplay){
                    case DISPLAY_MY_ROUTES:
                        new ConfigureDisplayFragment(context, view, typeDisplay, callbackDisplayActivity.getListRoutes().get(position));
                        break;

                    case DISPLAY_MY_EVENTS:
                        new ConfigureDisplayFragment(context, view, typeDisplay, FirebaseAuth.getInstance().getCurrentUser(), callbackDisplayActivity.getListEvents().get(position));
                        break;

                    case DISPLAY_MY_INVITS:
                        new ConfigureDisplayFragment(context, view, typeDisplay, FirebaseAuth.getInstance().getCurrentUser(), callbackDisplayActivity.getListInvitations().get(position));
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }
}
