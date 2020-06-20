package com.emacberry.uuid0xfd6ftracer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {

    private static Preferences pref = null;
    public static Preferences getInstance(Context c) {
        if (pref != null) {
            return pref;
        } else {
            pref = new Preferences(c);
            return pref;
        }
    }
    private SharedPreferences prefData;
    private Context c;

    private Preferences(Context c) {
        prefData = PreferenceManager.getDefaultSharedPreferences(c);
        this.c = c;
        try {
            if (prefData.getAll().size() == 0) {
                PreferenceManager.setDefaultValues(c, R.xml.settings01, true);
            } else {
                PreferenceManager.setDefaultValues(c, R.xml.settings01, false);
            }
        }catch(Throwable t){
            Log.e("PREFS", ""+t.getMessage());
        }
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener ocpl){
        prefData.registerOnSharedPreferenceChangeListener(ocpl);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener ocpl){
        prefData.unregisterOnSharedPreferenceChangeListener(ocpl);
    }

    public boolean getBoolean(int pkey, int dval) {
        if(prefData != null){
            return prefData.getBoolean(c.getString(pkey), Boolean.parseBoolean(c.getString(dval)));
        }
        return false;
    }

    public void setBoolean(int pkey, boolean b) {
        if(prefData != null) {
            SharedPreferences.Editor e = prefData.edit();
            e.putBoolean(c.getString(pkey), b);
            e.commit();
        }
    }
}