package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.ImageView;

import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Controllers.Fragments.DisplayFragment;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Models.Friend;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UtilsApp {

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

    public static String getIdEventFriend(String idFriend, BikeEvent bikeEvent){

        String idEventFriend = null;

        if(bikeEvent!=null){
            if(bikeEvent.getListEventFriends()!=null){
                if(bikeEvent.getListEventFriends().size()>0){
                    for(EventFriends eventFriends : bikeEvent.getListEventFriends()){
                        if(eventFriends.getIdFriend().equals(idFriend)) {
                            idEventFriend = eventFriends.getIdEvent();
                            break;
                        }
                    }
                }
            }
        }

        return idEventFriend;
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
    // ------------------------------------  DATE FORMAT  ---------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------

    public static String createStringDate(int year, int month, int dayOfMonth){

        String Day;
        int Month = month + 1;
        String new_month;

        if(dayOfMonth<10)
            Day = "0" + dayOfMonth;
        else
            Day = String.valueOf(dayOfMonth);

        if(Month<10)
            new_month = "0" + Month;
        else
            new_month = String.valueOf(Month);

        return Day + "/" + new_month + "/" + year;
    }

    public static String createStringTime(int hourOfDay, int minute){
        if(minute<10)
            return hourOfDay + ":0" + minute;
        else
            return hourOfDay + ":" + minute;
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
                for(int i = 0; i < listBikeEvent.size()-1 ; i++){
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
                for(int i = 0; i < listRoutes.size()-1 ; i++){
                    if(listRoutes.get(i).getId()==Integer.parseInt(idRoute)){
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public static int findFriendIndexInListEventFriends(Friend friend, List<EventFriends> listEventFriends){

        int index = -1;

        if(listEventFriends!=null){
            if(listEventFriends.size()>0){
                for(int i = 0; i < listEventFriends.size(); i++){
                    if(listEventFriends.get(i).getIdFriend().equals(friend.getId())){
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

    public static List<Friend> getListFriendsFromEventFriends(List<EventFriends> listEventFriends, String userId, Context context){

        List<Friend> listFriends = new ArrayList<>();

        if(listEventFriends!=null){
            if(listEventFriends.size()!=0){
                for(EventFriends eventFriends : listEventFriends){
                    listFriends.add(FriendsHandler.getFriend(context, eventFriends.getIdFriend(),userId));
                }
            }
        }

        return listFriends;
    }

    public static Friend getUserFromFirebaseUser(String mylogin, FirebaseUser user){

        String photoUrl = null;
        if(user.getPhotoUrl()!=null)
            photoUrl = user.getPhotoUrl().toString();

        return new Friend(user.getUid(), mylogin, user.getDisplayName(), photoUrl,false,null);
    }

    public static Boolean areRoutesEquals(Route route1, Route route2){

        Boolean answer = false;

        if(route1.getName().equals(route2.getName())){
            if(route1.getValid().equals(route2.getValid())){
                if(route1.getListRouteSegment().size()==route2.getListRouteSegment().size()){
                    // Compare routesegments
                    for(int i = 1; i < route1.getListRouteSegment().size()+1; i++){

                        int index1 = findIndexSegmentNumber(i,route1.getListRouteSegment());
                        int index2 = findIndexSegmentNumber(i,route2.getListRouteSegment());

                        if(index1==index2 && index1!=-1){
                            if(route1.getListRouteSegment().get(index1).getLat().equals(route2.getListRouteSegment().get(index2).getLat()) &&
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

    private static Boolean needLeftArrow(int position, int sizeList){

        if(sizeList==0)
            return false;
        else {
            return position != 0;
        }
    }

    private static Boolean needRightArrow(int position, int sizeList){

        if(sizeList==0)
            return false;
        else {
            return position != sizeList-1;
        }
    }

    public static void configureArrows(int position, int sizeList, ImageView arrowLeft, ImageView arrowRight, DisplayActivity activity){

        if(needLeftArrow(position,sizeList)) {
            arrowLeft.setVisibility(View.VISIBLE);
            arrowLeft.setOnClickListener(v -> activity.getPager().setCurrentItem(position-1));
        } else
            arrowLeft.setVisibility(View.INVISIBLE);

        if(needRightArrow(position,sizeList)) {
            arrowRight.setVisibility(View.VISIBLE);
            arrowRight.setOnClickListener(v -> activity.getPager().setCurrentItem(position+1));
        } else
            arrowRight.setVisibility(View.INVISIBLE);
    }
}
