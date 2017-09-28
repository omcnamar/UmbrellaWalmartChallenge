package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfo.WeatherInfo;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyWeatherInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by omcna on 9/28/2017.
 */

class MainActivityPresenter implements MainActivityContract.Presenter {

    private static final String WEATHER_URL = "api.wunderground.com";
    private static final String KEY = "d3e9f2825d24ab13";
    private MainActivityContract.View view;
    private Context context;
    private WeatherInfo weatherInfo;
    private HourlyWeatherInfo hourlyWeatherInfo;

    @Override
    public void attachView(MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context){
        this.context = context;
    }

    /**method that downloads data from weather channel*/
    @Override
    public void downloadWeatherData(String zipCode, final String fOrC){

        //make request to get the data
        final OkHttpClient okHttpClient;
        final Request request;
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(WEATHER_URL)
                .addPathSegment("api")
                .addPathSegment(KEY)
                .addPathSegment("conditions")
                .addPathSegment("q")
                .addPathSegment(zipCode + ".json")
                .build();

        okHttpClient = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(context, "Failed to make connection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                weatherInfo = gson.fromJson(response.body().string(), WeatherInfo.class);
                view.weatherDownloadedUpdateUI(weatherInfo, fOrC);
            }
        });

    }

    //method that downloads data from weather channel
    @Override
    public void downloadWeatherDataHourly(String zipCode, final String fOrC){

        //make request to get the l
        final OkHttpClient okHttpClient;
        final Request request;
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(WEATHER_URL)
                .addPathSegment("api")
                .addPathSegment(KEY)
                .addPathSegment("hourly10day")
                .addPathSegment("q")
                .addPathSegment(zipCode + ".json")
                .build();

        okHttpClient = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(context, "Failed to make connection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                //Log.d(TAG, "onResponse: " + response.body().string());
                hourlyWeatherInfo = gson.fromJson(response.body().string(), HourlyWeatherInfo.class);
                view.hourlyWeatherDownloadedUpdateUI(hourlyWeatherInfo, fOrC);
            }
        });

    }
}
