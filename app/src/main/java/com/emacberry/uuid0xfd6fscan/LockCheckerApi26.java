package com.emacberry.uuid0xfd6fscan;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LockCheckerApi26 {

    private static KeyguardManager km;
    private static KeyguardManager.KeyguardDismissCallback kdallback;
    private static final String LOG_TAG = "LockCheckerApi26";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean checkLockedApi26(Activity act, boolean requestUnlock) {
        boolean ret = false;
        try {
            if (km == null) {
                km = (KeyguardManager) act.getSystemService(Context.KEYGUARD_SERVICE);
            }
            ret = km.isKeyguardLocked();
            if (ret) {
                if(requestUnlock) {
                    if(kdallback == null) {
                        kdallback = new KeyguardManager.KeyguardDismissCallback() {
                            public void onDismissError() {
                            }

                            /**
                             * Called when dismissing Keyguard has succeeded and the device is now unlocked.
                             */
                            public void onDismissSucceeded() {
                            }

                            /**
                             * Called when dismissing Keyguard has been cancelled, i.e. when the user cancelled the
                             * operation or the bouncer was hidden for some other reason.
                             */
                            public void onDismissCancelled() {
                            }
                        };
                    }
                    km.requestDismissKeyguard(act, kdallback);
                }
            }
        } catch (Throwable t) {
            Log.d(LOG_TAG, "" + t.getMessage());
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isInLockStateApi26(FragmentActivity act) {
        boolean ret = false;
        try {
            if (km == null) {
                km = (KeyguardManager) act.getSystemService(Context.KEYGUARD_SERVICE);
            }
            ret = km.isKeyguardLocked();
        } catch (Throwable t) {
            Log.d(LOG_TAG, "" + t.getMessage());
        }
        return ret;
    }
}
