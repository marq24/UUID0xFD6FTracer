package com.emacberry.uuid0xfd6ftracer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.emacberry.uuid0xfd6ftracer.ui.main.PlaceholderFragment;
import com.emacberry.uuid0xfd6ftracer.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BeaconScannerActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ACTIVITY";

    protected static final String INTENT_EXTRA_TERMINATE_APP = "TERMINATE";
    protected static final String INTENT_EXTRA_SERVICE_ACTION = "SERVICE-ACTON";

    private Handler mHandler = new Handler();
    private ScannerService mScannerService;
    private ViewPager mViewPager;

    private boolean mActivityIsCreated = false;
    private boolean mKillApp = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName paramComponentName) {
            Log.w(LOG_TAG, "onServiceDisconnected() called... " + paramComponentName);
            if (paramComponentName != null) {
                String cName = paramComponentName.getClassName();
                if (cName.equalsIgnoreCase("com.emacberry.uuid0xfd6ftracer.ScannerService")) {
                    mScannerService = null;
                }
            }
        }

        @Override
        public void onServiceConnected(ComponentName paramComponentName, IBinder service) {
            Log.w(LOG_TAG, "onServiceConnected() called... " + service);
            if (service != null) {
                if (service instanceof ScannerService.LocalBinder) {
                    ScannerService.LocalBinder b = (ScannerService.LocalBinder) service;
                    mScannerService = b.getServerInstance();
                    mScannerService.setGuiCallback(BeaconScannerActivity.this);
                    BeaconScannerActivity.this.newBeconEvent(null);
                }
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        try{
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            }
        }catch(Throwable t){
            Log.d(LOG_TAG, ""+t.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(mScannerService != null){
                        mScannerService.checkForScannStart();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private final int MENU_FINISH = 80;
    private final int MENU_EXIT = 100;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            super.onCreateOptionsMenu(menu);
        } catch (Throwable t) {
        }
        menu.add(Menu.NONE, MENU_FINISH, Menu.NONE, R.string.menu_finish);
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.menu_exit);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            super.onCreateOptionsMenu(menu);
        } catch (Throwable t) {
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            return super.onContextItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case MENU_FINISH:
                    finish();
                    return true;

                case MENU_EXIT:
                    exitApp();
                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getBooleanExtra(INTENT_EXTRA_TERMINATE_APP, false)) {
            Log.w(LOG_TAG, "TERMINATE_APP triggered via Service");
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
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(sectionsPagerAdapter);

        // MARQ24: Currently NO other TAB's implemented... (will be added)
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.removeAllTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        MyOnTabListenerAndroidX otl = new MyOnTabListenerAndroidX();
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setTabListener(otl).setText(sectionsPagerAdapter.getPageTitle(i)));
        }*/

        FloatingActionButton start = findViewById(R.id.start);
        start.setOnClickListener(view -> {
            if (mScannerService != null) {
                mScannerService.startScan(true);
            }
        });
        FloatingActionButton stop = findViewById(R.id.stop);
        stop.setOnClickListener(view -> {
            if (mScannerService != null) {
                mScannerService.stopScan(true);
            }
        });
        mActivityIsCreated = true;
    }

    @Override
    protected void onStart() {
        Log.w(LOG_TAG, "onStart() called");
        try {
            super.onStart();
            try {
                if (mConnection != null) {
                    bindService(new Intent(this, ScannerService.class), mConnection, BIND_IMPORTANT | BIND_ALLOW_OOM_MANAGEMENT | BIND_ABOVE_CLIENT);
                    Log.w(LOG_TAG, "BIND SERVICE onStart()");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // make sure the scanning service is running...
            if (!ScannerService.isRunning) {
                Intent loggerIntent = new Intent(this, ScannerService.class);
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
                handleIntent(i);
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

    @Override
    protected void onStop() {
        try {
            if (mConnection != null) {
                unbindService(mConnection);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            super.onStop();
        } catch (Throwable t) {
            Log.e(LOG_TAG, "super.onStop() caused " + t.getMessage());
        }
        Log.w(LOG_TAG, "onStop() called");
    }


    @Override
    protected void onDestroy() {
        Log.w(LOG_TAG, "onDestroy called");
        if(mScannerService != null){
            mScannerService.setGuiCallback(null);
        }
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

    protected void exitApp() {
        stopService(new Intent(this, ScannerService.class));
        if (mHandler != null) {
            mHandler.postDelayed(() -> {
                mKillApp = true;
                if (mActivityIsCreated) {
                    // if the activity is visible we simply call 'finish' -> that will then
                    // trigger the kill...
                    finish();
                } else {
                    killApp();
                }

            }, 250);
        }
    }

    private void killApp() {
        Runnable killRunnable = () -> {
            try {
                Log.w(LOG_TAG, "TERMINATE_APP - killProcess called");
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Throwable t) {
                Log.e(LOG_TAG, "" + t.getMessage());
            }
            try {
                System.exit(0);
            } catch (Throwable t) {
                Log.e(LOG_TAG, "" + t.getMessage());
            }
        };
        if (mHandler != null) {
            mHandler.postDelayed(killRunnable, 1000);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean handleEvent = true;
        if (intent != null) {
            Log.d(LOG_TAG, "start intent extras: " + intent.getExtras());
            boolean wasStartFromService = intent.hasExtra(INTENT_EXTRA_SERVICE_ACTION);

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

            if (handleEvent && intent.hasExtra(INTENT_EXTRA_SERVICE_ACTION)) {
                //System.out.println("A");
            }
        }
    }

    public void newBeconEvent(String addr) {
        if(mScannerService != null){
            Log.v(LOG_TAG, "newBeconEvent: "+mScannerService.mContainer.get(addr));
            final int size = mScannerService.mContainer.size();
            runOnUiThread(()->{
            if(mViewPager != null) {
                Fragment info = ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(0);
                if (info instanceof PlaceholderFragment) {
                    ((PlaceholderFragment) info).setText(String.format(getString(R.string.act_active_beacons), size));
                }
            }});
        }
    }

    private class MyOnTabListenerAndroidX implements ActionBar.TabListener {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (tab != null && mViewPager != null) {
                try {
                    mViewPager.setCurrentItem(tab.getPosition());
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }
}
