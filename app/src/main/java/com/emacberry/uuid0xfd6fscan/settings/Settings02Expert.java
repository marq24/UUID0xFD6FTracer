package com.emacberry.uuid0xfd6fscan.settings;

import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.emacberry.uuid0xfd6fscan.R;

public class Settings02Expert extends PreferenceFragmentCompat {

    private EditTextPreference mTreshold;
    private EditTextPreference mGroupMed;
    private EditTextPreference mGroupNear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings02);
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.pref02_SIGSTRENGTH_title);
        }
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

        Preference.SummaryProvider<EditTextPreference> sProvider = preference -> String.format(getString(R.string.pref02_SIGSTRENGTH_summary), preference.getText());

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
    }
}
