package com.olegsagenadatrytwo.umbrellawalmartchallenge.model.custom;


import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyForecast;
import java.util.List;

/**
 * Created by omcna on 9/28/2017.
 */

public class DayData {
    private List<HourlyForecast> hourlyList;

    public DayData(List<HourlyForecast> hourlyList) {
        this.hourlyList = hourlyList;
    }

    public List<HourlyForecast> getHourlyList() {
        return hourlyList;
    }

    public void setHourlyList(List<HourlyForecast> hourlyList) {
        this.hourlyList = hourlyList;
    }
}
