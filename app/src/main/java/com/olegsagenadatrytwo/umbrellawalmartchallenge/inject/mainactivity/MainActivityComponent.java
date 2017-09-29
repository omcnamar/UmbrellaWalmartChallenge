package com.olegsagenadatrytwo.umbrellawalmartchallenge.inject.mainactivity;


import com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity.MainActivity;

import dagger.Component;


@Component(modules = MainActivityModule.class)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);

}