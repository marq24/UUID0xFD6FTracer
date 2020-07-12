package com.emacberry.uuid0xfd6fscan;

import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
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

public class ScannerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String INTENT_EXTRA_START = "START_SCAN";
    protected static final String INTENT_EXTRA_STOP = "STOP_SCAN";

    // SERVICE STUFF:
    // http://stackoverflow.com/questions/9740593/android-create-service-that-runs-when-application-stops

    // Running as FourgroundService!!!
    // http://developer.android.com/guide/components/services.html#Foreground

    private static final String LOG_TAG = "SCANNER";
    public static boolean isRunning = false;

    public static final ParcelUuid FD6F_UUID = ParcelUuid.fromString("0000fd6f-0000-1000-8000-00805f9b34fb");

    // /* SERVICES */
    IBinder mBinder = new LocalBinder();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // DO NOTHING RIGHT NOW...
        //Log.d(LOG_TAG, "onSharedPreferenceChanged "+key);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind called " + intent);
        if(mBinder == null){
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
                startScan(true);
            }
        }
    }

    private BroadcastReceiver mScreenOnOffReceiver;
    private BroadcastReceiver mBluetoothStateReceiver;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private MyScanCallback mScanCallback = new ScannerService.MyScanCallback();
    private Handler mHandler = new Handler();
    private ScannerActivity mGuiCallback = null;

    private boolean isAirplaneMode() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mHandler == null){
            mHandler = new Handler();
        }
        if (isRunning && intent != null) {
            handleIntentInt(intent);
            return START_STICKY;
        } else {
            super.onStartCommand(intent, flags, startId);
            Log.w(LOG_TAG, "onStartCommand() start");
            if (intent != null) {
                if(intent.getBooleanExtra(ScannerActivity.INTENT_EXTRA_AUTOSTART, false)){
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
                // BT ON OFF OBSERVER...
                startDeviceBluetoothStatusObserver();
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
            if(mHandler != null) {
                mHandler.postDelayed(() -> checkForScannStart(), 5000);
            }
            return START_STICKY;
        }
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            Preferences.getInstance(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
            if(mHandler == null){
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
        ensureAdapterAndScannerClosed();
        if (mBluetoothStateReceiver != null) {
            try {
                unregisterReceiver(mBluetoothStateReceiver);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (mScreenOnOffReceiver != null) {
            try {
                unregisterReceiver(mScreenOnOffReceiver);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        mHandler = null;

        Preferences.getInstance(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        isRunning = false;
        stopForeground(true);
        super.onDestroy();
        Log.w(LOG_TAG, "Service destroyed!!! (fine)");
    }


    private void handleIntentInt(@Nullable Intent intent) {
        Log.d(LOG_TAG, "start intent extras: " + intent.getExtras());
        if (intent.hasExtra(INTENT_EXTRA_START)) {
            startScan(true);
        } else if (intent.hasExtra(INTENT_EXTRA_STOP)) {
            stopScan(true);
        }
    }

    public boolean isScanning() {
        return mScannIsRunning;
    }

    private void ensureAdapterAndScannerClosed() {
        try {
            if (mBluetoothAdapter != null) {
                if (mBluetoothLeScanner != null && mScanCallback != null) {
                    mBluetoothLeScanner.stopScan(mScanCallback);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    mScannIsRunning = false;
                    updateNotification();
                    if(mGuiCallback!=null){
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
        if (viaGui) {
            mScannStopedViaGui = false;
        }
        if(checkScanPermissions()) {
            if (mBluetoothLeScanner != null && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && !mScannIsRunning) {
                Log.d(LOG_TAG, "mBluetoothLeScanner.startScan() called");
                ArrayList<ScanFilter> f = new ArrayList<>();
                f.add(new ScanFilter.Builder().setServiceUuid(FD6F_UUID).build());
                mContainer = new HashMap<>();
                mBluetoothLeScanner.startScan(f, new ScanSettings.Builder().build(), mScanCallback);
                mScannIsRunning = true;
                updateNotification();
                if(mGuiCallback!=null){
                    mGuiCallback.updateButtonImg();
                }
            }
            if(mHandler != null) {
                mHandler.postDelayed(() -> checkForScannStart(), 30000);
            }
        }else{
            // no permission...? start activity and request START again
            updateNotification();
        }
    }

    private AppOpsManager mAppOps;
    private boolean checkScanPermissions() {
        if(mAppOps==null){
            mAppOps = getSystemService(AppOpsManager.class);
        }
        if (checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isAppOppAllowed(mAppOps, AppOpsManager.OPSTR_FINE_LOCATION, BuildConfig.APPLICATION_ID)) {
            mHasScanPermission = true;
            return true;
        }else {
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

    public void checkForScannStart() {
        if (!mScannStopedViaGui) {
            if(mHandler != null) {
                if (mScannIsRunning && !mScannResultsOnStart) {
                    Log.w(LOG_TAG, "checkForScannStart() triggered - mScannIsRunning: TRUE");
                    mHandler.postDelayed(() -> stopScan(false), 500);
                    mHandler.postDelayed(() -> startScan(false), 5000);
                } else if (!mScannIsRunning) {
                    Log.w(LOG_TAG, "checkForScannStart() triggered - mScannIsRunning: FALSE");
                    mHandler.postDelayed(() -> startScan(false), 500);
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

        if (mScannIsRunning) {
            builder.setContentText(mNotifyTextScanning);
            builder.addAction(-1, this.getString(R.string.menu_stop_notify_action), getServiceIntent(INTENT_EXTRA_STOP));
        } else {
            if(checkScanPermissions() && mHasScanPermission) {
                builder.setContentText(getString(R.string.app_service_msgOff));
                // Check 'start scan' from notification action can cause no scan results after
                //  the start / this will not happen, if scan is triggered via Activity (no clue yet why)
                // logcat reports:
                // E/BluetoothUtils: Permission denial: Need ACCESS_FINE_LOCATION permission to get scan results
                //
                // wtf?!
                builder.addAction(-1, this.getString(R.string.menu_start_notify_action), getServiceIntent(INTENT_EXTRA_START));
            }else{
                builder.setContentText(getText(R.string.app_service_msgOffNoPermissions));
            }
        }
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(ScannerActivity.INTENT_EXTRA_TERMINATE_APP));
        } else {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px_api20, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(ScannerActivity.INTENT_EXTRA_TERMINATE_APP));
        }
        builder.setColor(ContextCompat.getColor(this, R.color.notification_action));
        return builder;
    }

    /*private void showLaunchNotification(){
        Intent fullScreenIntent = new Intent(this, ScannerActivity.class);
        // For the activity opening when notification cliced
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 2022, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, NotificationHelper.getBaseNotificationChannelId(this))
                .setSmallIcon(R.drawable.ic_app_notify72)
                .setContentTitle("Notification title")
                .setContentText("Notification Text")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                //.setFullScreenIntent(fullScreenPendingIntent, true)
                .setContentIntent(fullScreenPendingIntent)
                .build();

        startForeground(R.id.notify_backservive, notification);
    }*/

    private void showNotification() {
        try {
            mBuilder = getNotificationBuilder();
            startForeground(R.id.notify_backservive, mBuilder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            // TODO CHECK PERMISSION
            /*if (_logger != null) {
                _logger.checkPermissions("forground");
            }*/
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

    private void updateNotificationText(boolean force) {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        }
        boolean notify = false;
        if (mBuilder != null) {
            int size = mContainer.size();
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
                        if(mHasScanPermission) {
                            mBuilder.setContentText(getText(R.string.app_service_msgOff));
                        }else{
                            mBuilder.setContentText(getText(R.string.app_service_msgOffNoPermissions));
                        }
                    }
                    mBuilder.setStyle(null);
                    notify = true;
                }
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

    private class MySimpleTimer extends Thread{
        private volatile long iLastScanEvent = 0;
        private volatile boolean iIsTimeoutCheckerRunning = false;
        private MyScanCallback iCallback = null;

        public MySimpleTimer(MyScanCallback myScanCallback){
            super("MySimpleTimer");
            this.iCallback = myScanCallback;
        }

        public void run(){
            while(iIsTimeoutCheckerRunning){
                try {
                    long delay = 35000L;
                    if(iLastScanEvent > 0){
                        delay = (iLastScanEvent + delay) - System.currentTimeMillis();
                        if(delay < 0){
                            delay = 35000L;
                        }
                    }
                    if(BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "MySimpleTimer sleep "+(delay/1000)+"s");
                    }
                    sleep(delay);
                    if(BuildConfig.DEBUG) {
                        Log.v(LOG_TAG, "MySimpleTimer wake up");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long tsNow = System.currentTimeMillis();
                if(iLastScanEvent + 31000 < tsNow){
                    if(BuildConfig.DEBUG){
                        Log.v(LOG_TAG, "last scan event longer then 31sec ago...");
                    }
                    // we have not received since 30 seconds a new scan
                    // result, then we need to invalidate our content...
                    // (BeaconsInRange = 0)
                    iCallback.checkForOutdatedBeaconsAfterTimeout(tsNow);
                    iIsTimeoutCheckerRunning = false;
                }else{
                    if(BuildConfig.DEBUG){
                        Log.v(LOG_TAG, "last scan event shorter then 31sec ago all fine");
                    }
                }
            }
            if(BuildConfig.DEBUG){
                Log.v(LOG_TAG, "MySimpleTimer ended");
            }
        }
    }

    private class MyScanCallback extends ScanCallback {
        private long iLastContainerCheckTs = 0;
        public boolean mDoReport = false;
        public boolean mDisplayIsOn = true;
        private long iLastTs = 0;
        private MySimpleTimer iTimoutTimer = null;

        private void handleResult(@NonNull ScanResult result) {
            mScannResultsOnStart = true;
            long tsNow = System.currentTimeMillis();
            if(iTimoutTimer == null || !iTimoutTimer.iIsTimeoutCheckerRunning){
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

            String addr = result.getDevice().getAddress();
            UUIDFD6FBeacon beacon = null;
            int prevContainerSize = mContainer.size();
            synchronized (mContainer) {
                // check every 30sec by default for outdated beacon
                // data...
                long delay = 30000;
                beacon = mContainer.get(addr);
                if (beacon == null) {
                    beacon = new UUIDFD6FBeacon(addr, tsNow);
                    mContainer.put(addr, beacon);
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
            mDoReport = mContainer.size() != prevContainerSize;

            // after mContainer sync is left we can do the rest...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beacon.mTxPower = result.getTxPower();
            }
            beacon.addRssi(result.getTimestampNanos(), result.getRssi(), tsNow);
            ScanRecord rec = result.getScanRecord();
            if (rec != null) {
                beacon.mTxPowerLevel = rec.getTxPowerLevel();
                beacon.addData(rec.getServiceData(FD6F_UUID));
            }

            // finally letting the GUI know, that we have new data...
            // have in mind, that this will be triggered quite often
            if (mDoReport) {
                shouldRefreshGui(addr);
            }
        }

        private void shouldRefreshGui(String addr){
            if (mGuiCallback != null) {
                mGuiCallback.newBeconEvent(addr);
            }
            // only IF the display is active we will update the notification
            // -> we have to check what is with the amoled display devices?!
            if (mDisplayIsOn) {
                updateNotificationText(false);
            }
            Log.d(LOG_TAG, mContainer.size() + " " + mContainer.keySet());
            mDoReport = false;
        }

        protected void checkForOutdatedBeaconsAfterTimeout(long tsNow) {
            int prevSize = mContainer.size();
            checkForOutdatedBeaconsInt(tsNow);
            if(prevSize != mContainer.size()){
                shouldRefreshGui(null);
            }
        }

        protected void checkForOutdatedBeaconsInt(long tsNow){
            iLastContainerCheckTs = tsNow;
            ArrayList<String> addrsToRemove = new ArrayList<>();
            for (UUIDFD6FBeacon otherBeacon : mContainer.values()) {
                // if beacon not returned in any scan of the last 15sec
                // we going to remove it...
                if (otherBeacon.mLastTs + 15000 < tsNow) {
                    addrsToRemove.add(otherBeacon.addr);
                }
            }
            if (addrsToRemove.size() > 0) {
                for (String aOtherAddr : addrsToRemove) {
                    mContainer.remove(aOtherAddr);
                }
                if (BuildConfig.DEBUG) {
                    mDoReport = true;
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
            updateNotificationText(true);
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
                private boolean enabledBTCauseTurnedOff = false;
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
                                        if (!enabledBTCauseTurnedOff) {
                                            enabledBTCauseTurnedOff = true;
                                            // cancel all running processes...
                                            //mBTLEInitNeedToBeCancledCauseBTWasTurnedOff = true;
                                            try {
                                                mBluetoothAdapter.cancelDiscovery();
                                            } catch (Throwable t) {
                                                Log.d(LOG_TAG, "", t);
                                            }

                                            if (!isAirplaneMode()) {
                                                // autorestart BT in 5 seconds...
                                                if (mHandler != null) {
                                                    mHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mBluetoothAdapter.enable();
                                                        }
                                                    }, 5000);
                                                } else {
                                                    mBluetoothAdapter.enable();
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case BluetoothAdapter.STATE_ON:
                                case 15:
                                    enabledBTCauseTurnedOff = false;
                                    if (!bTLEInitStarted) {
                                        if (mBluetoothAdapter != null) {
                                            bTLEInitStarted = true;
                                            // start the LESCANN AGAIN...
                                            // TODO!!!
                                            //mBluetoothAdapter.startDiscovery();
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

        /*if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                if (!isAirplaneMode()) {
                    mBluetoothAdapter.enable();
                }
            }
        }*/
    }
}