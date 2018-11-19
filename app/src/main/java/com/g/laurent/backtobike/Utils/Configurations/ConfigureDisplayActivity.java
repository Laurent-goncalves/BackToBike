package com.g.laurent.backtobike.Utils.Configurations;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.g.laurent.backtobike.Controllers.Activities.DisplayActivity;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.Action;
import com.g.laurent.backtobike.Utils.BikeEventHandler;
import com.g.laurent.backtobike.Utils.MapTools.RouteHandler;
import com.g.laurent.backtobike.Utils.UtilsApp;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_EVENTS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_INVITS;
import static com.g.laurent.backtobike.Controllers.Activities.BaseActivity.DISPLAY_MY_ROUTES;


public class ConfigureDisplayActivity {

    private static final String CANCELLED = "cancelled";
    private static final String CANCEL ="CANCEL";
    private static final String REJECT ="REJECT";
    private static final String ACCEPT ="ACCEPT";
    private static final String DELETE ="DELETE";
    private static final String CHANGE ="CHANGE";
    @BindView(R.id.arrow_next) ImageButton arrowNext;
    @BindView(R.id.arrow_back) ImageButton arrowBack;
    @BindView(R.id.left_button) Button buttonLeft;
    @BindView(R.id.right_button) Button buttonRight;
    private List<Route> listRoutes;
    private List<BikeEvent> listEvents;
    private List<BikeEvent> listInvitations;
    private int position;
    private int count;
    private String typeDisplay;
    private String userId;
    private DisplayActivity displayActivity;
    private Context context;

    public ConfigureDisplayActivity(View view, int position, int count, String userId, String typeDisplay, DisplayActivity displayActivity) {

        ButterKnife.bind(this, view);

        this.position = position;
        this.count = count;
        this.typeDisplay=typeDisplay;
        this.userId=userId;
        this.displayActivity=displayActivity;
        context = displayActivity.getApplicationContext();

        switch (typeDisplay) {
            case DISPLAY_MY_ROUTES:
                listRoutes = RouteHandler.getMyRoutes(context, userId);
                break;
            case DISPLAY_MY_EVENTS:
                listEvents = BikeEventHandler.getAllFutureBikeEvents(context, userId);
                break;
            case DISPLAY_MY_INVITS:
                listInvitations = BikeEventHandler.getAllInvitations(context, userId);
                break;
        }

        configureArrows();
        configureButtons(count > 0, position);
        configureAddButton();
    }

    // --------------------------------------------------------------------------------------------------------
    // ------------------------------------- CONFIGURE ARROWS -------------------------------------------------
    // --------------------------------------------------------------------------------------------------------

    private void configureArrows(){

        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                if(count>0)
                    displayActivity.setIdSelected(String.valueOf(displayActivity.getListRoutes().get(position).getId()));
                break;
            case DISPLAY_MY_EVENTS:
                if(count>0)
                    displayActivity.setIdSelected(displayActivity.getListEvents().get(position).getId());
                break;
            case DISPLAY_MY_INVITS:
                if(count>0)
                    displayActivity.setIdSelected(displayActivity.getListInvitations().get(position).getId());
                break;
        }

