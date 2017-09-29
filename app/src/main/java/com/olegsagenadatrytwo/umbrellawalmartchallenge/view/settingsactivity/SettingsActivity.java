package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.R;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.inject.settingsactivity.DaggerSettingsActivityComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity implements SettingsActivityContract.View {

    //constants
    private static final int SETTINGS_REQUEST = 1;

    private Toolbar myToolbar;

    //recycler view
    private RecyclerView rvSettings;
    private SettingsAdapter adapter;

    String error;

    //presenter
    @Inject
    SettingsActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DaggerSettingsActivityComponent.create().inject(this);

        //get intent
        Intent intent = getIntent();
        error = intent.getStringExtra("error");
        if(error != null){
            showError();

        }

        actionBarSetUp();
        presenterSetUp();
        recyclerViewSetUp();
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }

    private void recyclerViewSetUp() {
        rvSettings = (RecyclerView) findViewById(R.id.rvSettings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvSettings.setLayoutManager(layoutManager);
        rvSettings.setItemAnimator(itemAnimator);

        //create a list of settings that can be modified to add additional settings
        List<String> settingsList = new ArrayList<>();
        settingsList.add("Zip");
        settingsList.add("Units");

        //create the adapter with this list of settings
        SettingsAdapter adapter = new SettingsAdapter(settingsList, this);
        rvSettings.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void presenterSetUp() {
        presenter.attachView(this);
        presenter.setContext(this);
    }

    /**
     * action bar set up
     */
    private void actionBarSetUp() {
        myToolbar = (Toolbar) findViewById(R.id.my_settings_toolbar);
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    /**
     * create options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_action_bar, menu);
        return true;
    }

    /**
     * options for action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case back button
            case android.R.id.home:
                onBackPressed();
            break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(SETTINGS_REQUEST);
        super.onBackPressed();
    }
}
