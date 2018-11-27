package com.g.laurent.backtobike;

import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.Route;
import com.g.laurent.backtobike.Models.RouteSegment;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.g.laurent.backtobike.Utils.MapTools.UtilsGoogleMaps;
import com.g.laurent.backtobike.Utils.UtilsTime;
import junit.framework.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.EVENT_ROUTE_TYPE;
import static com.g.laurent.backtobike.Utils.MapTools.RouteHandler.MY_ROUTE_TYPE;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test_route_time() {
        assertEquals(UtilsGoogleMaps.getTimeRoute(30000d), "01:52");
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

    @Test
    public void test_comparison_route() {

        Route route1 = new Route(0,"Trip to Las Vegas",null,MY_ROUTE_TYPE);
        Route route2 = new Route(0,"Trip to Las Vegas",null,EVENT_ROUTE_TYPE);
        Route route3 = new Route(0,"Trip to Las Vegas 2",null,MY_ROUTE_TYPE);
        Route route4 = new Route(0,"Trip to Las Vegas",null,MY_ROUTE_TYPE);

        List<RouteSegment> listRouteSegments1 = new ArrayList<>();

        RouteSegment ROUTE_SEG1_DEMO = new RouteSegment(0,1,48.819446, 2.344624,999);
        RouteSegment ROUTE_SEG2_DEMO = new RouteSegment(0,2,48.885412, 2.336589,999);
        RouteSegment ROUTE_SEG3_DEMO = new RouteSegment(0,3,48.874563, 2.312778,999);
        RouteSegment ROUTE_SEG4_DEMO = new RouteSegment(0,4,48.895220, 2.321511,999);
        RouteSegment ROUTE_SEG5_DEMO = new RouteSegment(0,5,48.929888, 2.321511,999);
        RouteSegment ROUTE_SEG6_DEMO = new RouteSegment(0,6,48.820336, 2.321511,999);

        listRouteSegments1.add(ROUTE_SEG1_DEMO);
        listRouteSegments1.add(ROUTE_SEG2_DEMO);
        listRouteSegments1.add(ROUTE_SEG3_DEMO);
        listRouteSegments1.add(ROUTE_SEG4_DEMO);
        listRouteSegments1.add(ROUTE_SEG5_DEMO);
        listRouteSegments1.add(ROUTE_SEG6_DEMO);

        List<RouteSegment> listRouteSegments2 = new ArrayList<>(listRouteSegments1);
        List<RouteSegment> listRouteSegments3 = new ArrayList<>(listRouteSegments1);
        List<RouteSegment> listRouteSegments4 = new ArrayList<>(listRouteSegments1);

        route1.setListRouteSegment(listRouteSegments1);
        route2.setListRouteSegment(listRouteSegments2);
        route3.setListRouteSegment(listRouteSegments3);
        route4.setListRouteSegment(listRouteSegments4);

        Assert.assertTrue(UtilsApp.areRoutesEquals(route1,route4));
        Assert.assertTrue(UtilsApp.areRoutesEquals(route1,route2));
        Assert.assertFalse(UtilsApp.areRoutesEquals(route1,route3));

        listRouteSegments4.remove(0);
        listRouteSegments4.remove(1);

        Assert.assertFalse(UtilsApp.areRoutesEquals(route1,route4));

        listRouteSegments4 = new ArrayList<>(listRouteSegments1);
        listRouteSegments4.get(0).setLat(48.819500);

        Assert.assertFalse(UtilsApp.areRoutesEquals(route1,route4));
    }

    @Test
    public void test_getCalendarAlarms(){

        String date = "15/10/2018";
        Calendar calendar = UtilsTime.getCalendarAlarm2daysbefore(date);
        Assert.assertEquals(13, calendar.get(Calendar.DAY_OF_MONTH));

        date = "02/10/2018";
        calendar = UtilsTime.getCalendarAlarm2daysbefore(date);
        Assert.assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));

        String time = "14:00";
        calendar = UtilsTime.getCalendarAlarmdayevent(date, time);
        Assert.assertEquals(10, calendar.get(Calendar.HOUR_OF_DAY));

        date = "01/10/2018";
        time = "02:00";
        calendar = UtilsTime.getCalendarAlarmdayevent(date, time);
        Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void test_id_pending_intent(){

        String date = "31/10/2018";
        String time = "21:00";

        int id2days = UtilsTime.generatePendingIntentID_2days(date,time);
        int idDayEvent = UtilsTime.generatePendingIntentID_4hours(date,time);

        Assert.assertEquals(210031110, id2days);
        Assert.assertEquals(210031112, idDayEvent);

        time = "7:00";

        id2days = UtilsTime.generatePendingIntentID_2days(date,time);
        idDayEvent = UtilsTime.generatePendingIntentID_4hours(date,time);

        Assert.assertEquals(70031110, id2days);
        Assert.assertEquals(70031112, idDayEvent);
    }

    @Test
    public void test_getTimeInDays(){
        String time = UtilsGoogleMaps.getTimeInDays(100000000);
        Assert.assertEquals("2 days", time);
        time = UtilsGoogleMaps.getTimeInDays(216000000);
        Assert.assertEquals("3 days", time);
    }

}