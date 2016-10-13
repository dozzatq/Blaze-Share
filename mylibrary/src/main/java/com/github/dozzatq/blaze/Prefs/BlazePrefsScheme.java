package com.github.dozzatq.blaze.Prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.dozzatq.blaze.BlazeContext;

/**
 * Created by RondailP on 12.10.2016.
 */
public class BlazePrefsScheme {

    private String prefsName;
    private int prefsMode;

    private Context appContext;

    BlazePrefsScheme(String prefsName, int prefsMode) {
        this.prefsName = prefsName;
        this.prefsMode = prefsMode;
        appContext = BlazeContext.getInstance().getApplicationContext();
    }

    public void applyDefaults(int resId, boolean readAgain )
    {
        if (prefsMode == -1 && prefsName == null)
            PreferenceManager.setDefaultValues(appContext, resId, readAgain);
        else PreferenceManager.setDefaultValues(appContext, prefsName, prefsMode, resId, readAgain);
    }

    /* SYNC */
    public void clearPrefs()
    {
        getPrefs().edit().clear().apply();
    }

    /* ASYNC */
    public void clearPrefsFuture()
    {
        getPrefs().edit().clear().commit();
    }

    /* ASYNC */
    public void putString(String key, String value)
    {
        getPrefs().edit().putString(key, value).apply();
    }

    /* SYNC */
    public boolean putStringFuture(String key, String value)
    {
        return getPrefs().edit().putString(key, value).commit();
    }

    public String getString(String key)
    {
        return getPrefs().getString(key, null);
    }

    /* ASYNC */
    public void putBoolean(String key, boolean value)
    {
        getPrefs().edit().putBoolean(key, value).apply();
    }

    /* SYNC */
    public boolean putBooleanFuture(String key, boolean value)
    {
        return getPrefs().edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean default_bool)
    {
        return getPrefs().getBoolean(key, default_bool);
    }

    public void removeValue(String key)
    {
        getPrefs().edit().remove(key).apply();
    }

    /* ASYNC */
    public void putLong(String key, Long value)
    {
        getPrefs().edit().putLong(key, value).apply();
    }

    /* SYNC */
    public boolean putLongFuture(String key, Long value)
    {
        return getPrefs().edit().putLong(key, value).commit();
    }

    public long getLong(String key, long defValue) {
        return getPrefs().getLong(key, defValue);
    }

    /* ASYNC */
    public void putFloat(String key, Float value)
    {
        getPrefs().edit().putFloat(key, value).apply();
    }

    /* SYNC */
    public boolean putFloatFuture(String key, Float value)
    {
        return getPrefs().edit().putFloat(key, value).commit();
    }

    public float getFloat(String key, float defValue) {
        return getPrefs().getFloat(key, defValue);
    }

    /* ASYNC */
    public void putInt(String key, Integer value)
    {
        getPrefs().edit().putInt(key, value).apply();
    }

    /* SYNC */
    public boolean putIntFuture(String key, Integer value)
    {
        return getPrefs().edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return getPrefs().getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return getPrefs().getString(key, defValue);
    }

    private SharedPreferences getPrefs() {
        if (prefsMode == -1 && prefsName == null)
            return PreferenceManager.getDefaultSharedPreferences(appContext);
        else return appContext.getSharedPreferences(prefsName, prefsMode);
    }

    private boolean save(String key, long value) {
        return getPrefs()
                .edit()
                .putLong(key, value)
                .commit();
    }

    private boolean save(String key, float value) {
        return getPrefs()
                .edit()
                .putFloat(key, value)
                .commit();
    }

    private boolean save(String key, int value) {
        return getPrefs()
                .edit()
                .putInt(key, value)
                .commit();
    }

    private boolean save(String key, String value) {
        return getPrefs()
                .edit()
                .putString(key, value)
                .commit();
    }
}
