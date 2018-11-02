package com.g.laurent.backtobike.Controllers.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.g.laurent.backtobike.Models.AlarmEvent;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackBaseActivity;
import com.g.laurent.backtobike.Models.CallbackCounters;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.NetworkSchedulerService;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.ToolbarManager;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.FirebaseRecover;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements CallbackBaseActivity{

    protected final static String MENU_MAIN_PAGE = "menu_main_page";
    protected final static String MENU_MY_FRIENDS = "menu_my_friends";
    protected final static String DISPLAY_MY_ROUTES ="display_my_routes";
    protected final static String DISPLAY_MY_EVENTS ="display_my_events";
    protected final static String DISPLAY_MY_INVITS ="display_my_invits";
    protected final static String MENU_TRACE_ROUTE = "menu_trace_route";
    protected final static String MENU_CREATE_EVENT = "menu_create_event";
    protected static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    protected static final String BUNDLE_ROUTE_ID ="bundle_route_id";
    protected static final String BUNDLE_ID ="bundle_id";
    protected static final String LOGIN_SHARED ="login_shared";
    protected static final int SIGN_OUT_TASK = 10;
    protected final static String MENU_SIGN_OUT= "menu_sign_out";
    protected static final String EXTRA_EVENT_ID ="extra_event_id";
    protected static final String EXTRA_USER_ID ="extra_user_id";
    protected static final String EXTRA_TYPE_ALARM ="extra_type_alarm";
    protected static final String ALARM_2_DAYS ="alarm_2_days";
    protected static final String ALARM_4_HOURS ="alarm_4_hours";
    protected static final int PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 44;
    protected CallbackBaseActivity callbackBaseActivity;
    protected ToolbarManager toolbarManager;
    protected String userId;
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbarManager = new ToolbarManager();
        callbackBaseActivity = this;

        if (getApplicationContext() != null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_WIFI_STATE},
                        PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }


    protected void assignToolbarViews(){

        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activity_drawer_layout);
        navigationView = findViewById(R.id.activity_nav_view);
    }

    public void launchAuthActivity(){
        Intent intent = new Intent(this,AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchFriendsActivity(){
        Intent intent = new Intent(this,FriendsActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchEventActivity(){
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchDisplayActivity(String typeDisplay, String id){
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra(BUNDLE_TYPE_DISPLAY,typeDisplay);
        intent.putExtra(BUNDLE_ID, id);
        startActivity(intent);
        finish();
    }

    public void launchTraceActivity(Route route){

        Intent intent = new Intent(this, TraceActivity.class);
        if(route!=null)
            intent.putExtra(BUNDLE_ROUTE_ID, route.getId());
        startActivity(intent);
        finish();
    }

    public void defineCountersAndConfigureToolbar(String typeDisplay){

        if(UtilsApp.isInternetAvailable(getApplicationContext())){

            FirebaseRecover firebaseRecover = new FirebaseRecover(getApplicationContext());
            firebaseRecover.recoverDatasForCounters(userId, getApplicationContext(), new CallbackCounters() {
                @Override
                public void onCompleted(List<Difference> differenceList, List<String> differenceStringList,int counterFriend, int counterEvents, int counterInvits) {
                    toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, counterFriend, counterEvents, counterInvits);
                    int count = counterFriend + counterEvents + counterInvits;
                    UtilsApp.setBadge(getApplicationContext(), count);
                }

                @Override
                public void onFailure(String error) {
                    toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, 0, 0,0);
                    UtilsApp.setBadge(getApplicationContext(), 0);
                }
            });
        } else {
            toolbarManager.configureToolbar(callbackBaseActivity, typeDisplay, 0, 0,0);
        }

    }

    public void configureAlarmManager(BikeEvent event) {

        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent2Days = new Intent(getApplicationContext(), AlarmEvent.class);
        intent2Days.putExtra(EXTRA_USER_ID, userId); // attach the userId to the intent
        intent2Days.putExtra(EXTRA_EVENT_ID, event.getId()); // attach the idEvent to the intent
        intent2Days.putExtra(EXTRA_TYPE_ALARM, ALARM_2_DAYS); // attach the type of alarm to the intent
        int idIntent2days = UtilsTime.generatePendingIntentID_2days(event.getDate(),event.getTime());
        int idIntentDayEvent = UtilsTime.generatePendingIntentID_4hours(event.getDate(),event.getTime());

        Intent intentDayEvent = new Intent(getApplicationContext(), AlarmEvent.class);
        intentDayEvent.putExtra(EXTRA_USER_ID, userId); // attach the userId to the intent
        intentDayEvent.putExtra(EXTRA_EVENT_ID, event.getId()); // attach the idEvent to the intent
        intentDayEvent.putExtra(EXTRA_TYPE_ALARM, ALARM_4_HOURS); // attach the type of alarm to the intent

        PendingIntent alarmIntent2days = PendingIntent.getBroadcast(getApplicationContext(), idIntent2days, intent2Days, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent alarmIntentDayEvent = PendingIntent.getBroadcast(getApplicationContext(), idIntentDayEvent, intentDayEvent, PendingIntent.FLAG_ONE_SHOT);

        // Set the first alarm to start at 12:00, 2 days before event.
        Calendar calendar2days = UtilsTime.getCalendarAlarm2daysbefore(event.getDate());

        // Set the second alarm to start 4 hours before event.
        Calendar calendarDayEvent = UtilsTime.getCalendarAlarmdayevent(event.getDate(), event.getTime());

        // Set alarm
        if (alarmMgr != null) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar2days.getTimeInMillis(), alarmIntent2days);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarDayEvent.getTimeInMillis(), alarmIntentDayEvent);
        }
    }

    public void cancelAlarmEvent(BikeEvent event) {

        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent2Days = new Intent(getApplicationContext(), AlarmEvent.class);
        Intent intentDayEvent = new Intent(getApplicationContext(), AlarmEvent.class);

        int idIntent2days = UtilsTime.generatePendingIntentID_2days(event.getDate(),event.getTime());
        int idIntentDayEvent = UtilsTime.generatePendingIntentID_4hours(event.getDate(),event.getTime());

        PendingIntent alarmIntent2days = PendingIntent.getBroadcast(getApplicationContext(), idIntent2days, intent2Days, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent alarmIntentDayEvent = PendingIntent.getBroadcast(getApplicationContext(), idIntentDayEvent, intentDayEvent, PendingIntent.FLAG_ONE_SHOT);

        // Cancel alarm
        if (alarmMgr != null) {

            alarmIntent2days.cancel();
            alarmIntentDayEvent.cancel();

            alarmMgr.cancel(alarmIntent2days);
            alarmMgr.cancel(alarmIntentDayEvent);
        }
    }

    public static void showSnackBar(BaseActivity baseActivity, String text) {
        //Snackbar.make(baseActivity.findViewById(R.id.fragment_position), text, Snackbar.LENGTH_LONG).show();
    }

    public void signOutUserFromFirebase(Context context) {
        AuthUI.getInstance()
                .signOut(context)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));

        launchAuthActivity();
    }

    protected OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }

    public BaseActivity getBaseActivity(){
        return this;
    }

    public Context getContextBaseActivity(){
        return getApplicationContext();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }
}
