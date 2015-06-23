package com.emmaguy.cleanstatusbar.config;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.emmaguy.cleanstatusbar.R;

public class KitKatStatusBarConfig extends DefaultStatusBarConfig implements StatusBarConfig {
    private final boolean mKitKatGradientEnabled;

    public KitKatStatusBarConfig(Resources r, AssetManager a, boolean kitKatGradientEnabled) {
        super(r, a);

        mKitKatGradientEnabled = kitKatGradientEnabled;
    }

    @Override
    public boolean drawGradient() {
        return mKitKatGradientEnabled;
    }

    @Override
    public int getForegroundColour() {
        if (drawGradient()) {
            mResources.getColor(R.color.kitkat_status_bar_gradient);
        }
        return mResources.getColor(R.color.kitkat_status_bar_default);
    }

    @Override
    public Drawable getGPSDrawable() {
        return getTintedDrawable(mResources, R.drawable.stat_sys_gps_on_19_plus, getForegroundColour());
    }
}
