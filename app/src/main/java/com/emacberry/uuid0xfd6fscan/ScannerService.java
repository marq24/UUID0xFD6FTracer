package com.emacberry.uuid0xfd6fscan;

import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ScannerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String INTENT_EXTRA_START = "START_SCAN";
    protected static final String INTENT_EXTRA_STOP = "STOP_SCAN";
    protected static final String INTENT_EXTRA_STARTBT = "START_BT";
    protected static final String INTENT_EXTRA_STARTLOC = "START_LOC";
    protected static final String INTENT_EXTRA_STARTBOTH = "START_BOTH";

    // SERVICE STUFF:
    // http://stackoverflow.com/questions/9740593/android-create-service-that-runs-when-application-stops

    // Running as FourgroundService!!!
    // http://developer.android.com/guide/components/services.html#Foreground

    private static final String LOG_TAG = "SCANNER";
    public static boolean isRunning = false;

    public static final ParcelUuid FD6F_UUID = ParcelUuid.fromString("0000fd6f-0000-1000-8000-00805f9b34fb"); // ExposureNotificationFramework (like Germany)
    public static final ParcelUuid FD64_UUID = ParcelUuid.fromString("0000fd64-0000-1000-8000-00805f9b34fb"); // FRANCE

    //public static final ParcelUuid FD6X_UUID = ParcelUuid.fromString("0000fd60-0000-1000-8000-00805f9b34fb");
    //public static final ParcelUuid FD6X_MASK = ParcelUuid.fromString("11111110-1111-1111-1111-111111111111");

    // /* SERVICES */
    IBinder mBinder = new LocalBinder();

    private String PKEY_SCANMODE;
    private String PKEY_GROUPBYSIGSTRENGTH;
    private String PKEY_USETHRESHOLD;
    private String PKEY_GROUPNEARVAL;
    private String PKEY_GROUPMEDVAL;
    private String PKEY_THRESHOLDVAL;
    private String PKEY_FORCEGPS;

    private Preferences mPrefs;
    private String mPrefScanMode;
    private boolean mPrefGroupBySignalStrength;
    private String mPrefGroupNearValAsString;
    private String mPrefGroupMedValAsString;
    private boolean mPrefUseThreshold;
    private String mPrefThresholdValAsString;
    private boolean mPrefForceGps;

    private void initPrefs() {
        PKEY_SCANMODE = getString(R.string.PKEY_SCANMODE);
        PKEY_GROUPBYSIGSTRENGTH = getString(R.string.PKEY_GROUPBYSIGSTRENGTH);
        PKEY_USETHRESHOLD = getString(R.string.PKEY_USETHRESHOLD);
        PKEY_GROUPNEARVAL = getString(R.string.PKEY_GROUPNEARVAL);
        PKEY_GROUPMEDVAL = getString(R.string.PKEY_GROUPMEDVAL);
        PKEY_THRESHOLDVAL = getString(R.string.PKEY_THRESHOLDVAL);
        PKEY_FORCEGPS = getString(R.string.PKEY_FORCEGPS);

        mPrefs = Preferences.getInstance(getBaseContext());
        mPrefScanMode = mPrefs.getString(PKEY_SCANMODE, R.string.DVAL_SCANMODE);
        mPrefGroupBySignalStrength = mPrefs.getBoolean(PKEY_GROUPBYSIGSTRENGTH, R.string.DVAL_GROUPBYSIGSTRENGTH);
        mPrefGroupNearValAsString = mPrefs.getString(PKEY_GROUPNEARVAL, R.string.DVAL_GROUPNEARVAL);
        mPrefGroupMedValAsString = mPrefs.getString(PKEY_GROUPMEDVAL, R.string.DVAL_GROUPMEDVAL);
        mPrefUseThreshold = mPrefs.getBoolean(PKEY_USETHRESHOLD, R.string.DVAL_USETHRESHOLD);
        mPrefThresholdValAsString = mPrefs.getString(PKEY_THRESHOLDVAL, R.string.DVAL_THRESHOLDVAL);
        mPrefForceGps = mPrefs.getBoolean(PKEY_FORCEGPS, R.string.DVAL_FORCEGPS);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        /*switch (key){
            case sPKEY_SCANMODE:
                mPrefScanMode = checkStringPref(sPKEY_SCANMODE, mPrefScanMode, prefs);
                break;

            default:
                break;
        }*/
        if (key.equals(PKEY_SCANMODE)) {
            mPrefScanMode = checkStringPref(prefs, PKEY_SCANMODE, mPrefScanMode);
        } else if (key.equals(PKEY_GROUPBYSIGSTRENGTH)) {
            mPrefGroupBySignalStrength = checkBooleanPref(prefs, PKEY_GROUPBYSIGSTRENGTH, mPrefGroupBySignalStrength);
        } else if (key.equals(PKEY_USETHRESHOLD)) {
            mPrefUseThreshold = checkBooleanPref(prefs, PKEY_USETHRESHOLD, mPrefUseThreshold);
        } else if (key.equals(PKEY_GROUPNEARVAL)) {
            mPrefGroupNearValAsString = checkStringPref(prefs, PKEY_GROUPNEARVAL, mPrefGroupNearValAsString);
        } else if (key.equals(PKEY_GROUPMEDVAL)) {
            mPrefGroupMedValAsString = checkStringPref(prefs, PKEY_GROUPMEDVAL, mPrefGroupMedValAsString);
        } else if (key.equals(PKEY_THRESHOLDVAL)) {
            mPrefThresholdValAsString = checkStringPref(prefs, PKEY_THRESHOLDVAL, mPrefThresholdValAsString);
        } else if (key.equals(PKEY_FORCEGPS)) {
            mPrefForceGps = checkBooleanPref(prefs, PKEY_FORCEGPS, mPrefForceGps);
        }
    }

    private String checkStringPref(SharedPreferences prefs, String pKey, String curValue) {
        String newValue = prefs.getString(pKey, null);
        if (curValue == null || !curValue.equals(newValue)) {
            triggerRestartScanCauseOfPrefChange();
            return newValue;
        } else {
            return curValue;
        }
    }

    private boolean checkBooleanPref(SharedPreferences prefs, String pKey, boolean curValue) {
        boolean newValue = prefs.getBoolean(pKey, false);
        if (curValue != newValue) {
            triggerRestartScanCauseOfPrefChange();
            return newValue;
        } else {
            return curValue;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind called " + intent);
        if (mBinder == null) {
            mBinder = new LocalBinder();
        }
        return mBinder;//
    }

    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind called " + intent);
        return super.onUnbind(intent);
    }

    public void onRebind(Intent intent) {
        Log.i(LOG_TAG, "onRebind called " + intent);
        super.onRebind(intent);
    }

    public class LocalBinder extends Binder {
        public ScannerService getServerInstance() {
            return ScannerService.this;
        }
    }

    public void setGuiCallback(ScannerActivity mainActivity) {
        mGuiCallback = mainActivity;
        mScanCallback.mDoReport = true;
        if (mainActivity != null) {
            // activity connected...
            if (!mScannIsRunning && !mScannStopedViaGui) {
                ensureAdapterAndScannerInit();
                startScan(true);
            }
        }
    }

    private BroadcastReceiver mScreenOnOffReceiver;
    private BroadcastReceiver mBluetoothStateReceiver;
    private BroadcastReceiver mLocationProviderStateReceiver;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private MyScanCallback mScanCallback = new ScannerService.MyScanCallback();
    private Handler mHandler = new Handler();
    private ScannerActivity mGuiCallback = null;
    public boolean mShowBtIsOffWarning = false;

    private boolean isAirplaneMode() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private LocationManager mLocationManager;
    public boolean isLocationProviderEnabled() {
        try {
            if(mLocationManager == null){
                mLocationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE );
            }
            boolean loc_enabled = false;
            try {
                loc_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!mPrefForceGps) {
                    if (!loc_enabled) {
                        loc_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    }
                    if (!loc_enabled) {
                        loc_enabled = mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                    }
                    if (!loc_enabled) {
                        loc_enabled = mLocationManager.isProviderEnabled("fused");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace() ;
            }
            return loc_enabled;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (isRunning && intent != null) {
            handleIntentInt(intent);
            return START_STICKY;
        } else {
            super.onStartCommand(intent, flags, startId);
            Log.w(LOG_TAG, "onStartCommand() start");
            if (intent != null) {
                if (intent.getBooleanExtra(ScannerActivity.INTENT_EXTRA_AUTOSTART, false)) {
                    Log.d(LOG_TAG, "Launched by AUTOSTART");
                }
            }
            try {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Log.d(LOG_TAG, "initBtLE() started...");
                ensureAdapterAndScannerInit();

                startScreenStateObserver();

                // BT ON/OFF OBSERVER...
                startDeviceBluetoothStatusObserver();

                // LocationService ON/OFF Observer
                startLocationProviderStatusObserver();

            } catch (SecurityException s) {
                Log.d(LOG_TAG, "", s);
                // TODO: check permissions!!!!
                /*if (_logger != null) {
                    _logger.checkPermissions("location");
                }*/
            } catch (Exception e) {
                Log.d(LOG_TAG, "" + e.getMessage());
            } catch (Throwable t) {
                Log.d(LOG_TAG, "" + t.getMessage());
            }

            // register for PrefChanges...
            //_prefs.registerOnSharedPreferenceChangeListener(this);

            isRunning = true;
            showNotification();
            Log.w(LOG_TAG, "onStartCommand() completed");

            // when the service is started we should check if the
            // scanner is running
            if (mHandler != null) {
                checkForScanStart(15000);
            }
            return START_STICKY;
        }
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            initPrefs();
            mPrefs.registerOnSharedPreferenceChangeListener(this);
            if (mHandler == null) {
                mHandler = new Handler();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        Log.w(LOG_TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        Log.w(LOG_TAG, "Service.onDestroy() called - shutting down Scanner");
        if (mScanChecker != null) {
            try {
                mScanChecker.cancel();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        ensureAdapterAndScannerClosed();
        unregisterBroadcastReceiver(mScreenOnOffReceiver);
        unregisterBroadcastReceiver(mBluetoothStateReceiver);
        unregisterBroadcastReceiver(mLocationProviderStateReceiver);
        mHandler = null;

        Preferences.getInstance(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        isRunning = false;
        stopForeground(true);
        super.onDestroy();
        Log.w(LOG_TAG, "Service destroyed!!! (fine)");
    }

    private void unregisterBroadcastReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void handleIntentInt(@Nullable Intent intent) {
        Log.d(LOG_TAG, "start intent extras: " + intent.getExtras());
        if (intent.hasExtra(INTENT_EXTRA_START)) {
            startScan(true);
        }

        if (intent.hasExtra(INTENT_EXTRA_STOP)) {
            stopScan(true);
        }

        if (intent.hasExtra(INTENT_EXTRA_STARTBT) || intent.hasExtra(INTENT_EXTRA_STARTBOTH)) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.enable();
            }
        }

        if (intent.hasExtra(INTENT_EXTRA_STARTLOC) || intent.hasExtra(INTENT_EXTRA_STARTBOTH)) {
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    public boolean isScanning() {
        return mScannIsRunning;
    }

    private void ensureAdapterAndScannerClosed() {
        try {
            if (mBluetoothAdapter != null) {
                if (mBluetoothLeScanner != null && mScanCallback != null) {
                    try {
                        mBluetoothLeScanner.stopScan(mScanCallback);
                    } catch (Throwable t) {
                        Log.d(LOG_TAG, "" + t.getMessage());
                    }
                }
                try {
                    mBluetoothAdapter.cancelDiscovery();
                } catch (Throwable t) {
                    Log.d(LOG_TAG, "" + t.getMessage());
                }
            }
            // make sure that nobody can use the mBluetoothAdapter anylonger
            mBluetoothAdapter = null;
        } catch (Throwable t) {
            Log.d(LOG_TAG, "" + t.getMessage());
        }
    }

    private void ensureAdapterAndScannerInit() {
        if (mBluetoothAdapter == null) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter != null) {
            mShowBtIsOffWarning = !mBluetoothAdapter.isEnabled();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public void stopScan(boolean viaGui) {
        if (mScannIsRunning) {
            if (viaGui) {
                mScannStopedViaGui = true;
            }
            mScannResultsOnStart = false;
            mContainer = new HashMap<>();
            if (mBluetoothLeScanner != null) {
                Log.d(LOG_TAG, "mBluetoothLeScanner.stopScan() called");
                try {
                    mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
                } catch (IllegalStateException ise) {
                    if (ise.getMessage().equalsIgnoreCase("bt adapter is not turned on")) {
                        Log.d(LOG_TAG, "BluetoothAdapter is OFF - all fine");
                    } else {
                        ise.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    try {
                        mBluetoothLeScanner.stopScan(mScanCallback);
                    } catch (IllegalStateException ise) {
                        if (ise.getMessage().equalsIgnoreCase("bt adapter is not turned on")) {
                            Log.d(LOG_TAG, "BluetoothAdapter is OFF - all fine");
                        } else {
                            ise.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mScannIsRunning = false;
                    updateNotification();
                    if (mGuiCallback != null) {
                        mGuiCallback.updateButtonImg();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean mScannIsRunning = false;
    private boolean mHasScanPermission = false;

    public void startScan(boolean viaGui) {
        if(isLocationProviderEnabled()) {
            if (viaGui) {
                mScannStopedViaGui = false;
            }
            if (checkScanPermissions()) {
                if (mBluetoothLeScanner != null && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && !mScannIsRunning) {
                    Log.d(LOG_TAG, "mBluetoothLeScanner.startScan() called");
                    ensureScanModeSet();
                    ArrayList<ScanFilter> f = new ArrayList<>();
                    switch (mPrefScanMode) {
                        case "ENF_FRA":
                            //f.add(new ScanFilter.Builder().setServiceUuid(FD6X_UUID, FD6X_MASK).build());
                            f.add(new ScanFilter.Builder().setServiceUuid(FD64_UUID).build());
                            f.add(new ScanFilter.Builder().setServiceUuid(FD6F_UUID).build());
                            mScanCallback.setScanUUID(null);
                            break;

                        case "FRA":
                            f.add(new ScanFilter.Builder().setServiceUuid(FD64_UUID).build());
                            mScanCallback.setScanUUID(FD64_UUID);
                            break;

                        default:
                        case "ENF":
                            f.add(new ScanFilter.Builder().setServiceUuid(FD6F_UUID).build());
                            mScanCallback.setScanUUID(FD6F_UUID);
                            break;
                    }
                    mContainer = new HashMap<>();

                    /*
                    Near: -infinity < attenuation ≤ 55 dB
                    Medium: 55 dB < attenuation ≤ 63 dB
                    Far: 63 dB < attenuation ≤ 73 dB
                    Bad: 73 dB < attenuation

                    If we apply average corrections from the table, we get RSSI boundaries at
                    -100 dB, -90 dB, -82 dB. But since for practical purposes -100 dB is the bottom of
                    the scale, I would recommend either not providing a "bad" bucket (including it in
                    the far one), or using something between -97 and -99 for this bucket.
                    */

                    if (!mPrefGroupBySignalStrength) {
                        if (!mPrefUseThreshold) {
                            mSignalStrengthGroup = null;
                        } else {
                            int thresholdVal = Integer.parseInt(mPrefThresholdValAsString);
                            mSignalStrengthGroup = new SignalStrengthCollection();
                            mSignalStrengthGroup.put(new RssiRange(RssiRangeType.BAD, 1000, thresholdVal), 0);
                            mSignalStrengthGroup.put(new RssiRange(RssiRangeType.GOOD, thresholdVal, 0), 0);
                        }
                    } else {
                        int goodEnd = Integer.parseInt(mPrefGroupNearValAsString);
                        int medEnd = Integer.parseInt(mPrefGroupMedValAsString);
                        // have in mind the values (in the prefs) are still "positive" so the comparision
                        // is reverted
                        if (goodEnd >= medEnd) {
                            // ok we need to use some fallback since the usr has specified false values...
                            mPrefGroupNearValAsString = getString(R.string.DVAL_GROUPNEARVAL);
                            mPrefGroupMedValAsString = getString(R.string.DVAL_GROUPMEDVAL);
                            goodEnd = Integer.parseInt(mPrefGroupNearValAsString);
                            medEnd = Integer.parseInt(mPrefGroupMedValAsString);
                        }
                        mSignalStrengthGroup = new SignalStrengthCollection();
                        if (!mPrefUseThreshold) {
                            mSignalStrengthGroup.put(new RssiRange(RssiRangeType.FAR, 1000, medEnd), 0);
                        } else {
                            int thresholdVal = Integer.parseInt(mPrefThresholdValAsString);
                            // have in mind the values (in the prefs) are still "positive" so the comparision
                            // is revered
                            if (medEnd >= thresholdVal) {
                                mPrefThresholdValAsString = getString(R.string.DVAL_THRESHOLDVAL);
                                thresholdVal = Integer.parseInt(mPrefThresholdValAsString);
                            }
                            mSignalStrengthGroup.put(new RssiRange(RssiRangeType.BAD, 1000, thresholdVal), 0);
                            mSignalStrengthGroup.put(new RssiRange(RssiRangeType.FAR, thresholdVal, medEnd), 0);
                        }
                        mSignalStrengthGroup.put(new RssiRange(RssiRangeType.MEDIUM, medEnd, goodEnd), 0);
                        mSignalStrengthGroup.put(new RssiRange(RssiRangeType.NEAR, goodEnd, 0), 0);
                    }
                    mBluetoothLeScanner.startScan(f, generateScanSettings().build(), mScanCallback);
                    mScannIsRunning = true;
                    updateNotification();
                    if (mGuiCallback != null) {
                        mGuiCallback.updateButtonImg();
                    }
                }
                // check after 1min if we have a scan result..
                checkForScanStart(60000);
            } else {
                // no permission...? start activity and request START again
                updateNotification();
            }
        }else{
            Log.w(LOG_TAG, "Scanner was not started cause GPS ist OFF");
        }
    }

    private ScanSettings.Builder generateScanSettings() {
        // added after reviewing https://github.com/google/exposure-notifications-internals
        // com.google.samples.exposurenotification.ble.scanner.BleScannerImpl
        // "cool" that the new "we don't want to tell you more details" is now called just
        // "FIXME!" - you just SUCKS! -> see
        // https://github.com/google/exposure-notifications-internals/blob/8f751a666697c3cae0a56ae3464c2c6cbe31b69e/exposurenotification/src/main/java/com/google/samples/exposurenotification/ble/scanner/BleScannerImpl.java#L231

        ScanSettings.Builder ssb = new ScanSettings.Builder();
        /* DEFAULT VALUES:
        mScanMode = SCAN_MODE_LOW_POWER;
        mCallbackType = CALLBACK_TYPE_ALL_MATCHES;
        mScanResultType = SCAN_RESULT_TYPE_FULL;
        mReportDelayMillis = 0;
        mMatchMode = MATCH_MODE_AGGRESSIVE;
        mNumOfMatchesPerFilter = MATCH_NUM_MAX_ADVERTISEMENT;
        mLegacy = true;
        mPhy = PHY_LE_ALL_SUPPORTED;
        */

        /****************************
         * SCAN_MODE
         ****************************/
        /**
         * A special Bluetooth LE scan mode. Applications using this scan mode will passively listen for
         * other scan results without starting BLE scans themselves.
         */
        //public static final int SCAN_MODE_OPPORTUNISTIC = -1;

        /**
         * Perform Bluetooth LE scan in low power mode. This is the default scan mode as it consumes the
         * least power. This mode is enforced if the scanning application is not in foreground.
         */
        //public static final int SCAN_MODE_LOW_POWER = 0;

        /**
         * Perform Bluetooth LE scan in balanced power mode. Scan results are returned at a rate that
         * provides a good trade-off between scan frequency and power consumption.
         */
        //public static final int SCAN_MODE_BALANCED = 1;

        /**
         * Scan using highest duty cycle. It's recommended to only use this mode when the application is
         * running in the foreground.
         */
        //public static final int SCAN_MODE_LOW_LATENCY = 2;
        ssb.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            /****************************
             * CALLBACK_TYPE
             ****************************/
            /**
             * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
             * If no filter is active, all advertisement packets are reported.
             */
            //public static final int CALLBACK_TYPE_ALL_MATCHES = 1;

            /**
             * A result callback is only triggered for the first advertisement packet received that matches
             * the filter criteria.
             */
            //public static final int CALLBACK_TYPE_FIRST_MATCH = 2;

            /**
             * Receive a callback when advertisements are no longer received from a device that has been
             * previously reported by a first match callback.
             */
            //public static final int CALLBACK_TYPE_MATCH_LOST = 4;
            ssb.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);


            /****************************
             * MATCH_MODE
             ****************************/
            /**
             * In Aggressive mode, hw will determine a match sooner even with feeble signal strength
             * and few number of sightings/match in a duration.
             */
            //public static final int MATCH_MODE_AGGRESSIVE = 1;

            /**
             * For sticky mode, higher threshold of signal strength and sightings is required
             * before reporting by hw
             */
            //public static final int MATCH_MODE_STICKY = 2;
            ssb.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);


            /****************************
             * MATCH_NUM
             ****************************/
            /**
             * Determines how many advertisements to match per filter, as this is scarce hw resource
             */
            /**
             * Match one advertisement per filter
             */
            //public static final int MATCH_NUM_ONE_ADVERTISEMENT = 1;

            /**
             * Match few advertisement per filter, depends on current capability and availibility of
             * the resources in hw
             */
            //public static final int MATCH_NUM_FEW_ADVERTISEMENT = 2;

            /**
             * Match as many advertisement per filter as hw could allow, depends on current
             * capability and availibility of the resources in hw
             */
            //public static final int MATCH_NUM_MAX_ADVERTISEMENT = 3;
            ssb.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);


            /****************************
             * SCAN_RESULT_TYPE [NOT POSSIBLE]
             ****************************/
            /**
             * Request full scan results which contain the device, rssi, advertising data, scan response
             * as well as the scan timestamp.
             *
             * @hide
             */
            //@SystemApi
            //public static final int SCAN_RESULT_TYPE_FULL = 0;

            /**
             * Request abbreviated scan results which contain the device, rssi and scan timestamp.
             * <p>
             * <b>Note:</b> It is possible for an application to get more scan results than it asked for, if
             * there are multiple apps using this type.
             *
             * @hide
             */
            //@SystemApi
            //public static final int SCAN_RESULT_TYPE_ABBREVIATED = 1;
            //ssb.setScanResultType(ScanSettings.SCAN_RESULT_TYPE_FULL);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /**
                 * Set whether only legacy advertisments should be returned in scan results.
                 * Legacy advertisements include advertisements as specified by the
                 * Bluetooth core specification 4.2 and below. This is true by default
                 * for compatibility with older apps.
                 *
                 * @param legacy true if only legacy advertisements will be returned
                 */
                ssb.setLegacy(false);
            }
        }

        return ssb;
    }

    private void ensureScanModeSet() {
        // for what ever reason the init of the Prefs was not successful?!
        if (mPrefScanMode == null) {
            try {
                mPrefScanMode = Preferences.getInstance(getApplicationContext()).getString(R.string.PKEY_SCANMODE, R.string.DVAL_SCANMODE);
            } catch (Throwable t) {
                Log.d(LOG_TAG, "" + t.getMessage());
            } finally {
                if (mPrefScanMode == null) {
                    mPrefScanMode = "ENF";
                }
            }
        }
    }

    private AppOpsManager mAppOps;

    private boolean checkScanPermissions() {
        if (mAppOps == null) {
            mAppOps = getSystemService(AppOpsManager.class);
        }
        if (checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isAppOppAllowed(mAppOps, AppOpsManager.OPSTR_FINE_LOCATION, BuildConfig.APPLICATION_ID)) {
            mHasScanPermission = true;
            return true;
        } else {
            // https://android.googlesource.com/platform/packages/apps/Bluetooth/+/master/src/com/android/bluetooth/Utils.java
            // sometimes the isAppOppAllowed(...) (copied from the com.android.bluetooth.Utils)
            // will return false, cause it will return 'AppOpsManager.MODE_IGNORED'
            // -> in this case we should not call the scanner start we have to find a way
            // to launch the activity and request 'scanStart' from there... (I love Android!)
            mAppOps = null;
            mHasScanPermission = false;
            return false;
        }
    }

    private static boolean isAppOppAllowed(AppOpsManager appOps, String op, String callingPackage) {
        return appOps.noteOp(op, Binder.getCallingUid(), callingPackage) == AppOpsManager.MODE_ALLOWED;
    }

    private boolean mScannResultsOnStart = false;
    private boolean mScannStopedViaGui = false;

    private ScanChecker mScanChecker;

    public synchronized void checkForScanStart(long delayInMs) {
        if (mScanChecker != null) {
            mScanChecker.reset(delayInMs);
        } else {
            mScanChecker = new ScanChecker(delayInMs);
            mScanChecker.start();
        }
    }

    private class ScanChecker extends Thread {
        private long iStartTime;
        private boolean iCanceled = false;

        public ScanChecker(long delayInMs) {
            iStartTime = System.currentTimeMillis() + delayInMs;
        }

        private synchronized void reset(long delayInMs) {
            long newStartTime = System.currentTimeMillis() + delayInMs;
            if (newStartTime < iStartTime) {
                Log.d(LOG_TAG, "update 'checkForScanStart()' to " + (delayInMs / 1000) + "s");
                iStartTime = newStartTime;
                interrupt();
            } else {
                Log.d(LOG_TAG, "skip set new start time 'checkForScanStart()' to " + (delayInMs / 1000) + "s");
            }
        }

        public void run() {
            long now = System.currentTimeMillis();
            while (!iCanceled && iStartTime - now > 0) {
                long sleepTime = iStartTime - now;
                Log.d(LOG_TAG, "calling checkForScanStart() in " + (sleepTime / 1000) + "s");
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                }
                now = System.currentTimeMillis();
            }
            if (!iCanceled) {
                Log.d(LOG_TAG, "calling checkForScanStart() NOW");
                checkForScanStartInt();
            } else {
                Log.d(LOG_TAG, "calling checkForScanStart() CANCELED - fine");
            }
            mScanChecker = null;
        }

        public void cancel() {
            iCanceled = true;
            interrupt();
        }

        private void checkForScanStartInt() {
            if (!mScannStopedViaGui) {
                if (mHandler != null) {
                    if (mScannIsRunning && !mScannResultsOnStart) {
                        Log.i(LOG_TAG, "checkForScannStart() triggered - mScannIsRunning: TRUE");
                        mHandler.postDelayed(() -> stopScan(false), 500);
                        mHandler.postDelayed(() -> startScan(false), 5000);
                    } else if (!mScannIsRunning) {
                        Log.i(LOG_TAG, "checkForScannStart() triggered - mScannIsRunning: FALSE");
                        mHandler.postDelayed(() -> startScan(false), 500);
                    } else {
                        Log.i(LOG_TAG, "checkForScannStart() triggered - NOTHING TO DO");
                    }
                }
            }
        }
    }

    private ScanRestarter mScanRestarter = null;

    private void triggerRestartScanCauseOfPrefChange() {
        if (mScanRestarter == null) {
            mScanRestarter = new ScanRestarter();
            mScanRestarter.start();
        }
        if (mScanRestarter != null) {
            mScanRestarter.iStartTime = System.currentTimeMillis();
        }
    }

    private class ScanRestarter extends Thread {
        private long iStartTime = System.currentTimeMillis();

        public void run() {
            while (iStartTime + 5000 > System.currentTimeMillis()) {
                try {
                    sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(LOG_TAG, "trigger scan restart cause of preferences changes");
            restartScan();
            mScanRestarter = null;
        }

        private void restartScan() {
            if (!mScannStopedViaGui) {
                if (mHandler != null) {
                    if (mScannIsRunning && mScannResultsOnStart) {
                        Log.w(LOG_TAG, "restartScan() triggered - mScannIsRunning: TRUE");
                        mHandler.postDelayed(() -> stopScan(false), 500);
                        mHandler.postDelayed(() -> startScan(false), 5000);
                    } else if (!mScannIsRunning) {
                        Log.w(LOG_TAG, "restartScan() triggered - mScannIsRunning: FALSE");
                        mHandler.postDelayed(() -> startScan(false), 500);
                    }
                }
            }
        }
    }

    private CharSequence mNotifyTextScanning;
    private String mNotifyTextAddon;
    private NotificationCompat.Builder mBuilder;

    private NotificationCompat.Builder getNotificationBuilder() {
        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html
        if (mNotifyTextScanning == null) {
            mNotifyTextScanning = getText(R.string.app_service_msgScan1);
            mNotifyTextAddon = getString(R.string.app_service_msgScan2);
        }

        String channelId = NotificationHelper.getBaseNotificationChannelId(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(getText(R.string.app_service_title));

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            builder.setSmallIcon(R.drawable.ic_app_notify72);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ScannerActivity.INTENT_EXTRA_SERVICE_ACTION, true);
        builder.setContentIntent(PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT));

        boolean mShowLocWarn = !isLocationProviderEnabled();

        if (!mShowBtIsOffWarning && !mShowLocWarn) {
            if (mScannIsRunning) {
                builder.setContentText(mNotifyTextScanning);
                builder.addAction(-1, this.getString(R.string.menu_stop_notify_action), getServiceIntent(INTENT_EXTRA_STOP));
            } else {
                if (checkScanPermissions() && mHasScanPermission) {
                    builder.setContentText(getString(R.string.app_service_msgOff));
                    // Check 'start scan' from notification action can cause no scan results after
                    //  the start / this will not happen, if scan is triggered via Activity (no clue yet why)
                    // logcat reports:
                    // E/BluetoothUtils: Permission denial: Need ACCESS_FINE_LOCATION permission to get scan results
                    //
                    // wtf?!
                    builder.addAction(-1, this.getString(R.string.menu_start_notify_action), getServiceIntent(INTENT_EXTRA_START));
                } else {
                    builder.setContentText(getText(R.string.app_service_msgOffNoPermissions));
                }
            }
        }else if(mShowBtIsOffWarning && mShowLocWarn){
            builder.setContentText(getString(R.string.app_service_msgNoBoth));
            builder.addAction(-1, this.getString(R.string.menu_start_both_action), getServiceIntent(INTENT_EXTRA_STARTBOTH));
        }else if(mShowBtIsOffWarning){
            builder.setContentText(getString(R.string.app_service_msgNoBt));
            builder.addAction(-1, this.getString(R.string.menu_start_bt_action), getServiceIntent(INTENT_EXTRA_STARTBT));
        }else if(mShowLocWarn){
            builder.setContentText(getString(R.string.app_service_msgNoLocation));
            builder.addAction(-1, this.getString(R.string.menu_start_location_action), getServiceIntent(INTENT_EXTRA_STARTLOC));
        }

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(ScannerActivity.INTENT_EXTRA_TERMINATE_APP));
        } else {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px_api20, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(ScannerActivity.INTENT_EXTRA_TERMINATE_APP));
        }
        builder.setColor(ContextCompat.getColor(this, R.color.notification_action));
        return builder;
    }

    private void showNotification() {
        try {
            mBuilder = getNotificationBuilder();
            startForeground(R.id.notify_backservive, mBuilder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private PendingIntent getAppIntent(String extra) {
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        return PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent getServiceIntent(String extra) {
        Intent intent = new Intent(this, ScannerService.class);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        return PendingIntent.getService(this, intent.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getTerminateAppIntent(String extra) {
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        return PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private KeyguardManager mKeyguardManager = null;
    private NotificationManagerCompat mNotificationManager = null;
    private boolean mNotifyCanBeReset = false;

    public void updateNotification() {
        try {
            mBuilder = getNotificationBuilder();
            if (mNotificationManager == null) {
                mNotificationManager = NotificationManagerCompat.from(this);
            }
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mBuilder.setShowWhen(true);
            mBuilder.setWhen(System.currentTimeMillis());
            mNotificationManager.notify(R.id.notify_backservive, mBuilder.build());
        } catch (Throwable t) {
            Log.d(LOG_TAG, "" + t.getMessage());
        }
    }

    private void updateNotificationText(boolean force, int size) {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        }
        boolean notify = false;
        if (mBuilder != null) {
            boolean mShowLocWarn = !isLocationProviderEnabled();
            if (!mShowBtIsOffWarning && !mShowLocWarn) {
                // if we have not any information about the current size that we should
                // display in the notification text we need to fetch it again...
                if (size == -1) {
                    if (mPrefGroupBySignalStrength && mSignalStrengthGroup != null) {
                        size = mSignalStrengthGroup.iGoodCount;
                    } else {
                        size = mContainer.size();
                    }
                }
                if (mScannIsRunning && size > 0) {
                    String txt = String.format(mNotifyTextAddon, size) + " " + mNotifyTextScanning;
                    mBuilder.setContentText(txt);
                    notify = true;//force || !mKeyguardManager.isKeyguardLocked();
                    mNotifyCanBeReset = true;
                } else {
                    if (mNotifyCanBeReset) {
                        mNotifyCanBeReset = false;
                        if (mScannIsRunning) {
                            mBuilder.setContentText(mNotifyTextScanning);
                        } else {
                            if (mHasScanPermission) {
                                mBuilder.setContentText(getText(R.string.app_service_msgOff));
                            } else {
                                mBuilder.setContentText(getText(R.string.app_service_msgOffNoPermissions));
                            }
                        }
                        mBuilder.setStyle(null);
                        notify = true;
                    }
                }
            }else if(mShowBtIsOffWarning && mShowLocWarn){
                mBuilder.setContentText(getText(R.string.app_service_msgNoBoth));
                notify = true;
            }else if (mShowBtIsOffWarning) {
                mBuilder.setContentText(getText(R.string.app_service_msgNoBt));
                notify = true;
            } else if(mShowLocWarn) {
                mBuilder.setContentText(getText(R.string.app_service_msgNoLocation));
                notify = true;
            }
        }

        if (notify) {
            if (mNotificationManager == null) {
                mNotificationManager = NotificationManagerCompat.from(this);
            }
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mBuilder.setShowWhen(true);
            mBuilder.setWhen(System.currentTimeMillis());
            mNotificationManager.notify(R.id.notify_backservive, mBuilder.build());
        }
    }

    /*private void trace() {
        Log.w(LOG_TAG, "------------------------------------");
        Log.w(LOG_TAG, "Update Notification... -> ");
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement t : trace) {
            Log.w(LOG_TAG, t.toString());
        }
    }*/

    protected HashMap<String, UUIDFD6FBeacon> mContainer = new HashMap<>();
    protected SignalStrengthCollection mSignalStrengthGroup = null;
    private int mTotalSize = 0;

    private class MySimpleTimer extends Thread {
        private volatile long iLastScanEvent = 0;
        private volatile boolean iIsTimeoutCheckerRunning = false;
        private MyScanCallback iCallback = null;

        public MySimpleTimer(MyScanCallback myScanCallback) {
            super("MySimpleTimer");
            this.iCallback = myScanCallback;
        }

        public void run() {
            while (iIsTimeoutCheckerRunning) {
                try {
                    long delay = 35000L;
                    if (iLastScanEvent > 0) {
                        delay = (iLastScanEvent + delay) - System.currentTimeMillis();
                        if (delay < 0) {
                            delay = 35000L;
                        }
                    }
                    if (BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "MySimpleTimer sleep " + (delay / 1000) + "s");
                    }
                    sleep(delay);
                    if (BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "MySimpleTimer wake up");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long tsNow = System.currentTimeMillis();
                if (iLastScanEvent + 31000 < tsNow) {
                    if (BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "last scan event longer then 31sec ago...");
                    }
                    // we have not received since 30 seconds a new scan
                    // result, then we need to invalidate our content...
                    // (BeaconsInRange = 0)
                    iCallback.checkForOutdatedBeaconsAfterTimeout(tsNow);
                    iIsTimeoutCheckerRunning = false;
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "last scan event shorter then 31sec ago all fine");
                    }
                }
            }
            if (BuildConfig.DEBUG) {
                Log.v(LOG_TAG, "MySimpleTimer ended");
            }
        }
    }

    public SignalStrengthGroupInfo[] getSignalStrengthGroupInfo() {
        if (mSignalStrengthGroup != null) {
            return mSignalStrengthGroup.getGroupSizeInfo();
        }
        return null;
    }

    public int[] getBeaconCountByType() {
        ensureScanModeSet();
        switch (mPrefScanMode) {
            default:
            case "ENF":
                if (mSignalStrengthGroup != null) {
                    return new int[]{mTotalSize, mSignalStrengthGroup.iGoodCount, -1};
                } else {
                    return new int[]{mTotalSize, mContainer.size(), -1};
                }

            case "FRA":
                if (mSignalStrengthGroup != null) {
                    return new int[]{mTotalSize, -1, mSignalStrengthGroup.iGoodCount};
                } else {
                    return new int[]{mTotalSize, -1, mContainer.size()};
                }

            case "ENF_FRA":
                return getBeaconCountSplitByType();
        }
    }

    private int[] getBeaconCountSplitByType() {
        int sizeENF = 0;
        int sizeSCF = 0;
        // grrrr we might need to filter here again by the signalStrength in order to get a valid
        // total count of beacons per type
        int thresholdFilterValue = -1000;
        if (mPrefUseThreshold) {
            thresholdFilterValue = -1 * Integer.parseInt(mPrefThresholdValAsString);
        }
        synchronized (mContainer) {
            for (UUIDFD6FBeacon b : mContainer.values()) {
                if (acceptSignalStrength(b, thresholdFilterValue)) {
                    //int neededToBeValidStatement = b.isENF ? sizeENF++ : sizeSCF++;
                    if (b.isENF) {
                        sizeENF++;
                    } else {
                        sizeSCF++;
                    }
                }
            }
        }
        return new int[]{mTotalSize, sizeENF, sizeSCF};
    }

    private boolean acceptSignalStrength(UUIDFD6FBeacon beacon, int thresholdFilterValue) {
        return thresholdFilterValue == -1000 || beacon.mLatestSignalStrength >= thresholdFilterValue;
    }

    private class RssiRange implements Comparable {
        public RssiRangeType type;
        int minValue;
        int maxValue;

        public RssiRange(RssiRangeType type, int min, int max) {
            this.type = type;
            this.minValue = -min;
            this.maxValue = -max;
        }

        @Override
        public int hashCode() {
            return minValue + maxValue;
        }

        @Override
        public int compareTo(Object obj) {
            if (obj instanceof RssiRange) {
                RssiRange r = (RssiRange) obj;
                if (r.maxValue < maxValue) {
                    return -1;
                } else if (r.maxValue == maxValue) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return 0;
        }

        @NonNull
        @Override
        public String toString() {
            return type.toString() + "[" + maxValue + "db|" + minValue + "db]";
        }
    }

    private class SignalStrengthCollection extends TreeMap<RssiRange, Integer> {
        private int iGoodCount = 0;

        public Integer[] sizes() {
            Integer[] ret = new Integer[values().size()];
            super.values().toArray(ret);
            return ret;
        }

        public SignalStrengthGroupInfo[] getGroupSizeInfo() {
            SignalStrengthGroupInfo[] ret = new SignalStrengthGroupInfo[size()];
            int i = 0;
            for (RssiRange aRange : keySet()) {
                ret[i++] = new SignalStrengthGroupInfo(aRange.type, get(aRange).intValue());
            }
            return ret;
        }

        public void add(UUIDFD6FBeacon beacon) {
            int rssi = beacon.mLatestSignalStrength;
            RssiRange range = getRangeForValue(rssi);
            if (range != null) {
                if (range.type.value > -1) {
                    iGoodCount++;
                }
                put(range, get(range).intValue() + 1);
            }
            if (BuildConfig.DEBUG) {
                Log.v(LOG_TAG, beacon.addr + " " + rssi + "db " + range);
            }
        }

        private RssiRange getRangeForValue(final int rssi) {
            for (RssiRange range : keySet()) {
                if (range.maxValue > rssi && rssi >= range.minValue) {
                    return range;
                }
            }
            return null;
        }

        public void clearCounts() {
            iGoodCount = 0;
            for (RssiRange range : keySet()) {
                put(range, 0);
            }
        }
    }

    private class MyScanCallback extends ScanCallback {
        private long iLastContainerCheckTs = 0;
        public boolean mDoReport = false;
        public boolean mDisplayIsOn = true;
        private long iLastTs = 0;
        private MySimpleTimer iTimoutTimer = null;
        private ParcelUuid mScanUUID = FD6F_UUID;
        private boolean mIsFD6F = true;
        private int iScanModeInt = 0;


        protected void setScanUUID(ParcelUuid uuid) {
            mScanUUID = uuid;
            if (uuid != null) {
                mIsFD6F = uuid.equals(FD6F_UUID);
                if (mIsFD6F) {
                    iScanModeInt = 0;
                } else {
                    iScanModeInt = 1;
                }
            } else {
                mIsFD6F = false;
                iScanModeInt = 2;
            }
        }

        private void handleResult(@NonNull ScanResult result) {
            mScannResultsOnStart = true;
            long tsNow = System.currentTimeMillis();
            if (iTimoutTimer == null || !iTimoutTimer.iIsTimeoutCheckerRunning) {
                iTimoutTimer = new MySimpleTimer(MyScanCallback.this);
                iTimoutTimer.iIsTimeoutCheckerRunning = true;
                iTimoutTimer.start();
            }
            iTimoutTimer.iLastScanEvent = tsNow;

            if (BuildConfig.DEBUG) {
                if (iLastTs + 2000 < tsNow) {
                    Log.v(LOG_TAG, "Process scan result...");
                }
                iLastTs = tsNow;
            }

            BluetoothDevice btDevice = result.getDevice();
            if (mScanUUID == null) /* MULTI SCAN MODE */ {
                if (btDevice != null) {
                    ParcelUuid[] uuids = btDevice.getUuids();
                    if (uuids != null) {
                        for (int i = 0; i < uuids.length; i++) {
                            boolean isFD6F = uuids[i].equals(FD6F_UUID);
                            if (isFD6F || uuids[i].equals(FD64_UUID)) {
                                ScanRecord rec = result.getScanRecord();
                                if (rec != null) {
                                    processDevice(btDevice, result, rec, tsNow, isFD6F, rec.getServiceData(uuids[i]));
                                } else {
                                    processDevice(btDevice, result, null, tsNow, isFD6F, null);
                                }
                                break;
                            }
                        }
                    } else {
                        // to bad no UUID-Info can be provided...
                        ScanRecord rec = result.getScanRecord();
                        if (rec != null) {
                            byte[] d6Fdata = rec.getServiceData(FD6F_UUID);
                            if (d6Fdata != null && d6Fdata.length > 0) {
                                processDevice(btDevice, result, rec, tsNow, true, d6Fdata);
                            } else {
                                byte[] d64data = rec.getServiceData(FD64_UUID);
                                if (d64data != null && d64data.length > 0) {
                                    processDevice(btDevice, result, rec, tsNow, false, d64data);
                                }
                            }
                        }
                    }
                }
            } else {
                // single UUID-Mode...
                ScanRecord rec = result.getScanRecord();
                if (rec != null) {
                    processDevice(btDevice, result, rec, tsNow, mIsFD6F, rec.getServiceData(mScanUUID));
                } else {
                    processDevice(btDevice, result, null, tsNow, mIsFD6F, null);
                }
            }
        }

        private void processDevice(final BluetoothDevice btDevice, final ScanResult result,
                                   final ScanRecord rec, final long tsNow,
                                   boolean isDF6F, byte[] data) {
            String addr = btDevice.getAddress();
            UUIDFD6FBeacon beacon = null;
            int prevContainerSize = mContainer.size();
            synchronized (mContainer) {
                // check every 30sec by default for outdated beacon
                // data...
                long delay = 30000;
                beacon = mContainer.get(addr);
                if (beacon == null) {
                    beacon = new UUIDFD6FBeacon(addr, tsNow, isDF6F);
                    mContainer.put(addr, beacon);
                    mTotalSize++;
                    // if we add an new id, we instantly check for
                    // possible expired ones...
                    delay = 0;
                    if (BuildConfig.DEBUG) {
                        mDoReport = true;
                    }
                }

                // check every x sec if there are expired Beacons in our store?
                if (iLastContainerCheckTs + delay < tsNow) {
                    checkForOutdatedBeaconsInt(tsNow);
                }
            }

            int newContainerSize = mContainer.size();
            mDoReport = newContainerSize != prevContainerSize;
            // after mContainer sync is left we can do the rest...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beacon.mTxPower = result.getTxPower();
            }
            beacon.addRssi(result.getTimestampNanos(), result.getRssi(), tsNow);
            if (rec != null) {
                beacon.mTxPowerLevel = rec.getTxPowerLevel();
            }
            if (data != null) {
                beacon.addData(data);
            }

            // if the user have configured a signal strength range we need to group the current
            // available Beacons by their last known signal strength...
            if (mSignalStrengthGroup != null) {
                mDoReport = groupBySignalStrength() || mDoReport;
            }

            // finally letting the GUI know, that we have new data...
            // have in mind, that this will be triggered quite often
            if (mDoReport) {
                shouldRefreshGui(addr, newContainerSize);
            }
        }

        private boolean groupBySignalStrength() {
            if (mSignalStrengthGroup != null) {
                if (BuildConfig.DEBUG) {
                    Log.v(LOG_TAG, "-------------> start grouping <-------------");
                }
                Integer[] oldSizes = mSignalStrengthGroup.sizes();
                mSignalStrengthGroup.clearCounts();
                synchronized (mContainer) {
                    for (UUIDFD6FBeacon beacon : mContainer.values()) {
                        mSignalStrengthGroup.add(beacon);
                    }
                }
                Integer[] newSizes = mSignalStrengthGroup.sizes();
                if (oldSizes.length != newSizes.length) {
                    return true;
                } else {
                    for (int i = 0; i < newSizes.length; i++) {
                        if (newSizes[i].intValue() != oldSizes[i].intValue()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private void shouldRefreshGui(String addr, int totalBeaconCount) {
            // if we do not have any special settings, then the 'totalBeaconCount'
            // is already the number we want to display everywhere (Notification and
            // in the activity text...
            // But if we have multiple UUIDs or if we have a threshold r another kind
            // of signalStrength grouping the value will be a different one
            if (mGuiCallback != null) {
                SignalStrengthGroupInfo[] groupSizeInfo = null;
                int totalGoodBeaconCount = totalBeaconCount;
                if (mSignalStrengthGroup != null) {
                    groupSizeInfo = mSignalStrengthGroup.getGroupSizeInfo();
                    totalGoodBeaconCount = mSignalStrengthGroup.iGoodCount;
                }
                switch (iScanModeInt) {
                    case 2:
                        // dual mode
                        int[] sizes = getBeaconCountSplitByType();
                        mGuiCallback.newBeconEvent(addr, mTotalSize, sizes[1], sizes[2], groupSizeInfo);
                        break;

                    case 1:
                        // StopCovid France mode
                        mGuiCallback.newBeconEvent(addr, mTotalSize, -1, totalGoodBeaconCount, groupSizeInfo);
                        break;

                    default:
                    case 0:
                        // ExposureNotificationFramework Mode
                        mGuiCallback.newBeconEvent(addr, mTotalSize, totalGoodBeaconCount, -1, groupSizeInfo);
                        break;
                }
            }
            // only IF the display is active we will update the notification
            // -> we have to check what is with the amoled display devices?!
            if (mDisplayIsOn) {
                if (mSignalStrengthGroup != null) {
                    updateNotificationText(false, mSignalStrengthGroup.iGoodCount);
                } else {
                    updateNotificationText(false, totalBeaconCount);
                }
            }
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, mContainer.size() + " " + mContainer.keySet());
            }
            mDoReport = false;
        }

        protected void checkForOutdatedBeaconsAfterTimeout(long tsNow) {
            int prevSize = mContainer.size();
            checkForOutdatedBeaconsInt(tsNow);
            int newSize = mContainer.size();
            if (prevSize != newSize) {
                shouldRefreshGui(null, newSize);
            }
        }

        protected void checkForOutdatedBeaconsInt(long tsNow) {
            iLastContainerCheckTs = tsNow;
            ArrayList<String> addrsToRemove = new ArrayList<>();
            synchronized (mContainer) {
                for (UUIDFD6FBeacon otherBeacon : mContainer.values()) {
                    // if beacon not returned in any scan of the last 25sec
                    // we going to remove it...
                    if (otherBeacon.mLastTs + 25000 < tsNow) {
                        addrsToRemove.add(otherBeacon.addr);
                    }
                }
                if (addrsToRemove.size() > 0) {
                    for (String aOtherAddr : addrsToRemove) {
                        mContainer.remove(aOtherAddr);
                        Log.d(LOG_TAG, "remove: " + aOtherAddr + " " + mContainer.size() + " " + mContainer.keySet());
                    }
                    if (mSignalStrengthGroup != null) {
                        groupBySignalStrength();
                    }
                    if (BuildConfig.DEBUG) {
                        mDoReport = true;
                    }
                }
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            handleResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult r : results) {
                handleResult(r);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public int hashCode() {
            return FD6F_UUID.hashCode();
        }
    }

    private class NotificationUpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            updateNotificationText(true, -1);
            return null;
        }
    }

    private void startScreenStateObserver() {
        if (mScreenOnOffReceiver == null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mScreenOnOffReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        if (mScanCallback != null) {
                            mScanCallback.mDisplayIsOn = true;
                        }
                        new NotificationUpdateTask().execute();
                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        if (mScanCallback != null) {
                            mScanCallback.mDisplayIsOn = false;
                        }
                    }
                }
            };
            registerReceiver(mScreenOnOffReceiver, filter);
        }
    }

    private void startDeviceBluetoothStatusObserver() {
        if (mBluetoothStateReceiver == null) {
            mBluetoothStateReceiver = new BroadcastReceiver() {
                private int lastState = -1;
                private boolean autoEnabledBTCauseTurnedOffTriggered = false;
                private boolean bTLEInitStarted = false;

                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_OFF' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_ON' =" + state + " [" + lastState + "]");
                                break;
                            case 15:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_BLE_ON' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_TURNING_ON' =" + state + " [" + lastState + "]");
                                break;
                            case 14:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_BLE_TURNING_ON' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(LOG_TAG, "New Bluetooth 'STATE_TURNING_OFF' =" + state + " [" + lastState + "]");
                                break;
                        }

                        if (lastState != state) {
                            lastState = state;
                            switch (state) {
                                case BluetoothAdapter.STATE_OFF:
                                    bTLEInitStarted = false;
                                    if (mBluetoothAdapter != null) {
                                        mShowBtIsOffWarning = true;
                                        // cancel all running processes...
                                        try {
                                            mBluetoothAdapter.cancelDiscovery();
                                        } catch (Throwable t) {
                                            Log.d(LOG_TAG, "", t);
                                        }
                                        stopScan(false);
                                        updateNotification();
                                        if (mGuiCallback != null) {
                                            mGuiCallback.newBeconEvent(null, -1, -1, -1, null);
                                        }
                                        if (!isAirplaneMode()) {
                                            if (mPrefs.getBoolean(R.string.PKEY_AUTOSTARTBLUETOOTH, R.string.DVAL_AUTOSTARTBLUETOOTH)) {
                                                if (!autoEnabledBTCauseTurnedOffTriggered) {
                                                    autoEnabledBTCauseTurnedOffTriggered = true;

                                                    // autorestart BT in 5 seconds...
                                                    if (mHandler != null) {
                                                        mHandler.postDelayed(() -> mBluetoothAdapter.enable(), 5000);
                                                    } else {
                                                        mBluetoothAdapter.enable();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case BluetoothAdapter.STATE_ON:
                                case 15 /*BluetoothAdapter.STATE_BLE_ON*/:
                                    autoEnabledBTCauseTurnedOffTriggered = false;
                                    mShowBtIsOffWarning = false;
                                    if (!bTLEInitStarted) {
                                        bTLEInitStarted = true;
                                        ensureAdapterAndScannerInit();
                                        mScannIsRunning = false;
                                        startScan(false);
                                        updateNotification();
                                        if (mGuiCallback != null) {
                                            mGuiCallback.newBeconEvent(null, -1, -1, -1, null);
                                        }
                                    }
                                    break;

                                case BluetoothAdapter.STATE_TURNING_ON:
                                    break;

                                case BluetoothAdapter.STATE_TURNING_OFF:
                                    break;
                            }
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBluetoothStateReceiver, filter);
        }

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mShowBtIsOffWarning = true;
                if (!isAirplaneMode()) {
                    if (mPrefs.getBoolean(R.string.PKEY_AUTOSTARTBLUETOOTH, R.string.DVAL_AUTOSTARTBLUETOOTH)) {
                        mBluetoothAdapter.enable();
                    }
                }
            }
        }
    }

    private void startLocationProviderStatusObserver() {
        if (mLocationProviderStateReceiver == null) {
            mLocationProviderStateReceiver = new BroadcastReceiver() {
                boolean iState = isLocationProviderEnabled();
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (action != null && action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                        if (intent.hasExtra(LocationManager.EXTRA_PROVIDER_ENABLED) && intent.hasExtra(LocationManager.EXTRA_PROVIDER_NAME)) {
                            boolean enabled = intent.getBooleanExtra(LocationManager.EXTRA_PROVIDER_ENABLED, false);
                            if(iState != enabled) {
                                if (enabled) {
                                    Log.w(LOG_TAG, "Location is enabled - call checkForScanStart(...)");
                                    checkForScanStart(2500);
                                } else {
                                    Log.w(LOG_TAG, "Location is disabled");
                                }
                                updateNotification();
                                if (mGuiCallback != null) {
                                    mGuiCallback.newBeconEvent(null, -1, -1, -1, null);
                                }
                            }
                            iState = enabled;
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
            registerReceiver(mLocationProviderStateReceiver, filter);
        }
    }
}