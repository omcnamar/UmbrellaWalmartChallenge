package com.olegsagenadatrytwo.umbrellawalmartchallenge.inject.settingsactivity;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity.SettingsActivity;

import dagger.Component;

/**
 * Created by omcna on 9/29/2017.
 */
@Component(modules = SettingsActivityModule.class)
public interface SettingsActivityComponent {

    void inject(SettingsActivity settingsActivity);
}
