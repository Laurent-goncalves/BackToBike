package com.g.laurent.backtobike.Utils.WeatherApi;

import android.content.Context;
import android.widget.Toast;

import com.g.laurent.backtobike.Models.CallbackWeather;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsApp;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class GetForecast implements Disposable {

    private Disposable disposable;
    private List<WeatherForecast> listWeathers;

    public void getListWeathersByDay(String api_key, LatLng latLng, String lang, Context context, CallbackWeather callbackWeather) {

        dispose();
        listWeathers = new ArrayList<>();
        String lat = String.valueOf(latLng.latitude);
        String lng = String.valueOf(latLng.longitude);

        this.disposable = ApiStream.streamFetchgetForecast(api_key,lang,lat,lng).subscribeWith(new DisposableObserver<Forecast>() {

            @Override
            public void onNext(Forecast forecast) {
                buildListWeatherForecast(forecast);
            }

            @Override
            public void onError(Throwable e) {
                String text = context.getResources().getString(R.string.error_weather_api) + "\n" + e.toString();
                callbackWeather.onFailure(text);
            }

            @Override
            public void onComplete() {
                callbackWeather.onCompleted(listWeathers);
                dispose();
            }
        });
    }

    private void buildListWeatherForecast(Forecast forecast) {

        if(forecast.getData()!=null) {
            if (forecast.getData().size() > 0) {
                for (Datum datum : forecast.getData()) {

                    String code;
                    String description = null;

                    if (datum.getWeather() != null) {
                        code = String.valueOf(datum.getWeather().getCode());
                        description = datum.getWeather().getDescription();
                    } else
                        code = "800";

                    listWeathers.add(new WeatherForecast(code,
                            UtilsApp.getRoundValueTemperature(datum.getMinTemp()),
                            UtilsApp.getRoundValueTemperature(datum.getMaxTemp()),
                            description,
                            datum.getDatetime()));
                }
            }
        }
    }

    public List<WeatherForecast> getListWeathers() {
        return listWeathers;
    }

    @Override
    public void dispose() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
