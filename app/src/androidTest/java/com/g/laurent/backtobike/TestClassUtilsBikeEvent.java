package com.g.laurent.backtobike;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Difference;
import com.g.laurent.backtobike.Models.EventFriends;
import com.g.laurent.backtobike.Utils.UtilsCounters;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;
import static android.support.test.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestClassUtilsBikeEvent {

    @Test
    public void test_compare_bike_events(){

        // OLD LIST

        BikeEvent bikeEvent1 = new BikeEvent();
        bikeEvent1.setId("bikeEvent1");
        bikeEvent1.setStatus("accepted");
        bikeEvent1.setListEventFriends(getOldListEventFriendsBikeEvent1());

        BikeEvent bikeEvent2 = new BikeEvent();
        bikeEvent2.setId("bikeEvent2");
        bikeEvent2.setStatus("accepted");
        bikeEvent2.setListEventFriends(getOldListEventFriendsBikeEvent2());

        List<BikeEvent> listBikeEventOld = new ArrayList<>();
        listBikeEventOld.add(bikeEvent1);
        listBikeEventOld.add(bikeEvent2);

        // NEW LIST

        BikeEvent bikeEvent3 = new BikeEvent();
        bikeEvent3.setId("bikeEvent1");
        bikeEvent3.setStatus("accepted");
        bikeEvent3.setListEventFriends(getNewListEventFriendsBikeEvent1());

        BikeEvent bikeEvent4 = new BikeEvent();
        bikeEvent4.setId("bikeEvent2");
        bikeEvent4.setStatus("cancelled");
        bikeEvent4.setListEventFriends(getNewListEventFriendsBikeEvent2());

        List<BikeEvent> listBikeEventNew = new ArrayList<>();
        listBikeEventNew.add(bikeEvent3);
        listBikeEventNew.add(bikeEvent4);

        List<Difference> differenceList = UtilsCounters.getListDifferencesBetweenListEvents(listBikeEventOld,listBikeEventNew, getInstrumentation().getTargetContext());

        Assert.assertEquals("2 friends have accepted the trip!", differenceList.get(0).getDifference());
        Assert.assertEquals("1 friend has refused the trip!", differenceList.get(1).getDifference());
        Assert.assertEquals("The trip has been cancelled.", differenceList.get(2).getDifference());
        Assert.assertEquals("1 friend has refused the trip!", differenceList.get(3).getDifference());
        Assert.assertEquals(4, differenceList.size());
    }

    private List<EventFriends> getOldListEventFriendsBikeEvent1(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"bikeEvent1","id1","id1","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(1,"bikeEvent1","id2","id2","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_3 = new EventFriends(2,"bikeEvent1","id3","id3","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_4 = new EventFriends(3,"bikeEvent1","id4","id4","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_5 = new EventFriends(4,"bikeEvent1","id5","id5","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        listEventFriends.add(EVENT_FRIENDS_DEMO_3);
        listEventFriends.add(EVENT_FRIENDS_DEMO_4);
        listEventFriends.add(EVENT_FRIENDS_DEMO_5);

        return listEventFriends;
    }

    private List<EventFriends> getOldListEventFriendsBikeEvent2(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"bikeEvent2","id6","id6","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(1,"bikeEvent2","id7","id7","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_3 = new EventFriends(2,"bikeEvent2","id8","id8","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        listEventFriends.add(EVENT_FRIENDS_DEMO_3);

        return listEventFriends;
    }

    private List<EventFriends> getNewListEventFriendsBikeEvent1(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"bikeEvent1","id1","id1","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(1,"bikeEvent1","id2","id2","accepted");
        EventFriends EVENT_FRIENDS_DEMO_3 = new EventFriends(2,"bikeEvent1","id3","id3","accepted");
        EventFriends EVENT_FRIENDS_DEMO_4 = new EventFriends(3,"bikeEvent1","id4","id4","rejected");
        EventFriends EVENT_FRIENDS_DEMO_5 = new EventFriends(4,"bikeEvent1","id5","id5","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        listEventFriends.add(EVENT_FRIENDS_DEMO_3);
        listEventFriends.add(EVENT_FRIENDS_DEMO_4);
        listEventFriends.add(EVENT_FRIENDS_DEMO_5);

        return listEventFriends;
    }

    private List<EventFriends> getNewListEventFriendsBikeEvent2(){

        List<EventFriends> listEventFriends = new ArrayList<>();
        EventFriends EVENT_FRIENDS_DEMO_1 = new EventFriends(0,"bikeEvent2","id6","id6","ongoing");
        EventFriends EVENT_FRIENDS_DEMO_2 = new EventFriends(1,"bikeEvent2","id7","id7","rejected");
        EventFriends EVENT_FRIENDS_DEMO_3 = new EventFriends(2,"bikeEvent2","id8","id8","ongoing");

        listEventFriends.add(EVENT_FRIENDS_DEMO_1);
        listEventFriends.add(EVENT_FRIENDS_DEMO_2);
        listEventFriends.add(EVENT_FRIENDS_DEMO_3);

        return listEventFriends;
    }
}
