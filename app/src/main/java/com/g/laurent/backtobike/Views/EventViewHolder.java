package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsTime;

import static com.g.laurent.backtobike.Utils.UtilsTime.getSeasonNumber;


public class EventViewHolder extends RecyclerView.ViewHolder {

    private static final String ACCEPTED = "accepted";
    private static final String CANCELLED = "cancelled";

    public EventViewHolder(View itemView) {
        super(itemView);
    }

    public void configureViews(BikeEvent bikeEvent, String userId, Context context) {

        TextView dateView = itemView.findViewById(R.id.date_view);
        ImageView iconOrganizer = itemView.findViewById(R.id.organizer_icon);
        TextView routeView = itemView.findViewById(R.id.route_name_view);
        ImageView iconFriends = itemView.findViewById(R.id.friend_icon);
        TextView friendCounter = itemView.findViewById(R.id.number_friends_view);
        TextView cancelText = itemView.findViewById(R.id.cancelled_text_rotated);

        // set background
        int seasonNum = getSeasonNumber();
        itemView.setBackgroundColor(SeasonPicture.seasonBackground[seasonNum]);

        // Configure dateView
        setDateView(dateView, context, bikeEvent);

        // Configure organizer icon
        if(bikeEvent.getOrganizerId().equals(userId))
            iconOrganizer.setVisibility(View.VISIBLE);

        // Configure route
        routeView.setText(bikeEvent.getRoute().getName());

        // Configure friend counter
        setCounterFriend(friendCounter, iconFriends, bikeEvent);

        // Set cancel text if applicable
        if(bikeEvent.getStatus().equals(CANCELLED))
            cancelText.setVisibility(View.VISIBLE);
    }

    private void setDateView(TextView dateView, Context context, BikeEvent bikeEvent){

        String today = UtilsTime.getTodayDate();
        int timeToEvent = UtilsTime.getNumberOfDaysBetweenTwoDate(today, bikeEvent.getDate());

        if( timeToEvent == 0){ // if event is today
            dateView.setText(context.getResources().getString(R.string.today));
        } else if (timeToEvent == 1){ // if event is tomorrow
            dateView.setText(context.getResources().getString(R.string.tomorrow));
        } else if (timeToEvent > 6){
            dateView.setText(bikeEvent.getDate());
        } else {
            String text = UtilsTime.getDateEvent(context, bikeEvent.getDate());
            dateView.setText(text);
        }
    }

    private void setCounterFriend(TextView friendCounter, ImageView iconFriend, BikeEvent bikeEvent){
        if(bikeEvent.getListEventFriends()!=null){
            if(bikeEvent.getListEventFriends().size()>0){

                int count = 0;
                for(EventFriends friend : bikeEvent.getListEventFriends()){
                    if(friend.getAccepted().equals(ACCEPTED))
                        count++;
                }

                String text;
                if(count > 9)
                    text = "(+9)";
                else
                    text = "(" + count + ")";

                friendCounter.setText(text);

            } else {
                friendCounter.setVisibility(View.GONE);
                iconFriend.setVisibility(View.GONE);
            }
        } else {
            friendCounter.setVisibility(View.GONE);
            iconFriend.setVisibility(View.GONE);
        }
    }

    public interface DaysName {
        Integer[] daysName = {
                R.string.sunday,
                R.string.monday,
                R.string.tuesday,
                R.string.wednesday,
                R.string.thursday,
                R.string.friday,
                R.string.saturday
        };
    }

    public interface SeasonPicture {

        Integer[] seasonDrawables = {
                R.drawable.winter,
                R.drawable.spring,
                R.drawable.summer,
                R.drawable.autumn
        };

        Integer[] seasonBackground = {
                R.color.colorWinter,
                R.color.colorSpring,
                R.color.colorSummer,
                R.color.colorAutumn
        };

    }
}
