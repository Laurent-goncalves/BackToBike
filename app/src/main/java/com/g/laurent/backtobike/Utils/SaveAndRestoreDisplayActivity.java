package com.g.laurent.backtobike.Utils;

import android.os.Bundle;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;


public class SaveAndRestoreDisplayActivity {

    private static final String BUNDLE_TYPE_ROUTES ="bundle_type_routes";
    private static final String BUNDLE_TYPE_EVENTS ="bundle_type_events";
    private static final String BUNDLE_TYPE_INVITS ="bundle_type_invits";
    private static final String BUNDLE_TYPE_DISPLAY ="bundle_type_display";
    private static final String BUNDLE_POSITION ="bundle_position";

    // ----------------------------------- SAVE DATA
    public static void saveData(Bundle bundle, DisplayActivity displayActivity){
        if(bundle!=null && displayActivity!=null) {
            bundle.putString(BUNDLE_TYPE_DISPLAY, displayActivity.getTypeDisplay());
            bundle.putInt(BUNDLE_POSITION, displayActivity.getPosition());
        }
    }

    // ----------------------------------- RESTORE DATA
    public static void restoreData(Bundle saveInstantState, DisplayActivity displayActivity){

        if(saveInstantState!=null && displayActivity!=null){

            String typeDisplay = saveInstantState.getString(BUNDLE_TYPE_DISPLAY);
            int position = saveInstantState.getInt(BUNDLE_POSITION, -1);

            displayActivity.setTypeDisplay(typeDisplay);
            displayActivity.setPosition(position);

            defineListToShow(typeDisplay);
        }
    }

    private static void defineListToShow(String typeDisplay){

        switch(typeDisplay){
            case BUNDLE_TYPE_ROUTES:

                break;
            case BUNDLE_TYPE_EVENTS:

                break;
            case BUNDLE_TYPE_INVITS:

                break;
        }
    }

}
