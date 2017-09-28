package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.flurry.android.FlurryAgent;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.R;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.custom.DayData;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfo.WeatherInfo;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyForecast;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyWeatherInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    //constants
    private static final String SETTINGS_PREF_FILE = "settings";
    private static final String CURRENT_SETTING = "F/C";
    private static final String ZIP_CODE = "zip_code";

    //presenter
    private MainActivityPresenter presenter;

    //views for toolbar
    private TextView tvTemperature;
    private TextView tvCondition;
    private Toolbar myToolbar;
    private LinearLayout toolBarHeaderViewLinearLayout;

    //recycler view and needed objects
    private RecyclerView rvDays;
    private DaysAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);

        //action bar set up
        LinearLayout includeView = (LinearLayout) findViewById(R.id.toolbar_header_view);
        tvTemperature = (TextView) includeView.findViewById(R.id.tvTemperatureTop);
        tvCondition = (TextView) includeView.findViewById(R.id.tvConditionTop);
        toolBarHeaderViewLinearLayout = (LinearLayout) includeView.findViewById(R.id.toolbar_header_view);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //recycler view set up
        rvDays = (RecyclerView) findViewById(R.id.rvDays);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvDays.setLayoutManager(layoutManager);
        rvDays.setItemAnimator(itemAnimator);

        //presenter set up
        presenter = new MainActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);

        //get the settings from sharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        String fahrenheitOrCelsius = sharedPreferences.getString(CURRENT_SETTING, "default");

        //if there was no current settings for temperature make fahrenheit the default
        if (fahrenheitOrCelsius.equals("default")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_SETTING, "F");
            editor.apply();
            String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
            //if there was no zip code show the dialog for zip code
            if (zipCode.equals("default")) {
                showZipCodeDialog("");
            } else { // if there was a zip code download the weather data
                presenter.downloadWeatherData(zipCode, "F");
                presenter.downloadWeatherDataHourly(zipCode, "F");
            }

        } else { //if settings were already configured use them to download weather data
            String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
            if (zipCode.equals("default")) {
                showZipCodeDialog("");
            } else {
                presenter.downloadWeatherData(zipCode, fahrenheitOrCelsius);
                presenter.downloadWeatherDataHourly(zipCode, fahrenheitOrCelsius);
            }
        }

        //set up Flurry
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "CG256N5PW5DKT5F4QD58");
    }

    /**
     * create action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    /**
     * options for action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case for zip code change
            case R.id.action_settings + 1:
                showZipCodeDialog("");
                //answers log
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("BtnZipCodeClicked")
                        .putContentType("action button clicked")
                        .putContentId(R.id.action_settings + 1 + "")
                        .putCustomAttribute("Favorites Count", 20)
                        .putCustomAttribute("Screen Orientation", "Portrait"));

                //flurry
                Map<String, String> eventParams = new HashMap<>();
                eventParams.put("event", "click");
                eventParams.put("value", "zip code");
                FlurryAgent.logEvent("Button clicked for Flurry test", eventParams);
                break;

            //case to change from F to C or from C to F
            case R.id.action_settings + 2:

                //get the reference to the sharedPreference
                SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
                String fOrC = sharedPreferences.getString(CURRENT_SETTING, "default");

                //if the current settings were set to 'F' change them to 'C' and wise versa
                if (fOrC.equals("F")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CURRENT_SETTING, "C");
                    editor.apply();
                    String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
                    presenter.downloadWeatherData(zipCode, "C");
                    presenter.downloadWeatherDataHourly(zipCode, "C");

                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CURRENT_SETTING, "F");
                    editor.apply();
                    String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
                    presenter.downloadWeatherData(zipCode, "F");
                    presenter.downloadWeatherDataHourly(zipCode, "F");
                }

                // answers log
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("BtnUnitsClicked")
                        .putContentType("action button clicked")
                        .putContentId(R.id.action_settings + 2 + "")
                        .putCustomAttribute("Favorites Count", 20)
                        .putCustomAttribute("Screen Orientation", "Portrait"));

                //flurry log
                Map<String, String> eventParams2 = new HashMap<>();
                eventParams2.put("event", "click");
                eventParams2.put("value", "ForC");
                FlurryAgent.logEvent("Button clicked for Flurry test", eventParams2);

                break;

        }
        return true;
    }

    /**
     * This method will show custom dialog to enter the zipcode
     */
    public void showZipCodeDialog(String error) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        Button ok = (Button) dialog.findViewById(R.id.btnSubmit);
        final EditText zip = (EditText) dialog.findViewById(R.id.etZipCode);
        TextView tvError = (TextView) dialog.findViewById(R.id.tvError);
        if (!error.equals("")) {
            tvError.setText(error);
        }
        //if button is clicked, close the custom dialog
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ZIP_CODE, zip.getText().toString());
                editor.apply();
                String fOrC = sharedPreferences.getString(CURRENT_SETTING, "default");
                presenter.downloadWeatherData(zip.getText().toString(), fOrC);
                presenter.downloadWeatherDataHourly(zip.getText().toString(), fOrC);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * This method will update the toolbar with current weather info
     */
    @Override
    public void weatherDownloadedUpdateUI(final WeatherInfo weatherInfo, final String fOrC) {

        //if weatherInfo is null this means that invalid zip code was entered, so re ask the user
        if (weatherInfo == null || weatherInfo.getCurrentObservation() == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showZipCodeDialog("Not valid Zip code");
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //update the toolbar with current hour weather info
                    if (fOrC.equals("F")) {
                        String temperature = String.valueOf(weatherInfo.getCurrentObservation().getTempF());
                        String temperatureWithDegreeSign = temperature + getString(R.string.degree);
                        tvTemperature.setText(temperatureWithDegreeSign);
                    } else {
                        String temperature = String.valueOf(weatherInfo.getCurrentObservation().getTempC());
                        String temperatureWithDegreeSign = temperature + getString(R.string.degree);
                        tvTemperature.setText(temperatureWithDegreeSign);
                    }
                    tvCondition.setText(weatherInfo.getCurrentObservation().getWeather());
                    myToolbar.setTitle(weatherInfo.getCurrentObservation().getDisplayLocation().getFull());

                    //change the color of toolbar based on current temperature
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (weatherInfo.getCurrentObservation().getTempF() > 60) {
                            toolBarHeaderViewLinearLayout.setBackgroundColor(getColor(R.color.colorAccent));
                            myToolbar.setBackgroundColor(getColor(R.color.colorAccent));
                        } else {
                            toolBarHeaderViewLinearLayout.setBackgroundColor(getColor(R.color.colorPrimary));
                            myToolbar.setBackgroundColor(getColor(R.color.colorPrimary));
                        }
                    }
                }
            });
        }

    }

    /**
     * This method will update the hourly forecast for each day
     */
    @Override
    public void hourlyWeatherDownloadedUpdateUI(final HourlyWeatherInfo hourlyWeatherInfo, final String fOrC) {

        if (hourlyWeatherInfo != null && hourlyWeatherInfo.getHourlyForecast() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<HourlyForecast> individualDay = new ArrayList<>();
                    List<DayData> listOfDays = new ArrayList<>();
                    DayData dayData;
                    while (hourlyWeatherInfo.getHourlyForecast().size() > 0) {
                        if (Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(0).getFCTTIME().getHour()) < 24 &&
                                Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(0).getFCTTIME().getHour()) != 0) {

                            individualDay.add(hourlyWeatherInfo.getHourlyForecast().get(0));
                            hourlyWeatherInfo.getHourlyForecast().remove(0);

                        } else if (Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(0).getFCTTIME().getHour()) == 0) {

                            dayData = new DayData(individualDay);
                            listOfDays.add(dayData);
                            individualDay = new ArrayList<>();
                            individualDay.add(hourlyWeatherInfo.getHourlyForecast().get(0));
                            hourlyWeatherInfo.getHourlyForecast().remove(0);

                        }

                    }
                    adapter = new DaysAdapter(listOfDays, getApplicationContext(), fOrC);
                    rvDays.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }
}

