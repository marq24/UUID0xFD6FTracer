package com.emacberry.uuid0xfd6fscan;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.emacberry.uuid0xfd6fscan.settings.Settings01;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Settings01())
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LockCheckerApi26.checkLockedApi26(this, true);
        } else {
            try {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }catch(Throwable t){
                Log.d("UNLOCK", "onCreate()", t);
            }
        }
    }
}