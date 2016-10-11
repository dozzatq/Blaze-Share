package com.github.dozzatq.blaze;

import android.content.Context;

/**
 * Created by RondailP on 10.10.2016.
 */
public class BlazeContext {
    private static BlazeContext ourInstance = null;

    public static BlazeContext getInstance() {
        BlazeContext localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (BlazeContext.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new BlazeContext();
                }
            }
        }
        return localInstance;
    }

    private Context applicationContext;

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
