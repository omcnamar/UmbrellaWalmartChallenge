package com.olegsagenadatrytwo.umbrellawalmartchallenge.inject.settingsactivity;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity.SettingsActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by omcna on 9/29/2017.
 */
@Module
class SettingsActivityModule {

    @Provides
    SettingsActivityPresenter providesSettingsActivityPresenter(){
        return new SettingsActivityPresenter();
    }
}
