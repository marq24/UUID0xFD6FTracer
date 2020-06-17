package com.emacberry.uuid0xfd6ftracer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class NotificationHelper {
    // see: https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1

    public static String getBaseNotificationChannelId(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return createBaseNotificationChannel(c);
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            return "PREAPI26BASECHANNELIDNOTUSED";
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static Object mBaseNotifyChannelFor8dot1 = null;

    @TargetApi(Build.VERSION_CODES.O)
    private static String createBaseNotificationChannel(Context context) {
        String channelIdNew = "UUID0xFD6FTracer_BASE_SERVICEID";
        if (mBaseNotifyChannelFor8dot1 == null) {
            String channelName = context.getString(R.string.notifyChannelBase);
            mBaseNotifyChannelFor8dot1 = new NotificationChannel(channelIdNew, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).setSound(null, null);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).setShowBadge(false);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).enableVibration(false);
            ((NotificationChannel) mBaseNotifyChannelFor8dot1).enableLights(false);
            try {
                if (context != null) {
                    NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    service.createNotificationChannel(((NotificationChannel) mBaseNotifyChannelFor8dot1));
                }
            } catch (Throwable t) {
                Log.d("BASE", "" + t.getMessage());
            }
        }
        return channelIdNew;
    }
}
