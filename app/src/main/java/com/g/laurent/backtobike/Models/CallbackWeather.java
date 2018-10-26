package com.g.laurent.backtobike.Models;

import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;

import java.util.List;

public interface CallbackWeather {

    void onCompleted(List<WeatherForecast> listWeatherForecast);

    void onFailure(String error);
}