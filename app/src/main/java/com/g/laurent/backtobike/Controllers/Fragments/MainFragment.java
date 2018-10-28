package com.g.laurent.backtobike.Controllers.Fragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.g.laurent.backtobike.Controllers.Activities.MainActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackMainActivity;
import com.g.laurent.backtobike.Models.CallbackWeather;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.WeatherApi.GetForecast;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;
import com.g.laurent.backtobike.Views.EventAdapter;
import com.g.laurent.backtobike.Views.EventViewHolder;
import com.g.laurent.backtobike.Views.WeatherAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import net.bytebuddy.dynamic.scaffold.MethodRegistry;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.g.laurent.backtobike.Utils.UtilsTime.getSeasonNumber;


public class MainFragment extends Fragment implements CallbackMainActivity {

    @BindView(R.id.weather_recyclerview) RecyclerView weatherView;
    @BindView(R.id.bikeevents_recyclerview) RecyclerView bikeEventView;
    @BindView(R.id.image_season) ImageView seasonImage;
    @BindView(R.id.title_center) ImageView centralTitle;
    @BindView(R.id.panel) ImageView panel;
    @BindView(R.id.layout_bike_event) LinearLayout bikeEventLayout;
    private Context context;
    private CallbackMainActivity callbackMainActivity;
    private MainActivity mMainActivity;
    private String userId;
    private Boolean panelExpanded;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(panelExpanded) {
                    slideUp(panel);
                    panelExpanded=false;
                } else {
                    slideDown(panel);
                    panelExpanded = true;
                }
            }
        });

        panelExpanded = false;
        userId = FirebaseAuth.getInstance().getUid();
        mMainActivity = (MainActivity) getActivity();
        callbackMainActivity = this;

        configureMainFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int heightLayout = bikeEventLayout.getHeight();
                int heightPanel = panel.getHeight();

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                lp.setMargins(0, heightLayout - 9 * heightPanel / 10, 0, 0);
                panel.setLayoutParams(lp);

            }
        }, 500);

        return view;
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

    private void configureEventsRecyclerView(){

        int seasonNum = getSeasonNumber();
        int drawableId = EventViewHolder.SeasonPicture.seasonDrawables[seasonNum];
        seasonImage.setImageDrawable(context.getResources().getDrawable(drawableId));

        // Get list of bikeEvent to display
        List<BikeEvent> listBikeEvent = BikeEventHandler.getAllFutureBikeEvents(context, userId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        EventAdapter eventAdapter = new EventAdapter(context, listBikeEvent, userId, callbackMainActivity);

        mMainActivity.runOnUiThread(() -> {
            // set adapter to recyclerView
            bikeEventView.setAdapter(eventAdapter);

            // Set layout manager to position the items
            bikeEventView.setLayoutManager(layoutManager);
        });
    }

    private void configureMainFragment(){

        GetForecast getForecast = new GetForecast();
        String apiKey = context.getResources().getString(R.string.weather_api_key);
        String language = context.getResources().getString(R.string.language);
        LatLng location = new LatLng(48.857327,2.336151);
        getForecast.getListWeathersByDay(apiKey, location, language, context, new CallbackWeather() {
            @Override
            public void onCompleted(List<WeatherForecast> listWeatherForecast) {
                configureWeatherRecyclerView(context, listWeatherForecast);
            }

            @Override
            public void onFailure(String error) {
                mMainActivity.runOnUiThread(() -> Toast.makeText(context, error, Toast.LENGTH_LONG).show());
            }
        });

        configureEventsRecyclerView();
    }

    private void configureWeatherRecyclerView(Context context, List<WeatherForecast> listWeatherForecast){

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        WeatherAdapter adapter = new WeatherAdapter(listWeatherForecast, context);

        mMainActivity.runOnUiThread(() -> {
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
        /*if(context instanceof CallbackEventActivity){
            mCallbackEventActivity = (CallbackEventActivity) context;
        }*/
    }

    @Override
    public void showEvent(BikeEvent bikeEvent) {


    }
}
