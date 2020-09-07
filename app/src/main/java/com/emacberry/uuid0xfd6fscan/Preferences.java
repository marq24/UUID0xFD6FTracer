package com.emacberry.uuid0xfd6fscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {

    private static Preferences pref = null;
    public static Preferences getInstance(Context c) {
        if (pref != null) {
            if(pref.prefData == null){
                pref = new Preferences(c);
            }
            return pref;
        } else {
            pref = new Preferences(c);
            return pref;
        }
    }
    private SharedPreferences prefData;
    private Context c;

    private Preferences(Context c) {
        try {
            prefData = PreferenceManager.getDefaultSharedPreferences(c);
            this.c = c;
            if (prefData.getAll().size() == 0) {
                // WE SHOULD INIT the SCAN-MODE depending on the USER_LANGUAGE/COUNTRY
                // In France/French we can set the default value to 'FRA'
                try {
                    String x = c.getResources().getConfiguration().locale.getCountry();
                    if (x.equalsIgnoreCase("fr")) {
                        SharedPreferences.Editor e = prefData.edit();
                        e.putString(c.getString(R.string.PKEY_SCANMODE), "FRA");
                        e.commit();
                    }
                }catch(Throwable t){}

            }
        }catch(Throwable t){
            Log.e("PREFS", ""+t.getMessage());
        }
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener ocpl){
        if(prefData != null) {
            prefData.registerOnSharedPreferenceChangeListener(ocpl);
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener ocpl){
        if(prefData != null) {
            prefData.unregisterOnSharedPreferenceChangeListener(ocpl);
        }
    }

    public boolean getBoolean(int pkey, int dval) {
        if(prefData != null){
            return prefData.getBoolean(c.getString(pkey), Boolean.parseBoolean(c.getString(dval)));
        }
        return false;
    }

    public String getString(int pkey, int dval) {
        if(prefData != null){
            return prefData.getString(c.getString(pkey), c.getString(dval));
        }
        return null;
    }

    public void setBoolean(int pkey, boolean b) {
        if(prefData != null) {
            SharedPreferences.Editor e = prefData.edit();
            e.putBoolean(c.getString(pkey), b);
            e.commit();
        }
    }
}