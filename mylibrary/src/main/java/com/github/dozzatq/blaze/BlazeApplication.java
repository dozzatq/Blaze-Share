package com.github.dozzatq.blaze;

import android.app.Application;

/**
 * Created by RondailP on 11.10.2016.
 */
public class BlazeApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        BlazeContext.getInstance().setApplicationContext(getApplicationContext());
    }
}
