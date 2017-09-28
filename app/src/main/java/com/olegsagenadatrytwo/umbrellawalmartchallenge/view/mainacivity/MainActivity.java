package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity.SettingsActivity;

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
    private static final int SETTINGS_REQUEST = 1;

    //presenter
    private MainActivityPresenter presenter;

    //views for toolbar
    private TextView tvTemperature;
    private TextView tvCondition;
    private Toolbar myToolbar;
    private LinearLayout toolBarHeaderViewLinearLayout;

    //recycler view
    private RecyclerView rvDays;
    private DaysAdapter adapter;

    //global weather data variables to avoid API calls
    private WeatherInfo weatherInfo;
    private HourlyWeatherInfo hourlyWeatherInfo;
    private String globalZipCode;
    private String globalFOrC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);

        actionBarSetUp();
        recyclerViewSetUp();
        initSharedPreferenceSettings();
        setUpFlurry();
        presenterSetUp();
    }

    /**
     * action bar set up
     */
    private void actionBarSetUp() {
        LinearLayout includeView = (LinearLayout) findViewById(R.id.toolbar_header_view);
        tvTemperature = (TextView) includeView.findViewById(R.id.tvTemperatureTop);
        tvCondition = (TextView) includeView.findViewById(R.id.tvConditionTop);
        toolBarHeaderViewLinearLayout = (LinearLayout) includeView.findViewById(R.id.toolbar_header_view);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        setSupportActionBar(myToolbar);
    }

    /**
     * create options menu
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
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivityForResult(settings, SETTINGS_REQUEST);
                //answers log
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("BtnSettingsClicked")
                        .putContentType("action button clicked")
                        .putContentId(R.id.action_settings + "")
                        .putCustomAttribute("Favorites Count", 20)
                        .putCustomAttribute("Screen Orientation", "Portrait"));

                //flurry
                Map<String, String> eventParams = new HashMap<>();
                eventParams.put("event", "click");
                eventParams.put("value", "settings");
                FlurryAgent.logEvent("Button clicked for Flurry test", eventParams);
                break;

        }
        return true;
    }

    /**
     * on activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST) {
            SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
            String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
            String fOrC = sharedPreferences.getString(CURRENT_SETTING, "default");

            //if zip code was changed make a API call
            if (!globalZipCode.equals(zipCode)) {
                presenter.downloadWeatherDataHourly(zipCode);
                presenter.downloadWeatherData(zipCode);
                globalZipCode = zipCode;
                globalFOrC = fOrC;

            } else { // if zip code was not changed check if the unit was changed

                if (!globalFOrC.equals(fOrC)) {
                    weatherDownloadedUpdateUI(weatherInfo);
                    hourlyWeatherDownloadedUpdateUI(hourlyWeatherInfo);
                    globalFOrC = fOrC;
                }
                globalFOrC = fOrC;
            }

        }
    }

    /**
     * recycler view set up
     */
    private void recyclerViewSetUp() {
        rvDays = (RecyclerView) findViewById(R.id.rvDays);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvDays.setLayoutManager(layoutManager);
        rvDays.setItemAnimator(itemAnimator);
    }

    /**
     * configure the initial settings sharedPreference
     */
    private void initSharedPreferenceSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        String fahrenheitOrCelsius = sharedPreferences.getString(CURRENT_SETTING, "default");

        //if there was no current settings for temperature make fahrenheit the default
        if (fahrenheitOrCelsius.equals("default")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_SETTING, getString(R.string.fahrenheit));
            editor.apply();
            globalFOrC = getString(R.string.fahrenheit);
        }
        globalFOrC = fahrenheitOrCelsius;
    }

    /**
     * set up Flurry
     */
    private void setUpFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "CG256N5PW5DKT5F4QD58");
    }

    /**
     * presenter set up
     */
    private void presenterSetUp() {
        presenter = new MainActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        String zipCode = sharedPreferences.getString(ZIP_CODE, "default");
        if (zipCode.equals("default")) {
            showZipCodeDialog("");
        } else {
            globalZipCode = zipCode;
            presenter.downloadWeatherData(zipCode);
            presenter.downloadWeatherDataHourly(zipCode);
        }

    }


    /**
     * This method will show custom dialog to enter the zip code
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
                String zipCode = zip.getText().toString();

                editor.putString(ZIP_CODE, zipCode);
                editor.apply();
                presenter.downloadWeatherData(zipCode);
                presenter.downloadWeatherDataHourly(zipCode);
                globalZipCode = zipCode;
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
    public void weatherDownloadedUpdateUI(final WeatherInfo weatherInfo) {

        this.weatherInfo = weatherInfo;

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

                    SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
                    String fahrenheitOrCelsius = sharedPreferences.getString(CURRENT_SETTING, "default");

                    //update the toolbar with current hour weather info
                    if (fahrenheitOrCelsius.equals(getString(R.string.fahrenheit))) {
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
                            toolBarHeaderViewLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            myToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        } else {
                            toolBarHeaderViewLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                            myToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
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
    public void hourlyWeatherDownloadedUpdateUI(final HourlyWeatherInfo hourlyWeatherInfo) {

        if (hourlyWeatherInfo != null && hourlyWeatherInfo.getHourlyForecast() != null) {
            this.hourlyWeatherInfo = hourlyWeatherInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<HourlyForecast> individualDay = new ArrayList<>();
                    List<DayData> listOfDays = new ArrayList<>();
                    DayData dayData;

                    for (int i = 0; i < hourlyWeatherInfo.getHourlyForecast().size(); i++) {
                        if (Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(i).getFCTTIME().getHour()) < 24 &&
                                Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(i).getFCTTIME().getHour()) != 0) {

                            individualDay.add(hourlyWeatherInfo.getHourlyForecast().get(i));

                        } else if (Integer.parseInt(hourlyWeatherInfo.getHourlyForecast().get(i).getFCTTIME().getHour()) == 0) {

                            dayData = new DayData(individualDay);
                            listOfDays.add(dayData);
                            individualDay = new ArrayList<>();
                            individualDay.add(hourlyWeatherInfo.getHourlyForecast().get(i));

                        }
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
                    String fahrenheitOrCelsius = sharedPreferences.getString(CURRENT_SETTING, "default");
                    adapter = new DaysAdapter(listOfDays, getApplicationContext(), fahrenheitOrCelsius);
                    rvDays.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}

