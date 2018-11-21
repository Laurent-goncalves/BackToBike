package com.g.laurent.backtobike.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.google.firebase.auth.FirebaseUser;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_EVENTS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_INVITS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_ROUTES;


public class UtilsApp {

    private static final String ONGOING = "ongoing";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";

    public static void resizeTextView(TextView commentView){
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(commentView, 14, 17, 1,
                TypedValue.COMPLEX_UNIT_DIP);
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------  ADD COUNT ON ICON APP  -----------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------  GET ID  --------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public static String getIdEvent(BikeEvent bikeEvent){
        String idInvitation = bikeEvent.getOrganizerId() + "_" + bikeEvent.getDate() + "_" + bikeEvent.getTime();
        idInvitation = idInvitation.replace("/","_");
        return idInvitation;
    }

    // ------------------------------------------------------------------------------------------------------------
    // ------------------------------------  TEMPERATURE  ---------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public static String getRoundValueTemperature(Double temperature){
        DecimalFormat twoDForm = new DecimalFormat("#");
        return String.valueOf(twoDForm.format(temperature)) + "°C";
    }

    // ------------------------------------------------------------------------------------------------------
    // ---------------------------------------- FIND IN LIST ------------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static int findFriendIndexInListFriends(Friend friend, List<Friend> listFriends){

        int index = -1;

