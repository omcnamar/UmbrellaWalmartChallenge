package com.olegsagenadatrytwo.umbrellawalmartchallenge;

import android.content.Context;

/**
 * Created by omcna on 9/28/2017.
 */

public interface BasePresenter<V extends BaseView> {

    void attachView(V view);
    void removeView();
    void setContext(Context context);
}
