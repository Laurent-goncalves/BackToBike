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

    private static final String BUNDLE_POSITION ="bundle_position";
    private static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    private ConfigureDisplayFragment config;
    private CallbackDisplayActivity callbackInvitActivity;
    private Context context;
    private int position;
    private String typeDisplay;
    private View view;

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
        view = inflater.inflate(R.layout.fragment_display, container, false);
        position = getArguments().getInt(BUNDLE_POSITION);
        typeDisplay = getArguments().getString(BUNDLE_TYPE_DISPLAY);
        config = new ConfigureDisplayFragment(context, view, typeDisplay);
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

}
