package com.g.laurent.backtobike.Utils.WeatherApi;

public class WeatherForecast  {

    private String codeWeather;
    private String temperatureInf;
    private String temperatureSup;
    private String description;
    private String date;

    public WeatherForecast(String codeWeather, String temperatureInf, String temperatureSup, String description, String date) {
        this.codeWeather = codeWeather;
        this.temperatureInf = temperatureInf;
        this.temperatureSup = temperatureSup;
        this.description=description;
        this.date = date;
    }

    public String getCodeWeather() {
        return codeWeather;
    }

    public void setCodeWeather(String codeWeather) {
        this.codeWeather = codeWeather;
    }

    public String getTemperatureInf() {
        return temperatureInf;
    }

    public void setTemperatureInf(String temperatureInf) {
        this.temperatureInf = temperatureInf;
    }

    public String getTemperatureSup() {
        return temperatureSup;
    }

    public void setTemperatureSup(String temperatureSup) {
        this.temperatureSup = temperatureSup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
