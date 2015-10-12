package com.emmaguy.cleanstatusbar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.WindowManager;

import com.emmaguy.cleanstatusbar.config.DefaultStatusBarConfig;
import com.emmaguy.cleanstatusbar.config.KitKatStatusBarConfig;
import com.emmaguy.cleanstatusbar.config.LollipopStatusBarConfig;
import com.emmaguy.cleanstatusbar.config.MStatusBarConfig;
import com.emmaguy.cleanstatusbar.config.StatusBarConfig;
import com.emmaguy.cleanstatusbar.widgets.StatusBarView;

public class CleanStatusBarService extends Service {
    private static final int NOTIFICATION_ID = 1;

    private static boolean sIsRunning = false;

    private static StatusBarView sStatusBarView;

    private WindowManager mWindowManager;
    private StatusBarConfig mStatusBarConfig;
    private NotificationManager mNotificationManager;

    private CleanStatusBarPreferences mPreferences;

    public CleanStatusBarService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mPreferences = new CleanStatusBarPreferences(PreferenceManager.getDefaultSharedPreferences(this), getResources());

        sIsRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int apiValue = mPreferences.getApiValue();
        if (apiValue == Build.VERSION_CODES.M) {
            mStatusBarConfig = new MStatusBarConfig(getResources(), getAssets(), mPreferences.isLightModeEnabled());
        } else if (apiValue == Build.VERSION_CODES.LOLLIPOP) {
            mStatusBarConfig = new LollipopStatusBarConfig(getResources(), getAssets());
        } else if (apiValue == Build.VERSION_CODES.KITKAT) {
            mStatusBarConfig = new KitKatStatusBarConfig(getResources(), getAssets(), mPreferences.isKitKatGradientEnabled());
        } else {
            mStatusBarConfig = new DefaultStatusBarConfig(getResources(), getAssets());
        }

        if (sStatusBarView == null) {
            sStatusBarView = new StatusBarView(this);
            mWindowManager.addView(sStatusBarView, getWindowManagerParams());
        }
        sStatusBarView.setStatusBarConfig(mStatusBarConfig,
                mPreferences.getBackgroundColour(),
                mPreferences.getClockTime(),
                mPreferences.showWifiIcon(),
                mPreferences.show3gIcon(),
                mPreferences.showGpsIcon());

        showNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    private WindowManager.LayoutParams getWindowManagerParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT); // must be translucent to support KitKat gradient
        params.gravity = Gravity.TOP;
        params.height = mStatusBarConfig.getStatusBarHeight();
        return params;
    }

    @Override
    public void onDestroy() {
        sIsRunning = false;

        if (sStatusBarView != null) {
            mWindowManager.removeView(sStatusBarView);
            sStatusBarView = null;
        }
        removeNotification();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setWhen(0)
                .setContentTitle(getString(R.string.clean_status_bar_is_running))
                .setContentText(getString(R.string.touch_to_configure))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT));
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void removeNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public static boolean isRunning() {
        return sIsRunning;
    }
}
