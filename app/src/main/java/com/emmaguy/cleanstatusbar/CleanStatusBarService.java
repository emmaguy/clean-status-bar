package com.emmaguy.cleanstatusbar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import com.emmaguy.cleanstatusbar.util.StatusBarConfig;
import com.emmaguy.cleanstatusbar.views.StatusBarView;

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

        mWindowManager.addView(mStatusBarView, getWindowManagerParams());
    }

    private WindowManager.LayoutParams getWindowManagerParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
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
}
