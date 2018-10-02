package com.g.laurent.backtobike;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.g.laurent.backtobike.Controllers.TraceActivity;
import com.g.laurent.backtobike.Utils.UtilsGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<TraceActivity> mActivityRule = new ActivityTestRule<>(TraceActivity.class);

    @Test
    public void test_route_distance(){

        LatLng point1 = new LatLng(48.858109, 2.339278);
        LatLng point2 = new LatLng(48.800981, 2.520347);
        LatLng point3 = new LatLng(48.800981, 2.520347);
        LatLng point4 = new LatLng(48.615268, 2.473662);
        LatLng point5 = new LatLng(48.615268, 2.473662);
        LatLng point6 = new LatLng(48.587223, 2.445282);

        final PolylineOptions rectOptions = new PolylineOptions()
                .add(point1)
                .add(point2)
                .add(point3)
                .add(point4)
                .add(point5)
                .add(point6);

        final GoogleMap map = mActivityRule.getActivity().getMap();

        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Polyline route = map.addPolyline(rectOptions);

                assertEquals((float) UtilsGoogleMaps.getMileageRoute(route),(float) 39426d, (float) 1);
            }
        });
    }

    @Test
    public void test_find_nearest_point_polyline(){

        LatLng point1 = new LatLng(48.858109, 2.339278);
        LatLng point2 = new LatLng(48.800981, 2.520347);
        LatLng point3 = new LatLng(48.615268, 2.473662);
        LatLng point4 = new LatLng(48.587223, 2.445282);

        final PolylineOptions rectOptions = new PolylineOptions()
                .add(point1)
                .add(point2)
                .add(point3)
                .add(point4);

        final GoogleMap map = mActivityRule.getActivity().getMap();

        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Polyline route = map.addPolyline(rectOptions);

                assertEquals(UtilsGoogleMaps.findIndexNearestPolyLinePoint(new LatLng(48.858109, 2.339278), route),0);

                assertEquals(UtilsGoogleMaps.findIndexNearestPolyLinePoint(new LatLng(48.615268, 2.473662), route),2);
            }
        });
    }

    private void waiting_time(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*



// Obtain MotionEvent object
long downTime = SystemClock.uptimeMillis();
long eventTime = SystemClock.uptimeMillis() + 100;
float x = 0.0f;
float y = 0.0f;
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
int metaState = 0;
MotionEvent motionEvent = MotionEvent.obtain(
    downTime,
    eventTime,
    MotionEvent.ACTION_UP,
    x,
    y,
    metaState
);

// Dispatch touch event to view
view.dispatchTouchEvent(motionEvent);



 */