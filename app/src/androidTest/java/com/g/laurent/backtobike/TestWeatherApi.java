package com.g.laurent.backtobike;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import com.g.laurent.backtobike.Models.CallbackWeather;
import com.g.laurent.backtobike.Utils.WeatherApi.GetForecast;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;
import com.google.android.gms.maps.model.LatLng;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static android.support.test.InstrumentationRegistry.getInstrumentation;



@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestWeatherApi {

    private Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test
    public void test_compare_bike_events(){

        GetForecast getForecast = new GetForecast();
        LatLng location = new LatLng(48.857327,2.336151);
        String apikey = getInstrumentation().getTargetContext().getResources().getString(R.string.weather_api_key);
        getForecast.getListWeathersByDay(apikey, location, "en", getInstrumentation().getTargetContext(), new CallbackWeather() {
            @Override
            public void onCompleted(List<WeatherForecast> listWeatherForecast) {
                Assert.assertEquals(16, listWeatherForecast.size());
            }

            @Override
            public void onFailure(String error) { }
        });

        waiting_time(10000);
    }

    private void waiting_time(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
