package com.g.laurent.backtobike;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.UtilsBikeEvent;
import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.g.laurent.backtobike.Utils.UtilsTime;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test_route_time() {
        assertEquals(UtilsGoogleMaps.getTimeRoute(30000d), "1:52");
    }

    @Test
    public void test_extract_data_from_tag() {

        int routeNumber = UtilsGoogleMaps.extractRouteFromTag("ROUTE-6");
        Assert.assertEquals(1, routeNumber);

        routeNumber = UtilsGoogleMaps.extractRouteFromTag("ROUTEALT-10");
        Assert.assertEquals(2, routeNumber);

        int index = UtilsGoogleMaps.extractIndexFromTag("ROUTE-10");
        Assert.assertEquals(10, index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTE-5");
        Assert.assertEquals(5, index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTEALT-1");
        Assert.assertEquals(1, index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTEALT-22");
        Assert.assertEquals(22, index);
    }

    @Test
    public void test_days_number_between_two_dates() {

        String date1 = "01/01/2001";
        String date2 = "02/01/2001";

        Assert.assertEquals(1, UtilsTime.getNumberOfDaysBetweenTwoDate(date1, date2));

        date2 = "01/01/2001";

        Assert.assertEquals(0, UtilsTime.getNumberOfDaysBetweenTwoDate(date1, date2));

        date2 = "31/12/2000";

        Assert.assertEquals(-1, UtilsTime.getNumberOfDaysBetweenTwoDate(date1, date2));
    }

    @Test
    public void test_sort_bikeevent_chronological_order() {

        BikeEvent event1 = new BikeEvent();
        event1.setId("id1");
        event1.setDate("12/02/2018");
        event1.setTime("15:00");

        BikeEvent event2 = new BikeEvent();
        event2.setId("id2");
        event2.setDate("12/02/2018");
        event2.setTime("11:00");

        BikeEvent event3 = new BikeEvent();
        event3.setId("id3");
        event3.setDate("11/02/2018");
        event3.setTime("15:00");

        BikeEvent event4 = new BikeEvent();
        event4.setId("id4");
        event4.setDate("13/02/2018");
        event4.setTime("09:00");

        List<BikeEvent> listBikeEvent = new ArrayList<>();
        listBikeEvent.add(event1);
        listBikeEvent.add(event2);
        listBikeEvent.add(event3);
        listBikeEvent.add(event4);

        List<BikeEvent> listBikeEventSorted = UtilsTime.getListBikeEventByChronologicalOrder(listBikeEvent);

        Assert.assertEquals("id3", listBikeEventSorted.get(0).getId());
        Assert.assertEquals("id2", listBikeEventSorted.get(1).getId());
        Assert.assertEquals("id1", listBikeEventSorted.get(2).getId());
        Assert.assertEquals("id4", listBikeEventSorted.get(3).getId());

    }

}