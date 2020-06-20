package com.emacberry.uuid0xfd6ftracer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean doAutostart = Preferences.getInstance(context).getBoolean(R.string.PKEY_AUTOSTART, R.string.DVAL_AUTOSTART);
        if (doAutostart) {
            Log.i("BOOT", "AUTOSTART UUID 0xFD6F Tracer");
            Intent scannerIntent = new Intent(context, ScannerService.class);
            scannerIntent.putExtra(ScannerActivity.INTENT_EXTRA_AUTOSTART, true);
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(scannerIntent);
                } else {
                    context.startService(scannerIntent);
                }
            } catch (Throwable t) {
                Log.e("BOOT", "" + t.getMessage());
            }
        }
    }
}
