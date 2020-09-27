package com.emacberry.uuid0xfd6fscan.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.emacberry.uuid0xfd6fscan.R;

public class Settings01 extends PreferenceFragmentCompat {
    private static final int PERMISSION_CHECK_BOOT = 99;

    private SwitchPreferenceCompat mAutostartSwitch;
    private SwitchPreferenceCompat batteryOptimization;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings01);
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.pref01_TITLE);
        }

        batteryOptimization = findPreference(getText(R.string.PKEY_IGNOREBATTERYOPT));
        batteryOptimization.setOnPreferenceChangeListener((preference, newValue) -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent;
                if (newValue.toString().equalsIgnoreCase("true")) {
                    intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getContext().getPackageName()));
                } else {
                    intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                }
                try {
                    getActivity().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context c = getContext();
            if(c != null) {
                batteryOptimization.setChecked(((PowerManager) c.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(c.getPackageName()));
            }
        }else{
            batteryOptimization.setEnabled(false);
            batteryOptimization.setChecked(false);
            this.getPreferenceScreen().removePreference(batteryOptimization);
        }

        mAutostartSwitch = findPreference(getString(R.string.PKEY_AUTOSTART));
        mAutostartSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            String nv = newValue.toString();
            if (nv.toLowerCase().startsWith("t")) {
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_CHECK_BOOT);
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(batteryOptimization!= null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Context c = getContext();
                if(c != null) {
                    batteryOptimization.setChecked(((PowerManager) c.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(c.getPackageName()));
                }
            }else{
                batteryOptimization.setChecked(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CHECK_BOOT:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    if(getActivity() != null) {
                        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
                        SharedPreferences.Editor e = prefs.edit();
                        e.putBoolean(getContext().getString(R.string.PKEY_AUTOSTART), true);
                        e.commit();

                        if(mAutostartSwitch != null){
                            getActivity().runOnUiThread(()->mAutostartSwitch.setChecked(true));
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}