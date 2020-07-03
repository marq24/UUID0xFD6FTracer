package com.emacberry.uuid0xfd6fscan.settings;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.emacberry.uuid0xfd6fscan.R;

public class Settings01 extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat mAutostartSwitch;
    private static final int PERMISSION_CHECK_BOOT = 99;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings01);
        /*androidx.activity.result.contract.ActivityResultContracts.RequestPermission x = new ActivityResultContracts.RequestPermission();
        ActivityResultLauncher<String> mx = getActivity().registerForActivityResult(x, new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                System.out.println(result);
            }
        });
        mx.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        */
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.pref01_TITLE);
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
