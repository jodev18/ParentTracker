package com.khsoftsolutions.parenttracker.core;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by myxroft2 on 9/10/17.
 */

public class ParentApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cIVPmNYYcqxCkohWKrrNh6PVJ5vnNjCVljtVPseC")
                .server("https://parseapi.back4app.com/")
                .clientKey("fVnKyvPWjtfxK8qJnbX9N6ioz7oWpcy0mlNAlZof")
                .build()
        );
    }

}
