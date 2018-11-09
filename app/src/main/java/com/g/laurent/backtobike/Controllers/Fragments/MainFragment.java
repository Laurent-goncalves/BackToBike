package com.g.laurent.backtobike.Controllers.Fragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackMainActivity;
import com.g.laurent.backtobike.Models.CallbackWeather;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.RouteSegment;
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
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.g.laurent.backtobike.Utils.UtilsTime.getSeasonNumber;


public class MainFragment extends Fragment {

    @BindView(R.id.weather_recyclerview) RecyclerView weatherView;
    @BindView(R.id.bikeevents_recyclerview) RecyclerView bikeEventView;
    @BindView(R.id.image_season) ImageView seasonImage;
    @BindView(R.id.title_center) ImageView centralTitle;
    @BindView(R.id.panel) RelativeLayout panel;
    @BindView(R.id.image_panel) ImageView imagePanel;
    @BindView(R.id.count_friends) TextView countFriends;
    @BindView(R.id.count_invitation) TextView countInvits;
    @BindView(R.id.count_events) TextView countEvents;
    @BindView(R.id.textview_differences) TextView differencesPanel;
    @BindView(R.id.layout_bike_event) LinearLayout bikeEventLayout;
    @BindView(R.id.title_weather) TextView titleWeather;
    private final static String MENU_MAIN_PAGE = "menu_main_page";
    private static final String DISPLAY_MY_ROUTES ="display_my_routes";
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
    private String serverKey = "AAAAg5ho7EE:APA91bG9pRx7eyOE5JT4O_bBm7c5AcHEVHyyMHVrH9R9d9dxorr3tXCH0bFF-a-_UuOr469a6oX_xOvPvNI6N_-a3s0ONjxUDHq5_k_MAn8uHZ8_EtpYXDG8bMOQ5q0xllqaH5Qv3ic4";

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
        differencesPanel.setOnClickListener(onClickPanelListener);
        imagePanel.setOnClickListener(onClickPanelListener);
        differencesPanel.setMovementMethod(new ScrollingMovementMethod());

        centralTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            sendNotification(token);

                            System.out.println("eee  token=" + token);
                        });

                /*FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);

                BikeEvent invitation = new BikeEvent("id2_28_11_2018_14:00", "id2", "28/11/2018", "14:00", 0, "Comments : take good shoes", "ongoing");
                Route route = new Route(0,"Trip to Paris",false);
                route.setListRouteSegment(getListRouteSegments());
                invitation.setRoute(route);
                invitation.setListEventFriends(getListEventFriends());

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference()
                        .child("users").child(userId).child("my_invitations").child("id2_28_11_2018_14:00");


                firebaseUpdate.setInvitation(databaseReference, invitation);
                firebaseUpdate.setRoute(databaseReference.child("route"), route);
                firebaseUpdate.setRouteSegment(databaseReference.child("route"), getListRouteSegments());*/
            }
        });

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

    @OnClick(R.id.button_my_routes)
    public void clickOnSignOutButton(){
        callbackMainActivity.launchDisplayActivity(DISPLAY_MY_ROUTES,null);
    }

    public String send(String to,  String body) {
        try {

            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + serverKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", to);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", "nouvelle notif");
            notification.put("body", body);
            message.put("data", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","key=" + serverKey)
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    private void sendNotification(String to){

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject param = new JSONObject();
            jsonObject.put("data", param);
            jsonObject.put("to", to);
            jsonObject.put("priority", "high");
            jsonObject.put("content_available", true);

            JSONObject notification = new JSONObject();
            notification.put("title", "nouvelle notif");
            notification.put("body", "body");
            jsonObject.put("data", notification);

            post("https://fcm.googleapis.com/fcm/send", jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //Something went wrong
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseStr = response.body().string();
                                Log.d("Response", responseStr);
                                // Do what you want to do with the response.
                            } else {
                                // Request not successful
                            }
                        }
                    }
            );
        } catch (JSONException ex) {
            Log.d("Exception", "JSON exception", ex);
        }
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


    private List<RouteSegment> getListRouteSegments(){

        List<RouteSegment> listRouteSegments = new ArrayList<>();
        RouteSegment ROUTE_SEG1_DEMO = new RouteSegment(0,1,48.819446, 2.344624,999);
        RouteSegment ROUTE_SEG2_DEMO = new RouteSegment(0,2,48.885412, 2.336589,999);
        RouteSegment ROUTE_SEG3_DEMO = new RouteSegment(0,3,48.874563, 2.312778,999);
        RouteSegment ROUTE_SEG4_DEMO = new RouteSegment(0,4,48.858933, 2.321511,999);

        listRouteSegments.add(ROUTE_SEG1_DEMO);
        listRouteSegments.add(ROUTE_SEG2_DEMO);
        listRouteSegments.add(ROUTE_SEG3_DEMO);
        listRouteSegments.add(ROUTE_SEG4_DEMO);

        return listRouteSegments;
    }

    private List<EventFriends> getListEventFriends(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"id1_01_01_2000_14:00","id2","id2","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(0,"id1_01_01_2000_14:00","id3","id3","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);

        return listEventFriends;
    }

}