        configureArrows(position, count);
    }

    public void configureArrows(int position, int sizeList){

        if(UtilsApp.needLeftArrow(position,sizeList)) {
            arrowBack.setVisibility(View.VISIBLE);
            arrowBack.setOnClickListener(v -> {
                displayActivity.getPager().setCurrentItem(position-1);
                displayActivity.getAdapter().notifyDataSetChanged();
            });
        } else
            arrowBack.setVisibility(View.INVISIBLE);

        if(UtilsApp.needRightArrow(position,sizeList)) {
            arrowNext.setVisibility(View.VISIBLE);
            arrowNext.setOnClickListener(v -> {
                displayActivity.getPager().setCurrentItem(position+1);
                displayActivity.getAdapter().notifyDataSetChanged();
            });
        } else
            arrowNext.setVisibility(View.INVISIBLE);
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------- CONFIGURE BUTTONS ADD ROUTE, LEFT & RIGHT ----------------------------------
    // --------------------------------------------------------------------------------------------------------

    private void configureButtons(Boolean showButtons, int position){

        Context context = displayActivity.getApplicationContext();

        if(!showButtons){ // HIDE BUTTONS
            buttonLeft.setVisibility(View.GONE);
            buttonRight.setVisibility(View.GONE);

        } else { // SHOW BUTTONS
            switch(typeDisplay){
                case DISPLAY_MY_ROUTES:

                    // DELETE ROUTE
                    configureLeftButton(context.getResources().getString(R.string.delete));

                    // CHANGE ROUTE
                    configureRightButton(context.getResources().getString(R.string.change));
                    break;

                case DISPLAY_MY_EVENTS:

                    // CANCEL EVENT
                    Boolean isEventCancelled = listEvents.get(position).getStatus().equals(CANCELLED);

                    if(!isEventCancelled) { // If event not cancelled

                        if(listEvents.get(position).getOrganizerId().equals(userId))
                            configureLeftButton(context.getResources().getString(R.string.cancel));
                        else
                            configureLeftButton(context.getResources().getString(R.string.reject));

                    } else // if event cancelled by organizer
                        configureLeftButton(context.getResources().getString(R.string.delete));

                    // CENTER BUTTON LEFT
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    buttonLeft.setLayoutParams(lp);

                    // REMOVE BUTTON RIGHT
                    buttonRight.setVisibility(View.GONE);

                    break;

                case DISPLAY_MY_INVITS:

                    // REJECT INVITATION
                    configureLeftButton(context.getResources().getString(R.string.reject));

                    // ACCEPT INVITATION
                    configureRightButton(context.getResources().getString(R.string.accept));
                    break;
            }

            setOnClickListenersButtons();
        }
    }

    private void configureLeftButton(String typeButton){

        Drawable iconLeft[];
        buttonLeft.setVisibility(View.VISIBLE);
        buttonLeft.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.round_cancel_white_48),null,null);
        iconLeft = buttonLeft.getCompoundDrawables();
        iconLeft[1].setColorFilter(context.getResources().getColor(R.color.colorReject),PorterDuff.Mode.SRC_IN);
        buttonLeft.setTextColor(context.getResources().getColor(R.color.colorReject));

        switch (typeButton){
            case CANCEL:
                buttonLeft.setText(context.getResources().getString(R.string.cancel));
                break;
            case DELETE:
                buttonLeft.setText(context.getResources().getString(R.string.delete));
                break;
            case REJECT:
                buttonLeft.setText(context.getResources().getString(R.string.reject));
                break;
        }
    }

    private void configureRightButton(String typeButton){

        Drawable iconRight[];
        buttonRight.setVisibility(View.VISIBLE);

        switch (typeButton){
            case ACCEPT:
                buttonRight.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.baseline_check_circle_white_48),null,null);
                iconRight = buttonRight.getCompoundDrawables();
                iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.accept));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineComplete));
                break;
            case CHANGE:
                buttonRight.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(R.drawable.baseline_edit_white_48),null,null);
                iconRight = buttonRight.getCompoundDrawables();
                iconRight[1].setColorFilter(context.getResources().getColor(R.color.colorPolylineNotComplete),PorterDuff.Mode.SRC_IN);
                buttonRight.setText(context.getResources().getString(R.string.change));
                buttonRight.setTextColor(context.getResources().getColor(R.color.colorPolylineNotComplete));
                break;
        }
    }

    private void setOnClickListenersButtons(){
        switch(typeDisplay){
            case DISPLAY_MY_ROUTES:
                // DELETE ROUTE
                buttonLeft.setOnClickListener(v -> Action.showAlertDialogDeleteRoute(listRoutes.get(position), userId, displayActivity));
                // CHANGE ROUTE
                buttonRight.setOnClickListener(v -> displayActivity.launchTraceActivity(listRoutes.get(position)));
                break;

            case DISPLAY_MY_EVENTS:
                // CANCEL or REJECT EVENT
                buttonLeft.setOnClickListener(v -> {

                    Boolean isEventFromOrganizer = listEvents.get(position).getOrganizerId().equals(userId);
                    Boolean isEventCancelled = listEvents.get(position).getStatus().equals(CANCELLED);

                    if(!isEventCancelled) { // If event not cancelled
                        if(isEventFromOrganizer) // If user is the organizer
                            Action.showAlertDialogCancelBikeEvent(listEvents.get(position), userId, displayActivity);
                        else // If user is NOT the organizer
                            Action.showAlertDialogRejectEvent(listEvents.get(position),userId,displayActivity);
                    } else { // if event cancelled by organizer, delete it
                        Action.deleteBikeEvent(listEvents.get(position), userId, context);
                        String message = context.getResources().getString(R.string.delete_event);
                        displayActivity.updateListAfterDeletion(message);
                    }

                    // cancel alarm bikeEvent
                    displayActivity.cancelAlarmEvent(listEvents.get(position));
                });
                break;

            case DISPLAY_MY_INVITS:
                // REJECT INVITATION
                buttonLeft.setOnClickListener(v -> {
                    Action.showAlertDialogRejectInvitation(listInvitations.get(position),userId,displayActivity);
                    displayActivity.defineCountersAndConfigureToolbar(typeDisplay);
                });

                // ACCEPT INVITATION
                buttonRight.setOnClickListener(v -> {
                    Action.acceptInvitation(listInvitations.get(position),userId,context);
                    // set alarm for event
                    displayActivity.configureAlarmManager(listInvitations.get(position));
                    displayActivity.updateListAfterDeletion(context.getResources().getString(R.string.accept_invitation));
                    displayActivity.defineCountersAndConfigureToolbar(typeDisplay);
                });
                break;
        }
    }

    private void configureAddButton(){

        if(typeDisplay.equals(DISPLAY_MY_ROUTES) || typeDisplay.equals(DISPLAY_MY_EVENTS)){
            ImageButton buttonAdd = displayActivity.findViewById(R.id.button_add);
            buttonAdd.setOnClickListener(v -> {
                switch(typeDisplay){

                    case DISPLAY_MY_ROUTES:
                        displayActivity.launchTraceActivity(null);
                        break;

                    case DISPLAY_MY_EVENTS:
                        displayActivity.launchEventActivity();
                        break;
                }
            });
        } else if (typeDisplay.equals(DISPLAY_MY_INVITS)){
            ImageButton buttonAdd = displayActivity.findViewById(R.id.button_add);
            buttonAdd.setVisibility(View.GONE);
        }
    }
}
