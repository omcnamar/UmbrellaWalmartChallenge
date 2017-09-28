
package com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Features {

    @SerializedName("hourly10day")
    @Expose
    private Integer hourly10day;

    public Integer getHourly10day() {
        return hourly10day;
    }

    public void setHourly10day(Integer hourly10day) {
        this.hourly10day = hourly10day;
    }

}
