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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.emacberry.uuid0xfd6ftracer.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "0xFD6F";
    private static final String LOG_TAG_BTLE = "BT";

    private boolean mBTLEInitNeedToBeCancledCauseBTWasTurnedOff = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBluetoothStateReceiver;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback = new MyScanCallback();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 99);

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
                startScan();
            }
        });
        FloatingActionButton stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                stopScan();
            }
        });


        if (mBluetoothAdapter == null) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
    }

    @Override
    protected void onDestroy() {
        haltAll();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startAll();
    }

    private void stopScan() {
        if(mScannIsRunning){
            if(mBluetoothLeScanner != null) {
                Log.d(LOG_TAG_BTLE, "mBluetoothLeScanner.stopScan() called");
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
    private void  startScan(){
        if(mBluetoothLeScanner != null && mBluetoothAdapter.isEnabled()) {
            Log.d(LOG_TAG_BTLE, "mBluetoothLeScanner.startScan() called");
            ArrayList<ScanFilter> f = new ArrayList<>();
            f.add(new ScanFilter.Builder().setServiceUuid(COVID19_UUID).build());
            mBluetoothLeScanner.startScan(f, new ScanSettings.Builder().build(), mScanCallback);
            mScannIsRunning = true;
        }
    }

    private void haltAll() {
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
    }

    private volatile boolean mBlueToothScanWasStarted = false;

    public static final ParcelUuid COVID19_UUID = ParcelUuid.fromString("0000fd6f-0000-1000-8000-00805f9b34fb");

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private class Covid19Beacon {
        public int mTxPowerLevel;
        public int mTxPower;
        public String addr;
        public long mLastTs;
        public TreeMap<Long, Integer> sigHistory = new TreeMap<>();
        public HashSet<String> data = new HashSet<>();

        public Covid19Beacon(String addr) {
            this.addr = addr;
        }

        public void addRssi(long ts, int rssi) {
            mLastTs = System.currentTimeMillis();
            sigHistory.put(ts, rssi);
        }

        public void addData(byte[] serviceData) {
            data.add(bytesToHex(serviceData));
        }

        private String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        }

        @Override
        public String toString() {
            StringBuffer b = new StringBuffer();
            b.append(addr);
            b.append(" [");
            b.append(data.size());
            b.append("] ");
            b.append(sigHistory.size());
            b.append(' ');
            b.append(new Date(mLastTs));
            return b.toString();
        }
    }

    private HashMap<String, Covid19Beacon> mContainer = new HashMap<>();

    private class MyScanCallback extends ScanCallback {
        private void handleResult(@NonNull ScanResult result){
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
            if(rec != null) {
                beacon.mTxPowerLevel = rec.getTxPowerLevel();
                beacon.addData(rec.getServiceData(COVID19_UUID));
            }
            System.out.println(beacon);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            handleResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for(ScanResult r: results){
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

    private void startAll() {
        Log.d(LOG_TAG_BTLE, "initBtLE() started...");
        if (mBluetoothAdapter == null) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        // BT ON OFF OBSERVER...
        startDeviceBluetoothStatusObserver();
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
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_OFF' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_ON' =" + state + " [" + lastState + "]");
                                break;
                            case 15:
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_BLE_ON' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_TURNING_ON' =" + state + " [" + lastState + "]");
                                break;
                            case 14:
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_BLE_TURNING_ON' =" + state + " [" + lastState + "]");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(LOG_TAG_BTLE, "New Bluetooth 'STATE_TURNING_OFF' =" + state + " [" + lastState + "]");
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
                                            mBTLEInitNeedToBeCancledCauseBTWasTurnedOff = true;
                                            try {
                                                mBluetoothAdapter.cancelDiscovery();
                                            } catch (Throwable t) {
                                                Log.d(LOG_TAG_BTLE, "", t);
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


    private boolean isAirplaneMode() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