        if(listFriends!=null){
            if(listFriends.size()>0){
                for(int i = 0; i < listFriends.size(); i++){
                    if(listFriends.get(i).getId().equals(friend.getId())){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListFriends(String idFriend, List<Friend> listFriends){

        int index = -1;

        if(listFriends!=null){
            if(listFriends.size()>0){
                for(int i = 0; i < listFriends.size(); i++){
                    if(listFriends.get(i).getId().equals(idFriend)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findIndexEventInList(String idEvent, List<BikeEvent> listBikeEvent){

        int index = -1;

        if(listBikeEvent!=null && idEvent !=null){
            if(listBikeEvent.size()>0){
                for(int i = 0; i < listBikeEvent.size() ; i++){
                    if(listBikeEvent.get(i).getId()!=null){
                        if(listBikeEvent.get(i).getId().equals(idEvent)){
                            index = i;
                            break;
                        }
                    }
                }
            }
        }

        return index;
    }

    public static int findIndexRouteInList(String idRoute, List<Route> listRoutes){

        int index = -1;

        if(listRoutes!=null && idRoute !=null){
            if(listRoutes.size()>0){
                for(int i = 0; i < listRoutes.size(); i++){
                    if(listRoutes.get(i).getId()==Integer.parseInt(idRoute)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListEventFriends(String idFriend, List<EventFriends> listEventFriends){

        int index = -1;

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(int i = 0; i < listEventFriends.size(); i++){
                    if(listEventFriends.get(i).getIdFriend().equals(idFriend)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListIds(String idFriend, ArrayList<String> listIds){

        int index = -1;

        if(listIds!=null){
            if(listIds.size()>0){
                for(int i = 0; i < listIds.size(); i++){
                    if(listIds.get(i).equals(idFriend)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    // ------------------------------------------------------------------------------------------------------
    // ---------------------------------------- TRANSFORM LIST ----------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public static List<String> transformListRouteIntoListRouteNames(List<Route> listRoutes){

        List<String> listRoutesNames = new ArrayList<>();

        if(listRoutes!=null){
            if(listRoutes.size()>0){
                for(Route route : listRoutes){
                    if(route.getName()==null)
                        listRoutesNames.add("");
                    else
                        listRoutesNames.add(route.getName());
                }
            }
        }

        return listRoutesNames;
    }

    public static List<EventFriends> positionOrganizerAtStartList(Context context, List<EventFriends> listEventFriends, BikeEvent event, EventFriends user, String idOrganizer){

        List<EventFriends> newlistEventFriends = new ArrayList<>(listEventFriends);

        if(!user.getIdFriend().equals(idOrganizer)){
            user.setAccepted(event.getStatus());
        }

        if(user.getIdFriend().equals(idOrganizer)){
            newlistEventFriends.add(0, user);
        } else {
            Friend friend = FriendsHandler.getFriend(context, idOrganizer, String.valueOf(user.getId()));
            EventFriends eventOrganizer = new EventFriends(0, event.getId(), friend.getId(), friend.getLogin(), ACCEPTED);
            if(friend!=null)
                newlistEventFriends.add(0, eventOrganizer);
        }

        int indexOrg = -1;

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(int i = 0; i< listEventFriends.size(); i++){
                    if(listEventFriends.get(i).getIdFriend()!=null){
                        if(listEventFriends.get(i).getIdFriend().equals(idOrganizer)){
                            indexOrg = i;
                            break;
                        }
                    }
                }

                if(indexOrg!=-1) {
                    newlistEventFriends.add(0, listEventFriends.get(indexOrg));
                    newlistEventFriends.remove(indexOrg + 1);
                }
            }
        }

        return newlistEventFriends;
    }

    public static String getAcceptanceEventUser(String idUser, List<EventFriends> eventFriendsList){

        String answer = ACCEPTED;

        if(eventFriendsList!=null){
            if(eventFriendsList.size()>0){
                for(EventFriends eventFriends : eventFriendsList){
                    if(eventFriends.getIdFriend().equals(idUser)) {
                        answer = eventFriends.getAccepted();
                        break;
                    }
                }
            }
        }

        return answer;
    }

    public static Friend getUserFromFirebaseUser(String mylogin, FirebaseUser user){

        String photoUrl = null;
        if(user.getPhotoUrl()!=null)
            photoUrl = user.getPhotoUrl().toString();

        return new Friend(user.getUid(), mylogin, user.getDisplayName(), photoUrl,REJECTED,ONGOING);
    }

    public static Boolean areRoutesEquals(Route route1, Route route2){

        Boolean answer = false;

        if(route1.getName().equals(route2.getName())){
            if(route1.getListRouteSegment().size()==route2.getListRouteSegment().size()) {
                // Compare routesegments
                for (int i = 1; i < route1.getListRouteSegment().size() + 1; i++) {

                    int index1 = findIndexSegmentNumber(i, route1.getListRouteSegment());
                    int index2 = findIndexSegmentNumber(i, route2.getListRouteSegment());

                    if (index1 == index2 && index1 != -1) {
                        if (route1.getListRouteSegment().get(index1).getLat().equals(route2.getListRouteSegment().get(index2).getLat()) &&
                                route1.getListRouteSegment().get(index1).getLng().equals(route2.getListRouteSegment().get(index2).getLng()))
                            answer = true;
                        else {
                            answer = false;
                            break;
                        }
                    } else {
                        answer = false;
                        break;
                    }
                }
            }
        }

        return answer;
    }

    private static int findIndexSegmentNumber(int number, List<RouteSegment> listRouteSegments){

        int index = -1;

        if(listRouteSegments!=null){
            if(listRouteSegments.size()>0){
                for(int i = 0; i<listRouteSegments.size(); i++){
                    if(listRouteSegments.get(i).getNumber()==number){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return Math.round(dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Boolean needLeftArrow(int position, int sizeList){

        if(sizeList==0)
            return false;
        else {
            return position != 0;
        }
    }

    public static Boolean needRightArrow(int position, int sizeList){

        if(sizeList==0)
            return false;
        else {
            return position != sizeList-1;
        }
    }

    public static int definePositionToDisplay(String typeDisplay, String idSelected, DisplayActivity displayActivity){
        switch (typeDisplay) {
            case DISPLAY_MY_ROUTES:
                return UtilsApp.findIndexRouteInList(idSelected, displayActivity.getListRoutes());
            case DISPLAY_MY_EVENTS:
                return UtilsApp.findIndexEventInList(idSelected, displayActivity.getListEvents());
            case DISPLAY_MY_INVITS:
                return UtilsApp.findIndexEventInList(idSelected, displayActivity.getListInvitations());
            default:
                return -1;
        }
    }

    // -------------------------------------------------------------------------------------------------
    // --------------------------------------- INTERNET ------------------------------------------------
    // -------------------------------------------------------------------------------------------------

    private static Boolean isWifiEnabled(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifi != null && wifi.isWifiEnabled();
        } else
            return false;
    }

    private static Boolean isNetworkEnabled(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Boolean isInternetAvailable(Context context){
        return isNetworkEnabled(context) || isWifiEnabled(context);
    }
}
