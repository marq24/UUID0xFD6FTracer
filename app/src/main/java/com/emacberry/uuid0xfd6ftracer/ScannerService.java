package com.emacberry.uuid0xfd6ftracer;

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
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScannerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    // SERVICE STUFF:
    // http://stackoverflow.com/questions/9740593/android-create-service-that-runs-when-application-stops

    // Running as FourgroundService!!!
    // http://developer.android.com/guide/components/services.html#Foreground

    private static final String LOG_TAG = "SCANNER";
    public static boolean isRunning = false;

    public static final ParcelUuid COVID19_UUID = ParcelUuid.fromString("0000fd6f-0000-1000-8000-00805f9b34fb");

    // /* SERVICES */
    IBinder mBinder = new LocalBinder();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind called " + intent);
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

    public void setGuiCallback(BeaconScannerActivity mainActivity) {
        mGuiCallback = mainActivity;
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBluetoothStateReceiver;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback = new ScannerService.MyScanCallback();
    private Handler mHandler = new Handler();
    private BeaconScannerActivity mGuiCallback = null;

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
        super.onStartCommand(intent, flags, startId);
        Log.w(LOG_TAG, "onStartCommand() start");
        if (intent != null) {
            /*if (intent.hasExtra(BaseActivity.ISREINITDATALOGGER)) {
                isReInitOfDataLogger = intent.getExtras().getBoolean(BaseActivity.ISREINITDATALOGGER);
            }
            if (isReInitOfDataLogger && intent.hasExtra(BaseActivity.NOGPSFIXARRIVED)) {
                isReInitOfDataLogger = false;
            }*/
        }

        try {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Log.d(LOG_TAG, "initBtLE() started...");
            if (mBluetoothAdapter == null) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
            }
            if (mBluetoothAdapter != null) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }

            // BT ON OFF OBSERVER...
            startDeviceBluetoothStatusObserver();
            mHandler.postDelayed(() -> startScan(),5000);

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
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Log.w(LOG_TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        Log.w(LOG_TAG, "Service.onDestroy() called - shutting down Scanner");
        try {
            if (mBluetoothAdapter != null) {
                if(mBluetoothLeScanner != null && mScanCallback != null){
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

        if (mBluetoothStateReceiver != null) {
            try {
                unregisterReceiver(mBluetoothStateReceiver);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        mHandler = null;


        // un-register for PrefChanges...
        //_prefs.unregisterOnSharedPreferenceChangeListener(this);
        //_logger.unregisterDataLogerservice();

        isRunning = false;
        stopForeground(true);
        super.onDestroy();
        Log.w(LOG_TAG, "Service destroyed!!! (fine)");
    }


    public void stopScan() {
        if(mScannIsRunning){
            if(mBluetoothLeScanner != null) {
                Log.d(LOG_TAG, "mBluetoothLeScanner.stopScan() called");
                try {
                    mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
                }catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    mScannIsRunning = false;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean mScannIsRunning = false;
    public void  startScan(){
        if(mBluetoothLeScanner != null && mBluetoothAdapter.isEnabled() && !mScannIsRunning) {
            Log.d(LOG_TAG, "mBluetoothLeScanner.startScan() called");
            ArrayList<ScanFilter> f = new ArrayList<>();
            f.add(new ScanFilter.Builder().setServiceUuid(COVID19_UUID).build());
            mBluetoothLeScanner.startScan(f, new ScanSettings.Builder().build(), mScanCallback);
            mScannIsRunning = true;
        }
    }

    private CharSequence mNotifyText;
    private CharSequence mNotifyTitle;
    private NotificationCompat.Builder mBuilder;

    private NotificationCompat.Builder getNotificationBuilder() {
        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html
        // In this sample, we'll use the same text for the ticker and the
        // expanded notification
        if (mNotifyTitle == null) {
            mNotifyTitle = getText(R.string.app_service_title);
            mNotifyText = getText(R.string.app_service_msg);
        }

        String channelId = NotificationHelper.getBaseNotificationChannelId(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(mNotifyTitle);
        builder.setContentText(mNotifyText);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            builder.setSmallIcon(R.drawable.ic_app_notify72);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        Intent intent = new Intent(this, BeaconScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(BeaconScannerActivity.INTENT_EXTRA_SERVICE_ACTION, true);
        builder.setContentIntent(PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT));

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(BeaconScannerActivity.INTENT_EXTRA_TERMINATE_APP));
        } else {
            builder.addAction(R.drawable.ic_outline_exit_to_app_24px_api20, this.getString(R.string.menu_exit_notify_action), getTerminateAppIntent(BeaconScannerActivity.INTENT_EXTRA_TERMINATE_APP));
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
            // TODO CHECK PERMISSION
            /*if (_logger != null) {
                _logger.checkPermissions("forground");
            }*/
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*private PendingIntent getAppIntent(String extra) {
        Intent intent = new Intent(this, GPSLoggerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        return PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }*/

    private PendingIntent getTerminateAppIntent(String extra) {
        Intent intent = new Intent(this, BeaconScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        return PendingIntent.getActivity(this, intent.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /*private KeyguardManager mKeyguardManager = null;
    private NotificationManagerCompat mNotificationManager = null;
    private long mLastNotifyUpdateTs = 0;
    private boolean mNotifyCanBeReset = false;

    public void updateNotification() {
        try {
            mBuilder = getNotificationBuilder();
            if (mNotificationManager == null) {
                mNotificationManager = NotificationManagerCompat.from(this);
            }
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mNotificationManager.notify(R.id.notify_backservive, mBuilder.build());
        }catch(Throwable t){
            Log.d(LOG_TAG, ""+t.getMessage());
        }
    }*/

    /*private void updateNotificationText(Record rec, long nowTs) {
        final int interval = _prefs.getInt(R.string.PKEY_STATUS_AS_NOTIFICATION, R.string.DVAL_STATUS_AS_NOTIFICATION);
        if (interval > 0) {
            if (mKeyguardManager == null) {
                mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            }
            if (nowTs > mLastNotifyUpdateTs + interval) {
                mLastNotifyUpdateTs = nowTs;
                boolean notify = false;
                boolean reset = true;
                if (mBuilder != null) {
                    String txt = null;
                    if (_logger != null) {
                        if (_logger.isLoggingMode()) {
                            if (_logger._mcpGuiUpdateCallback == null || mKeyguardManager.inKeyguardRestrictedInputMode()) {
                                txt = _logger.getDisplayStringOfCurrentRecord(false, false);
                            }
                        }
                    }

                    if (txt != null) {
                        // in lock mode we need to split the lines...
                        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(txt));
                        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
                            int len = txt.length();
                            // find the "next" space... from the middle...
                            int pos = txt.indexOf(" ", len / 2);
                            String t1 = txt.substring(0, pos);
                            String t2 = txt.substring(pos + 1, len);
                            mBuilder.setContentTitle(t1);
                            mBuilder.setContentText(t2);
                        } else {
                            mBuilder.setContentTitle(mNotifyTitle);
                            mBuilder.setContentText(txt);
                        }
                        notify = true;
                        reset = false;
                        mNotifyCanBeReset = true;
                    }
                }

                if (reset) {
                    if (mNotifyCanBeReset) {
                        mNotifyCanBeReset = false;
                        mBuilder.setContentTitle(mNotifyTitle);
                        mBuilder.setContentText(mNotifyText);
                        mBuilder.setStyle(null);
                        notify = true;
                    }
                }

                if (notify) {
                    if (mNotificationManager == null) {
                        mNotificationManager = NotificationManagerCompat.from(this);
                        //mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                    mNotificationManager.notify(R.id.notify_backservive, mBuilder.build());
                }
            }
        }
    }

    private void trace() {
        Log.w(LOG_TAG, "------------------------------------");
        Log.w(LOG_TAG, "Update Notification... -> ");
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement t : trace) {
            Log.w(LOG_TAG, t.toString());
        }
    }
    */

    protected HashMap<String, Covid19Beacon> mContainer = new HashMap<>();

    private class MyScanCallback extends ScanCallback {

        private void handleResult(@NonNull ScanResult result) {
            String addr = result.getDevice().getAddress();
            Covid19Beacon beacon = null;
            synchronized (mContainer) {
                beacon = mContainer.get(addr);
                if (beacon == null) {
                    beacon = new Covid19Beacon(addr);
                    mContainer.put(addr, beacon);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beacon.mTxPower = result.getTxPower();
            }
            beacon.addRssi(result.getTimestampNanos(), result.getRssi());
            ScanRecord rec = result.getScanRecord();
            if (rec != null) {
                beacon.mTxPowerLevel = rec.getTxPowerLevel();
                beacon.addData(rec.getServiceData(COVID19_UUID));
            }

            // finally letting the GUI know, that we have new data...
            // have in mind, that this will be triggered quite often
            if (mGuiCallback != null) {
                mGuiCallback.newBeconEvent(addr);
            }else {
                Log.v(LOG_TAG, addr);
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
            return COVID19_UUID.hashCode();
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