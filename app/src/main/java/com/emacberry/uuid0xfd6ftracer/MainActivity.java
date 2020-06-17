package com.emacberry.uuid0xfd6ftracer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emacberry.uuid0xfd6ftracer.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public static final String TERMINATE_APP = "";
    public static final String SERVICE_ACTION = "SERVICE-ACTON";

    private static final String LOG_TAG = "0xFD6F";
    private Handler mHandler = new Handler();

    private DataLogger _dataProviderService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName paramComponentName) {
            Log.w(LOG_TAG, "onServiceDisconnected() called... " + paramComponentName);
            if (paramComponentName != null) {
                String cName = paramComponentName.getClassName();
                if (cName.equalsIgnoreCase("com.emacberry.uuid0xfd6ftracer.DataLogger")) {
                    _dataProviderService = null;
                }
            }
        }

        @Override
        public void onServiceConnected(ComponentName paramComponentName, IBinder service) {
            Log.w(LOG_TAG, "onServiceConnected() called... " + service);
            if (service != null) {
                if (service instanceof DataLogger.LocalBinder) {
                    DataLogger.LocalBinder b = (DataLogger.LocalBinder) service;
                    _dataProviderService = b.getServerInstance();
                }
            }
        }
    };

    private Intent getServiceIntent() {
        return new Intent(this, DataLogger.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getBooleanExtra(TERMINATE_APP, false)) {
            Log.i(LOG_TAG, "TERMINATE_APP");
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitApp();
                    }
                }, 50);
            }
            finish();
        }

        requestPermissions(new String[]{
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN}, 99);

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (_dataProviderService != null) {
                    _dataProviderService.startScan();
                }
            }
        });
        FloatingActionButton stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (_dataProviderService != null) {
                    _dataProviderService.stopScan();
                }
            }
        });

        mActivityIsCreated = true;
    }

    protected void onStart() {
        try {
            Log.d(LOG_TAG, "START ACTIVITY");
            super.onStart();
            try {
                if (mConnection != null) {
                    bindService(getServiceIntent(), mConnection, BIND_IMPORTANT | BIND_ALLOW_OOM_MANAGEMENT | BIND_ABOVE_CLIENT);
                    Log.w(LOG_TAG, "BIND SERVICE!");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (!DataLogger.isRunning) {
                Intent loggerIntent = getServiceIntent();
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(loggerIntent);
                    } else {
                        startService(loggerIntent);
                    }
                } catch (Throwable t) {
                    Log.d(LOG_TAG, "" + t.getMessage());
                }
            }

            Intent i = getIntent();
            if (i != null) {
                Log.d(LOG_TAG, "start intent extras: " + i.getExtras());
                boolean wasStartFromService = i.hasExtra(SERVICE_ACTION);

                /*boolean isAutoStartAfterBoot = i.hasExtra(BaseActivity.STARTINBACKGROUND);
                boolean wasStartFromService = i.hasExtra(WASSTARTFROMSERVICE);

                if (i.hasExtra(BaseActivity.ISBOOTCHECK) || isAutoStartAfterBoot || !DataLogger.isRunning) {
                    if (!wasStartFromService) {
                        _prefs.setPlanedServiceRestart(true);
                        Intent loggerIntent = getServiceIntent();
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= 26) {
                                startForegroundService(loggerIntent);
                            } else {
                                startService(loggerIntent);
                            }
                        } catch (Throwable t) {
                            Log.d(LOG_TAG, "" + t.getMessage());
                        }
                        _doNotStopGPSService = false;
                        lastServiceStartTs = System.currentTimeMillis();
                        Log.w(LOG_TAG, "START SERVICE!");
                    }
                }*/

            } else {
                Log.d(LOG_TAG, "start intent was null");
            }
        } catch (Exception e) {
            try {
                Toast.makeText(getApplicationContext(), "Exception! '" + e.getMessage() + "' Please RESTART", Toast.LENGTH_LONG).show();
            } catch (Throwable T) {
            }
        } catch (Throwable t) {
            try {
                Toast.makeText(getApplicationContext(), "FATAL ERROR! '" + t.getMessage() + "' Please RESTART", Toast.LENGTH_LONG).show();
            } catch (Throwable T) {
            }
        }
    }

    protected void onStop() {
        try {
            if (mConnection != null) {
                unbindService(mConnection);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        /*if (canActivityOnStopAlsoStopBackgroundService()) {
            if (lastServiceStartTs + 500 < System.currentTimeMillis()) {
                if (!_doNotStopGPSService) {
                    //_logger._lastNoGpsFixTimeStamp = -1;
                    stopService(getServiceIntent());
                    Log.w(LOG_TAG, "STOP SERVICE!");
                }
            }
        }*/
        try {
            super.onStop();
        } catch (Throwable t) {
            Log.e(LOG_TAG, "super.onStop() caused " + t.getMessage());
        }
        Log.d(LOG_TAG, "STOP ACTIVITY");
    }


    @Override
    protected void onDestroy() {
        Log.w(this.getClass().getName(), "DESTROYED");
        mActivityIsCreated = false;
        if (mKillApp) {
            killApp();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean mKillApp = false;
    private boolean mActivityIsCreated = false;

    protected void exitApp() {
        stopService(getServiceIntent());
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mKillApp = true;
                    if (mActivityIsCreated) {
                        // if the activity is visible we simply call 'finish' -> that will then
                        // trigger the kill...
                        finish();
                    } else {
                        killApp();
                    }

                }
            }, 250);
        }
    }

    private void killApp() {
        Runnable killRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(LOG_TAG, "TERMINATE_APP - killProcess called");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    //Process.sendSignal(android.os.Process.myPid(), Process.SIGNAL_QUIT);
                } catch (Throwable t) {
                    Log.d(LOG_TAG, "" + t.getMessage());
                }
                try {
                    System.exit(0);
                } catch (Throwable t) {
                    Log.d(LOG_TAG, "" + t.getMessage());
                }
            }
        };
        if (mHandler != null) {
            mHandler.postDelayed(killRunnable, 250);
        }
    }


    private void handleIntent(Intent intent) {
        boolean handleEvent = true;
        if (intent != null) {
            /*if (handleEvent && intent.getBooleanExtra(STOP_LOGGING, false)) {
                Log.i(LOG_TAG, "START OR STOP LOGGING");
                if (_logger != null && _logger.isViewingMode()) {
                    resetViewMode();
                }
                startOrStopLoggingService();
                handleEvent = false;
            }

            if (handleEvent && intent.getBooleanExtra(TTS_SHUTUP, false)) {
                Log.i(LOG_TAG, "TTS SHUT UP!");
                if (_logger != null) {
                    _logger.dFlyCancelAnyTTS();
                }
                handleEvent = false;
            }

            // launched via ShortCut
            if (handleEvent && "intent.action.STARTREC".equals(intent.getAction())) {
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startLoggingService(true);
                        }
                    }, 1500);
                }
                handleEvent = false;
            } else if (handleEvent && "intent.action.OPENPATHMAN".equals(intent.getAction())) {
                Log.i(LOG_TAG, "intent.action.OPENPATHMAN");
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openPathManager();
                        }
                    }, 100);
                }
                handleEvent = false;
            } else if (handleEvent && "intent.action.OPENSETTINGS".equals(intent.getAction())) {
                Log.i(LOG_TAG, "intent.action.OPENSETTINGS");
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openSettings();
                        }
                    }, 100);
                }
                handleEvent = false;
            }*/

            if (handleEvent && intent.hasExtra(SERVICE_ACTION)) {
                //System.out.println("A");
            }
        }
    }
}
