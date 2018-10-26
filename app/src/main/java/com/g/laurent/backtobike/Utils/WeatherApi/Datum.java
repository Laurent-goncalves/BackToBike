
package com.g.laurent.backtobike.Utils.WeatherApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("moonrise_ts")
    @Expose
    private transient Integer moonriseTs;
    @SerializedName("wind_cdir")
    @Expose
    private transient String windCdir;
    @SerializedName("rh")
    @Expose
    private transient Integer rh;
    @SerializedName("pres")
    @Expose
    private transient Double pres;
    @SerializedName("sunset_ts")
    @Expose
    private transient Integer sunsetTs;
    @SerializedName("ozone")
    @Expose
    private transient Double ozone;
    @SerializedName("moon_phase")
    @Expose
    private transient Double moonPhase;
    @SerializedName("wind_gust_spd")
    @Expose
    private transient Double windGustSpd;
    @SerializedName("snow_depth")
    @Expose
    private transient Integer snowDepth;
    @SerializedName("clouds")
    @Expose
    private transient Integer clouds;
    @SerializedName("ts")
    @Expose
    private transient Integer ts;
    @SerializedName("sunrise_ts")
    @Expose
    private transient Integer sunriseTs;
    @SerializedName("app_min_temp")
    @Expose
    private transient Double appMinTemp;
    @SerializedName("wind_spd")
    @Expose
    private transient Double windSpd;
    @SerializedName("pop")
    @Expose
    private transient Integer pop;
    @SerializedName("wind_cdir_full")
    @Expose
    private transient String windCdirFull;
    @SerializedName("slp")
    @Expose
    private transient Double slp;
    @SerializedName("app_max_temp")
    @Expose
    private transient Double appMaxTemp;
    @SerializedName("vis")
    @Expose
    private transient Double vis;
    @SerializedName("dewpt")
    @Expose
    private transient Integer dewpt;
    @SerializedName("snow")
    @Expose
    private transient Integer snow;
    @SerializedName("uv")
    @Expose
    private transient Double uv;
    @SerializedName("valid_date")
    @Expose
    private transient String validDate;
    @SerializedName("wind_dir")
    @Expose
    private transient Integer windDir;
    @SerializedName("max_dhi")
    @Expose
    private transient Object maxDhi;
    @SerializedName("clouds_hi")
    @Expose
    private transient  Integer cloudsHi;
    @SerializedName("precip")
    @Expose
    private transient Double precip;
    @SerializedName("weather")
    @Expose
    private Weather weather;
    @SerializedName("max_temp")
    @Expose
    private Double maxTemp;
    @SerializedName("moonset_ts")
    @Expose
    private transient Integer moonsetTs;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("temp")
    @Expose
    private transient Integer temp;
    @SerializedName("min_temp")
    @Expose
    private Double minTemp;
    @SerializedName("clouds_mid")
    @Expose
    private transient Integer cloudsMid;
    @SerializedName("clouds_low")
    @Expose
    private transient Integer cloudsLow;

    public Integer getMoonriseTs() {
        return moonriseTs;
    }

    public void setMoonriseTs(Integer moonriseTs) {
        this.moonriseTs = moonriseTs;
    }

    public String getWindCdir() {
        return windCdir;
    }

    public void setWindCdir(String windCdir) {
        this.windCdir = windCdir;
    }

    public Integer getRh() {
        return rh;
    }

    public void setRh(Integer rh) {
        this.rh = rh;
    }

    public Double getPres() {
        return pres;
    }

    public void setPres(Double pres) {
        this.pres = pres;
    }

    public Integer getSunsetTs() {
        return sunsetTs;
    }

    public void setSunsetTs(Integer sunsetTs) {
        this.sunsetTs = sunsetTs;
    }

    public Double getOzone() {
        return ozone;
    }

    public void setOzone(Double ozone) {
        this.ozone = ozone;
    }

    public Double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(Double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public Double getWindGustSpd() {
        return windGustSpd;
    }

    public void setWindGustSpd(Double windGustSpd) {
        this.windGustSpd = windGustSpd;
    }

    public Integer getSnowDepth() {
        return snowDepth;
    }

    public void setSnowDepth(Integer snowDepth) {
        this.snowDepth = snowDepth;
    }

    public Integer getClouds() {
        return clouds;
    }

    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }

    public Integer getSunriseTs() {
        return sunriseTs;
    }

    public void setSunriseTs(Integer sunriseTs) {
        this.sunriseTs = sunriseTs;
    }

    public Double getAppMinTemp() {
        return appMinTemp;
    }

    public void setAppMinTemp(Double appMinTemp) {
        this.appMinTemp = appMinTemp;
    }

    public Double getWindSpd() {
        return windSpd;
    }

    public void setWindSpd(Double windSpd) {
        this.windSpd = windSpd;
    }

    public Integer getPop() {
        return pop;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public String getWindCdirFull() {
        return windCdirFull;
    }

    public void setWindCdirFull(String windCdirFull) {
        this.windCdirFull = windCdirFull;
    }

    public Double getSlp() {
        return slp;
    }

    public void setSlp(Double slp) {
        this.slp = slp;
    }

    public Double getAppMaxTemp() {
        return appMaxTemp;
    }

    public void setAppMaxTemp(Double appMaxTemp) {
        this.appMaxTemp = appMaxTemp;
    }

    public Double getVis() {
        return vis;
    }

    public void setVis(Double vis) {
        this.vis = vis;
    }

    public Integer getDewpt() {
        return dewpt;
    }

    public void setDewpt(Integer dewpt) {
        this.dewpt = dewpt;
    }

    public Integer getSnow() {
        return snow;
    }

    public void setSnow(Integer snow) {
        this.snow = snow;
    }

    public Double getUv() {
        return uv;
    }

    public void setUv(Double uv) {
        this.uv = uv;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public Integer getWindDir() {
        return windDir;
    }

    public void setWindDir(Integer windDir) {
        this.windDir = windDir;
    }

    public Object getMaxDhi() {
        return maxDhi;
    }

    public void setMaxDhi(Object maxDhi) {
        this.maxDhi = maxDhi;
    }

    public Integer getCloudsHi() {
        return cloudsHi;
    }

    public void setCloudsHi(Integer cloudsHi) {
        this.cloudsHi = cloudsHi;
    }

    public Double getPrecip() {
        return precip;
    }

    public void setPrecip(Double precip) {
        this.precip = precip;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Integer getMoonsetTs() {
        return moonsetTs;
    }

    public void setMoonsetTs(Integer moonsetTs) {
        this.moonsetTs = moonsetTs;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getTemp() {
        return temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public Integer getCloudsMid() {
        return cloudsMid;
    }

    public void setCloudsMid(Integer cloudsMid) {
        this.cloudsMid = cloudsMid;
    }

    public Integer getCloudsLow() {
        return cloudsLow;
    }

    public void setCloudsLow(Integer cloudsLow) {
        this.cloudsLow = cloudsLow;
    }

}
