package com.g.laurent.backtobike;

import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import junit.framework.Assert;
import org.junit.Test;
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

}