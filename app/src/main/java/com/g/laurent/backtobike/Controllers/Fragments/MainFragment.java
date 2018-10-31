package com.g.laurent.backtobike.Controllers.Fragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackMainActivity;
import com.g.laurent.backtobike.Models.CallbackWeather;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.MapTools.UtilsGoogleMaps;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.g.laurent.backtobike.Utils.WeatherApi.GetForecast;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;
import com.g.laurent.backtobike.Views.EventAdapter;
import com.g.laurent.backtobike.Views.EventViewHolder;
import com.g.laurent.backtobike.Views.WeatherAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.g.laurent.backtobike.Utils.UtilsTime.getSeasonNumber;


public class MainFragment extends Fragment {

    @BindView(R.id.weather_recyclerview) RecyclerView weatherView;
    @BindView(R.id.bikeevents_recyclerview) RecyclerView bikeEventView;
    @BindView(R.id.image_season) ImageView seasonImage;
    @BindView(R.id.title_center) ImageView centralTitle;
    @BindView(R.id.panel) RelativeLayout panel;
    @BindView(R.id.image_panel) ImageView imagePanel;
    @BindView(R.id.scrollview_panel) ScrollView panelScroll;
    @BindView(R.id.count_friends) TextView countFriends;
    @BindView(R.id.count_invitation) TextView countInvits;
    @BindView(R.id.count_events) TextView countEvents;
    @BindView(R.id.textview_differences) TextView differencesPanel;
    @BindView(R.id.layout_bike_event) LinearLayout bikeEventLayout;
    @BindView(R.id.title_weather) TextView titleWeather;
    private static final String DISPLAY_MY_EVENTS ="display_my_events";
    private static final String DISPLAY_MY_INVITS ="display_my_invits";
    private static final String BUNDLE_COUNTER_EVENTS = "bundle_counter_events";
    private static final String BUNDLE_COUNTER_INVITS = "bundle_counter_invits";
    private static final String BUNDLE_COUNTER_FRIENDS = "bundle_counter_friends";
    private static final String BUNDLE_DIFFERENCES = "bundle_differences";
    private static final String BUNDLE_LATITUDE = "bundle_latitude";
    private static final String BUNDLE_LONGITUDE = "bundle_longitude";
    private Context context;
    private CallbackMainActivity callbackMainActivity;
    private String userId;
    private Boolean panelExpanded;
    private LatLng currentLocation;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        userId = FirebaseAuth.getInstance().getUid();

        panelExpanded = false;
        panel.setOnClickListener(onClickPanelListener);
        panelScroll.setOnClickListener(onClickPanelListener);
        differencesPanel.setOnClickListener(onClickPanelListener);
        imagePanel.setOnClickListener(onClickPanelListener);

        if(getArguments()!=null){
            currentLocation = new LatLng(getArguments().getDouble(BUNDLE_LATITUDE),getArguments().getDouble(BUNDLE_LONGITUDE));
        }

        configureMainFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int heightLayout = bikeEventLayout.getHeight();
                int heightPanel = panel.getHeight();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, heightLayout - 9 * heightPanel / 10, 0, 0);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                panel.setLayoutParams(lp);
            }
        }, 500);

        return view;
    }

    View.OnClickListener onClickPanelListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(panelExpanded) {
                slideUp(panel);
                panelExpanded=false;
            } else {
                slideDown(panel);
                panelExpanded = true;
            }
        }
    };

    private void configureCountersAndPanel(){

        if(getArguments()!=null){

            int counterEvents = getArguments().getInt(BUNDLE_COUNTER_EVENTS, 0);
            int counterInvits = getArguments().getInt(BUNDLE_COUNTER_INVITS, 0);
            int counterFriends = getArguments().getInt(BUNDLE_COUNTER_FRIENDS, 0);
            String differences = getArguments().getString(BUNDLE_DIFFERENCES, null);

            if(counterFriends>9){
                String text = "+9";
                countFriends.setText(text);
            } else if(counterFriends==0)
                countFriends.setVisibility(View.GONE);
            else
                countFriends.setText(String.valueOf(counterFriends));

            if(counterInvits>9){
                String text = "+9";
                countInvits.setText(text);
            } else if(counterInvits==0)
                countInvits.setVisibility(View.GONE);
            else
                countInvits.setText(String.valueOf(counterInvits));

            if(counterEvents>9){
                String text = "+9";
                countEvents.setText(text);
            } else if(counterEvents==0)
                countEvents.setVisibility(View.GONE);
            else
                countEvents.setText(String.valueOf(counterEvents));

            if(differences.equals("")) {
                differencesPanel.setText(context.getResources().getString(R.string.no_new_information));
            } else
                differencesPanel.setText(differences);
        }
    }

    // slide the view from its current position to below itself
    public void slideUp(View view){

        centralTitle.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0f, 1.0f);
        anim.setDuration(1000);
        centralTitle.startAnimation(anim);

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 0);
        animation.setDuration(1000);
        animation.start();
    }

    // slide the view from below itself to the current position
    public void slideDown(View view){

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0f);
        anim.setDuration(1000);
        centralTitle.startAnimation(anim);
        centralTitle.setVisibility(View.INVISIBLE);

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY",   !panelExpanded ? (9*view.getHeight()/10) : 0);
        animation.setDuration(1000);
        animation.start();
    }

    @OnClick(R.id.button_invitations)
    public void clickOnInvitationButton(){
        callbackMainActivity.launchDisplayActivity(DISPLAY_MY_INVITS, null);
    }

    @OnClick(R.id.button_new_friends)
    public void clickOnFriendsButton(){
        callbackMainActivity.launchFriendsActivity();
    }

    @OnClick(R.id.button_events)
    public void clickOnEventsButton(){
        callbackMainActivity.launchDisplayActivity(DISPLAY_MY_EVENTS, null);
    }

    @OnClick(R.id.button_disconnect)
    public void clickOnSignOutButton(){
        callbackMainActivity.signOutUserFromFirebase(context);
    }

    private void configureEventsRecyclerView(){

        // Define which image to show according to current season
        int seasonNum = getSeasonNumber();
        int drawableId = EventViewHolder.SeasonPicture.seasonDrawables[seasonNum];
        seasonImage.setImageDrawable(context.getResources().getDrawable(drawableId));

        // Get list of bikeEvent to display
        List<BikeEvent> listBikeEvent = UtilsTime.getListBikeEventByChronologicalOrder(BikeEventHandler.getAllFutureBikeEvents(context, userId));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        EventAdapter eventAdapter = new EventAdapter(context, listBikeEvent, userId, callbackMainActivity);

        callbackMainActivity.getMainActivity().runOnUiThread(() -> {
            // set adapter to recyclerView
            bikeEventView.setAdapter(eventAdapter);

            // Set layout manager to position the items
            bikeEventView.setLayoutManager(layoutManager);
        });
    }

    private void setTitleWeather(LatLng currentLocation){
        String city = null;

        try {
            city = UtilsGoogleMaps.getCityWithLatLng(context, currentLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(city!=null) {
            String text = context.getResources().getString(R.string.weather_forecast_at) + " " + city;
            titleWeather.setText(text);
        } else {
            String text = context.getResources().getString(R.string.weather_forecast);
            titleWeather.setText(text);
        }
    }

    private void configureMainFragment(){

        setTitleWeather(currentLocation);
        GetForecast getForecast = new GetForecast();
        String apiKey = context.getResources().getString(R.string.weather_api_key);
        String language = context.getResources().getString(R.string.language);
        getForecast.getListWeathersByDay(apiKey, currentLocation, language, context, new CallbackWeather() {
            @Override
            public void onCompleted(List<WeatherForecast> listWeatherForecast) {
                configureWeatherRecyclerView(context, listWeatherForecast);
            }

            @Override
            public void onFailure(String error) {
                callbackMainActivity.getMainActivity().runOnUiThread(() -> Toast.makeText(context, error, Toast.LENGTH_LONG).show());
            }
        });

        configureEventsRecyclerView();
        configureCountersAndPanel();
    }

    private void configureWeatherRecyclerView(Context context, List<WeatherForecast> listWeatherForecast){

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        WeatherAdapter adapter = new WeatherAdapter(listWeatherForecast, context);

        callbackMainActivity.getMainActivity().runOnUiThread(() -> {
            // set adapter to recyclerView
            weatherView.setAdapter(adapter);

            // Set layout manager to position the items
            weatherView.setLayoutManager(layoutManager);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context=context;

        if(context instanceof CallbackMainActivity){
            callbackMainActivity = (CallbackMainActivity) context;
        }
    }


}
