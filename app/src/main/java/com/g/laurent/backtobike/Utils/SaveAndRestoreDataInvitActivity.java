package com.g.laurent.backtobike.Utils;

import android.os.Bundle;
import com.g.laurent.backtobike.Controllers.Activities.EventActivity;
import com.g.laurent.backtobike.Models.Invitation;
import java.util.ArrayList;

public class SaveAndRestoreDataInvitActivity {

    private final static String BUNDLE_LIST_FRIENDS_ID = "bundle_list_friends_id";
    private final static String BUNDLE_ID_ROUTE = "bundle_id_route";
    private final static String BUNDLE_DATE = "bundle_date";
    private final static String BUNDLE_TIME = "bundle_time";
    private final static String BUNDLE_COMMENTS = "bundle_comments";

    // ----------------------------------- SAVE DATA

    public static void saveData(Bundle bundle, EventActivity eventActivity){
        if(bundle!=null && eventActivity !=null) {

            Invitation invit = eventActivity.getInvitation();

            bundle.putString(BUNDLE_DATE, invit.getDate());
            bundle.putString(BUNDLE_TIME, invit.getTime());
            bundle.putInt(BUNDLE_ID_ROUTE, invit.getIdRoute());
            bundle.putStringArrayList(BUNDLE_LIST_FRIENDS_ID, invit.getListIdFriends());
            bundle.putString(BUNDLE_COMMENTS, invit.getComments());
        }
    }

    // ----------------------------------- RESTORE DATA
    public static void restoreData(Bundle saveInstantState, EventActivity eventActivity){

        if(saveInstantState!=null && eventActivity !=null){

            String date = saveInstantState.getString(BUNDLE_DATE);
            String time = saveInstantState.getString(BUNDLE_TIME);
            int idRoute = saveInstantState.getInt(BUNDLE_ID_ROUTE, -1);
            ArrayList<String> listGuestsId = saveInstantState.getStringArrayList(BUNDLE_LIST_FRIENDS_ID);
            String comments = saveInstantState.getString(BUNDLE_COMMENTS);

            eventActivity.getInvitation().setDate(date);
            eventActivity.getInvitation().setTime(time);
            eventActivity.getInvitation().setIdRoute(idRoute);
            eventActivity.getInvitation().setListIdFriends(listGuestsId);
            eventActivity.getInvitation().setComments(comments);
        }
    }

}
