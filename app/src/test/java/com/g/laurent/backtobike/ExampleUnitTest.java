package com.g.laurent.backtobike;

import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
    public void test_route_time(){
        assertEquals(UtilsGoogleMaps.getTimeRoute(30000d),"1:52");
    }

    @Test
    public void test_route_distance(){

        List<Polyline> route = new ArrayList<>();

        LatLng start1 = new LatLng(48.858109, 2.339278);
        LatLng end1 = new LatLng(48.800981, 2.520347);
        PolylineOptions rectOptions1 = new PolylineOptions().add(start1).add(end1);

        LatLng start2 = new LatLng(48.800981, 2.520347);
        LatLng end2 = new LatLng(48.615268, 2.473662);
        PolylineOptions rectOptions2 = new PolylineOptions().add(start2).add(end2);

        LatLng start3 = new LatLng(48.615268, 2.473662);
        LatLng end3 = new LatLng(48.587223, 2.445282);
        PolylineOptions rectOptions3 = new PolylineOptions().add(start3).add(end3);

        GoogleMap map;

        Polyline polyline1 = map.addPolyline(rectOptions1);
        Polyline polyline2 = map.addPolyline(rectOptions2);
        Polyline polyline3 = map.addPolyline(rectOptions3);

        route.add(polyline1);
        route.add(polyline2);
        route.add(polyline3);

        assertEquals((float) UtilsGoogleMaps.getMileageRoute(route),(float) 30000d, (float) 1);
    }

}