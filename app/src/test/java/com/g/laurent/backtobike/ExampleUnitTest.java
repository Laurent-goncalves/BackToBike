package com.g.laurent.backtobike;

import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
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
    public void test_extract_data_from_tag(){

        int routeNumber = UtilsGoogleMaps.extractRouteFromTag("ROUTE-6");
        Assert.assertEquals(1,routeNumber);

        routeNumber = UtilsGoogleMaps.extractRouteFromTag("ROUTEALT-10");
        Assert.assertEquals(2,routeNumber);

        int index = UtilsGoogleMaps.extractIndexFromTag("ROUTE-10");
        Assert.assertEquals(10,index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTE-5");
        Assert.assertEquals(5,index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTEALT-1");
        Assert.assertEquals(1,index);

        index = UtilsGoogleMaps.extractIndexFromTag("ROUTEALT-22");
        Assert.assertEquals(22,index);

    }



    /*@Test
    public void test_equality_latlng(){

        LatLng position1 = new LatLng(48.8625196080201,2.2872030796928584);
        LatLng position2 = new LatLng(48.8625196080201,2.2872030796928584);

        Assert.assertTrue(UtilsGoogleMaps.arePositionsEquals(position1,position2));

        position2 = new LatLng(48.86591097479993,2.2745751764164197);

        Assert.assertFalse(UtilsGoogleMaps.arePositionsEquals(position1,position2));
    }*/

}