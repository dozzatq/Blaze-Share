package com.github.dozzatq.blaze.Prefs;

/**
 * Created by RondailP on 09.09.2016.
 */
public class BlazePreferences extends BlazePrefsScheme {
    private static BlazePreferences ourInstance = null;
    public static BlazePreferences getInstance() {
        BlazePreferences localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (BlazePreferences.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new BlazePreferences();
                }
            }
        }
        return localInstance;
    }

    private BlazePreferences(){
        super(null, -1);
    }

    public BlazePrefsScheme getScheme(String prefsName, int prefsMode)
    {
        return new BlazePrefsScheme(prefsName, prefsMode);
    }

    public BlazePrefsScheme getDefaultScheme()
    {
        return this;
    }

}
