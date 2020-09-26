package com.emacberry.uuid0xfd6fscan;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import com.emacberry.uuid0xfd6fscan.ui.main.PlaceholderFragment;
import com.emacberry.uuid0xfd6fscan.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScannerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "ACTIVITY";

    protected static final String INTENT_EXTRA_TERMINATE_APP    = "TERMINATE";
    protected static final String INTENT_EXTRA_SERVICE_ACTION   = "SERVICE-ACTON";
    protected static final String INTENT_EXTRA_AUTOSTART        = "AUTOSTART";

    private Handler mHandler = new Handler();
    private ScannerService mScannerService;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;

    private boolean mActivityIsCreated = false;
    private boolean mKillApp = false;
    private boolean mShowTotal = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName paramComponentName) {
            Log.w(LOG_TAG, "onServiceDisconnected() called... " + paramComponentName);
            if (paramComponentName != null) {
                String cName = paramComponentName.getClassName();
                if (cName.equalsIgnoreCase("com.emacberry.uuid0xfd6fscan.ScannerService")) {
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
                    mScannerService.setGuiCallback(ScannerActivity.this);
                    final int[] sizes = mScannerService.getBeaconCountByType();
                    runOnUiThread(()-> setActiveBeaconCount(sizes[0], sizes[1], sizes[2], mScannerService.getSignalStrengthGroupInfo()));
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
            Log.e(LOG_TAG, ""+t.getMessage());
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
                        if(mScannerService.isScanning()){
                            mScannerService.checkForScannStart();
                        }else{
                            mScannerService.startScan(true);
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private final int MENU_START_STOP = 80;
    private final int MENU_SETTING = 90;
    private final int MENU_FINISH = 900;
    private final int MENU_EXIT = 1000;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            super.onCreateOptionsMenu(menu);
        } catch (Throwable t) {
        }
        menu.add(Menu.NONE, MENU_SETTING, Menu.NONE, R.string.menu_settings);
        menu.add(Menu.NONE, MENU_START_STOP, Menu.NONE, R.string.menu_start);
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
        MenuItem startService = menu.findItem(MENU_START_STOP);
        startService.setEnabled(mScannerService != null);
        if(mScannerService != null){
            if(mScannerService.isScanning()){
                startService.setTitle(R.string.menu_stop);
            }else{
                startService.setTitle(R.string.menu_start);
            }
        }
        return true;
    }

    private class AsyncMenuHandler extends AsyncTask<MenuItem, Void, Void>{
        @Override
        protected Void doInBackground(MenuItem... menuItems) {
            try{
                if(menuItems.length>0){
                    MenuItem item = menuItems[0];
                    if (item.isCheckable()) {
                        // do nothing
                    } else {
                        switch (item.getItemId()) {
                            case MENU_SETTING:
                                Intent intent = new Intent(ScannerActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;

                            case MENU_START_STOP:
                                if(mScannerService != null){
                                    if(mScannerService.isScanning()) {
                                        mScannerService.stopScan(true);
                                    }else{
                                        mScannerService.startScan(true);
                                    }
                                }
                                break;

                            case MENU_FINISH:
                                finish();
                                break;

                            case MENU_EXIT:
                                exitApp();
                                break;

                            default:
                                break;
                        }
                    }
                }
            }catch(Throwable t){
                Log.e(LOG_TAG, ""+t.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        new AsyncMenuHandler().execute(item);
        // hack
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkTerninate(getIntent())) {

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

            mFab = findViewById(R.id.startstop);
            mFab.setOnClickListener(view -> {
                try {
                    if (mScannerService != null) {
                        if (mScannerService.isScanning()) {
                            mScannerService.stopScan(true);
                        } else {
                            mScannerService.startScan(true);
                        }
                    }
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "" + t.getMessage());
                }
            });
            Preferences.getInstance(this).registerOnSharedPreferenceChangeListener(this);

            mActivityIsCreated = true;
            switch (Preferences.getInstance(this).getString(R.string.PKEY_SCANMODE, R.string.DVAL_SCANMODE)){
                case "ENF_FRA":
                    setActiveBeaconCount(0, 0, 0, null);
                    break;

                case "FRA":
                    setActiveBeaconCount(0, -1, 0, null);
                    break;

                default:
                case "ENF":
                    setActiveBeaconCount(0, 0, -1, null);
                    break;
            }

            mShowTotal = Preferences.getInstance(this).getBoolean(R.string.PKEY_SHOWTOTAL, R.string.DVAL_SHOWTOTAL);
            mHandler.postDelayed(() -> updateButtonImg(), 500);
        }
    }

    private boolean checkTerninate(Intent intent) {
        if (intent != null && intent.getBooleanExtra(INTENT_EXTRA_TERMINATE_APP, false)) {
            try{
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
                return false;
            }catch(Throwable t){
                Log.e(LOG_TAG, ""+t.getMessage());
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        Log.w(LOG_TAG, "onStart() called");
        try {
            super.onStart();
            Intent i = getIntent();
            if (i != null) {
                handleIntent(i);
            } else {
                Log.d(LOG_TAG, "start intent was null");
            }

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
                Intent scannerIntent = new Intent(this, ScannerService.class);
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(scannerIntent);
                    } else {
                        startService(scannerIntent);
                    }
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "" + t.getMessage());
                }
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
        } catch (Throwable t) {
            Log.e(LOG_TAG, ""+t.getMessage());
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
        Preferences.getInstance(this).unregisterOnSharedPreferenceChangeListener(this);
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
        try {
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
        }catch(Throwable t){
            Log.e(LOG_TAG, ""+t.getMessage());
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
        if (intent != null) {
            Log.d(LOG_TAG, "start intent extras: " + intent.getExtras());
            if(checkTerninate(intent)) {
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
            }
        }
    }

    public void updateButtonImg(){
        if(mScannerService != null) {
            runOnUiThread(() -> {
                if(mFab != null) {
                    if (mScannerService.isScanning()) {
                        mFab.setImageResource(android.R.drawable.ic_media_pause);
                    }else{
                        mFab.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
            });
        }
    }

    public void newBeconEvent(String addr, final int sizeTotal, final int sizeENF, final int sizeSCF, final SignalStrengthGroupInfo[] ranges) {
        if(mScannerService != null){
            if(addr !=null) {
                Log.v(LOG_TAG, "newBeconEvent: " + mScannerService.mContainer.get(addr));
            }else{
                Log.v(LOG_TAG, "newBeconEvent: NO SCAN RESULTS");
            }
        }
        runOnUiThread(()-> setActiveBeaconCount(sizeTotal, sizeENF, sizeSCF, ranges));
    }

    private void setActiveBeaconCount(final int sizeTotal, final int sizeENF, final int sizeSCF, final SignalStrengthGroupInfo[] ranges){
        // setting the initial TEXT..
        if(mViewPager != null) {
            Fragment info = ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(0);
            if (info instanceof PlaceholderFragment) {
                if(mScannerService != null && mScannerService.mShowBtIsOffWarning){
                    ((PlaceholderFragment) info).setNoBluetoothInfoText(getString(R.string.act_enable_bt));
                }else {
                    String total;

                    // rendering TOTAL INFO BLOCK...
                    if (mShowTotal) {
                        total = String.format(getString(R.string.act_total_beacons), sizeTotal);
                    } else {
                        total = null;
                    }

                    // rendering the ExposureNotification and/or StopCovidFrance info
                    if (sizeENF > -1 && sizeSCF > -1) {
                        // dual mode...
                        ((PlaceholderFragment) info).setText(
                                total,
                                String.format(getString(R.string.act_active_enf_beacons), sizeENF),
                                String.format(getString(R.string.act_active_scf_beacons), sizeSCF)
                        );
                    } else if (sizeENF > -1) {
                        // default ExposureNotificationFramework mode
                        ((PlaceholderFragment) info).setText(
                                total,
                                String.format(getString(R.string.act_active_beacons), sizeENF),
                                null);
                    } else {
                        // StopCovid France mode
                        ((PlaceholderFragment) info).setText(total,
                                null,
                                String.format(getString(R.string.act_active_scf_beacons), sizeSCF));
                    }

                    // render the additional "ranges"
                    if(ranges != null){
                        switch (ranges.length){
                            default:
                                // default:
                                // DO NOTHING - unknown number of available fields...
                                ((PlaceholderFragment) info).setRangeInfo(null, null, null, null);
                                break;

                            case 2:
                                // ONLY GOOD/BAD => threshold is defined...
                                // we might want to show additionally the number of
                                // BAD signals?! - for now we skip this...
                                ((PlaceholderFragment) info).setRangeInfo(null, null, null, "BAD: "+ranges[1].size);
                                break;

                            case 3:
                                // NEAR|MEDIUM|FAR
                                ((PlaceholderFragment) info).setRangeInfo("NEAR: "+ranges[0].size, "MEDIUM: "+ranges[1].size, "FAR: "+ranges[2].size, null);
                                break;

                            case 4:
                                // NEAR|MEDIUM|FAR|BAD
                                ((PlaceholderFragment) info).setRangeInfo("NEAR: "+ranges[0].size, "MEDIUM: "+ranges[1].size, "FAR: "+ranges[2].size, "BAD: "+ranges[3].size);
                                break;
                        }
                    } else{
                        ((PlaceholderFragment) info).setRangeInfo(null, null, null, null);
                    }
                }
            }
        };
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String sTotalKey = getString(R.string.PKEY_SHOWTOTAL);
        if(key.equalsIgnoreCase(sTotalKey)){
            mShowTotal = sharedPreferences.getBoolean(sTotalKey, false);
        }
    }
}
