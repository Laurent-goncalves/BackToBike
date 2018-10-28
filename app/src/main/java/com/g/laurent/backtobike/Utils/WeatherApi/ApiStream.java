package com.g.laurent.backtobike.Utils.WeatherApi;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ApiStream {

    public static Observable<Forecast> streamFetchgetForecast(String api_key, String lang, String lat, String lng){
        ApiService forecastRequest = ApiService.retrofit.create(ApiService.class);

        return forecastRequest.getWeatherForecast(api_key, lang, lat, lng, "14")
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread());
                .observeOn(Schedulers.newThread());  // TEST
    }
}
