package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.BasePresenter;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.BaseView;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfo.WeatherInfo;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyWeatherInfo;

/**
 * Created by omcna on 9/28/2017.
 */

interface MainActivityContract {

    interface View extends BaseView {
        void weatherDownloadedUpdateUI(WeatherInfo weatherInfo);
        void hourlyWeatherDownloadedUpdateUI(HourlyWeatherInfo hourlyWeatherInfo);
    }

    interface Presenter extends BasePresenter<View> {
        void downloadWeatherData(String zipCode);
        void downloadWeatherDataHourly(String zipCode);
    }
}
