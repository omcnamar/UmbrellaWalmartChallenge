package com.olegsagenadatrytwo.umbrellawalmartchallenge.inject.mainactivity;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
class MainActivityModule {

    @Provides
    MainActivityPresenter providesMainActivityPresenter() {
        return new MainActivityPresenter();
    }

}