package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity;

import android.content.Context;

/**
 * Created by omcna on 9/28/2017.
 */

public class SettingsActivityPresenter implements SettingsActivityContract.Presenter {

    private SettingsActivityContract.View view;
    private Context context;

    @Override
    public void attachView(SettingsActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

}
