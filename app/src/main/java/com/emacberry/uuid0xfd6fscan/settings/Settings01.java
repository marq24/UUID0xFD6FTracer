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
import android.text.InputType;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.emacberry.uuid0xfd6fscan.R;

public class Settings01 extends PreferenceFragmentCompat {
    private static final int PERMISSION_CHECK_BOOT = 99;

    private SwitchPreferenceCompat mAutostartSwitch;
    private SwitchPreferenceCompat batteryOptimization;

    private EditTextPreference mTreshold;
    private EditTextPreference mGroupMed;
    private EditTextPreference mGroupNear;

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

        // EXPERT Settings....
        SwitchPreferenceCompat groupBySignalStrength = findPreference(getString(R.string.PKEY_GROUPBYSIGSTRENGTH));
        groupBySignalStrength.setOnPreferenceChangeListener((preference, newValue) -> {
            if(newValue != null && newValue.toString().toLowerCase().startsWith("t")){
                mGroupNear.setEnabled(true);
                mGroupMed.setEnabled(true);
            }else{
                mGroupNear.setEnabled(false);
                mGroupMed.setEnabled(false);
            }
            return true;
        });
        SwitchPreferenceCompat treshold = findPreference(getString(R.string.PKEY_USETHRESHOLD));
        treshold.setOnPreferenceChangeListener((preference, newValue) -> {
            if(newValue != null && newValue.toString().toLowerCase().startsWith("t")){
                mTreshold.setEnabled(true);
            }else{
                mTreshold.setEnabled(false);
            }
            return true;
        });

        EditTextPreference.OnBindEditTextListener numberFilter = (EditTextPreference.OnBindEditTextListener) editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            //editText.addTextChangedListener(new MyTextWatcher(editText));
        };

        Preference.SummaryProvider<EditTextPreference> sProvider = preference -> String.format(getString(R.string.pref01_SIGSTRENGTH_summary), preference.getText());

        mGroupNear = ((EditTextPreference) findPreference(getString(R.string.PKEY_GROUPNEARVAL)));
        mGroupNear.setOnBindEditTextListener(numberFilter);
        mGroupNear.setSummaryProvider(sProvider);
        mGroupNear.setEnabled(groupBySignalStrength.isChecked());

        mGroupMed = ((EditTextPreference) findPreference(getString(R.string.PKEY_GROUPMEDVAL)));
        mGroupMed.setOnBindEditTextListener(numberFilter);
        mGroupMed.setSummaryProvider(sProvider);
        mGroupMed.setEnabled(groupBySignalStrength.isChecked());

        mTreshold = ((EditTextPreference) findPreference(getString(R.string.PKEY_THRESHOLDVAL)));
        mTreshold.setOnBindEditTextListener(numberFilter);
        mTreshold.setSummaryProvider(sProvider);
        mTreshold.setEnabled(treshold.isChecked());
    }

    /*private class MyTextWatcher implements  TextWatcher{
        private EditText iEdit;

        private MyTextWatcher(EditText edit) {
            this.iEdit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            iEdit.removeTextChangedListener(this);

            String replaced = s.toString().replaceAll("[^[0-9]]", "");
            iEdit.setText(replaced);
            iEdit.setSelection(replaced.length());

            iEdit.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }*/

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
