package com.emmaguy.cleanstatusbar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.WindowManager;

import com.emmaguy.cleanstatusbar.ui.StatusBarView;
import com.emmaguy.cleanstatusbar.util.StatusBarConfig;

public class CleanStatusBarService extends Service {
    private StatusBarConfig mStatusBarConfig;
    private StatusBarView mStatusBarView;

    private WindowManager mWindowManager;

    public CleanStatusBarService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mStatusBarView = new StatusBarView(this);
        mStatusBarConfig = new StatusBarConfig(getResources());

        mStatusBarView.setFont(this, shouldUseMediumFont());
        mStatusBarView.setTime(getClockTime());
        mStatusBarView.setForegroundColour(getForegroundColour());

        if (shouldDrawGradient()) {
            Drawable[] layers = {new ColorDrawable(getBackgroundColour()), getResources().getDrawable(R.drawable.gradient_bg)};
            LayerDrawable layerDrawable = new LayerDrawable(layers);

            mStatusBarView.setBackgroundDrawable(layerDrawable);
        } else {
            mStatusBarView.setBackgroundColor(getBackgroundColour());
        }

        mWindowManager.addView(mStatusBarView, getWindowManagerParams());
    }

    private WindowManager.LayoutParams getWindowManagerParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.OPAQUE);
        params.gravity = Gravity.TOP;
        params.height = mStatusBarConfig.getStatusBarHeight();
        return params;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mStatusBarView != null) {
            mWindowManager.removeView(mStatusBarView);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean shouldDrawGradient() {
        return getSharedPrefs().getBoolean(MainActivity.PREFS_KEY_DRAW_GRADIENT, false);
    }

    private boolean shouldUseMediumFont() {
        return getSharedPrefs().getBoolean(MainActivity.PREFS_KEY_USE_MEDIUM_FONT, true);
    }

    public String getClockTime() {
        return getSharedPrefs().getString(MainActivity.PREFS_KEY_CLOCK_TIME, "12:00");
    }

    public int getBackgroundColour() {
        return getSharedPrefs().getInt(MainActivity.PREFS_KEY_BACKGROUND_COLOUR, android.R.color.black);
    }

    public int getForegroundColour() {
        return getSharedPrefs().getInt(MainActivity.PREFS_KEY_FOREGROUND_COLOUR, android.R.color.white);
    }

    private SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
}
