package com.emmaguy.cleanstatusbar.config;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by emma on 21/06/15.
 */
public interface StatusBarConfig {
    int getStatusBarHeight();

    boolean drawGradient();

    int getForegroundColour();

    Typeface getFont();

    float getFontSize();

    Drawable getNetworkIconDrawable(int icon);

    Drawable getGPSDrawable();

    Drawable getWifiDrawable();

    int getRightPadding();

    int getNetworkIconPaddingOffset();

    int getWifiPaddingOffset();

    void setBatteryViewDimensions(View v);
}
