package com.emmaguy.cleanstatusbar.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.emmaguy.cleanstatusbar.R;

public class StatusBarConfig {
    private static final String RESOURCE_NAME_STATUS_BAR_HEIGHT = "status_bar_height";

    private final int mApiLevel;
    private final boolean mIsKitKatGradientEnabled;

    private final Resources mResources;
    private final AssetManager mAssetManager;

    public StatusBarConfig(int apiLevel, boolean isKitKatGradientEnabled, Resources r, AssetManager a) {
        mApiLevel = apiLevel;
        mIsKitKatGradientEnabled = isKitKatGradientEnabled;
        mResources = r;
        mAssetManager = a;
    }

    public int getStatusBarHeight() {
        return getInternalDimensionSize(mResources, RESOURCE_NAME_STATUS_BAR_HEIGHT);
    }

    public boolean shouldDrawGradient() {
        return mIsKitKatGradientEnabled && mApiLevel == Build.VERSION_CODES.KITKAT;
    }

    public int getForegroundColour() {
        int colourResId = R.color.android_jellybean_status_bar;

        switch (mApiLevel) {
            case Build.VERSION_CODES.KITKAT:
                if (shouldDrawGradient()) {
                    colourResId = R.color.android_kitkat_status_bar_gradient;
                } else {
                    colourResId = R.color.android_kitkat_status_bar_default;
                }
                break;
            case Build.VERSION_CODES.LOLLIPOP:
                colourResId = R.color.android_l_status_bar;
                break;
        }

        return mResources.getColor(colourResId);
    }

    private boolean isAndroidL() {
        return mApiLevel == Build.VERSION_CODES.LOLLIPOP;
    }

    public Typeface getFont() {
        if (isAndroidL()) {
            return Typeface.createFromAsset(mAssetManager, "fonts/Roboto-Medium.ttf");
        }

        return null;
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public float getFontSize() {
        return 16.0f;
    }

    public Drawable getNetworkIconDrawable(int icon) {
        if (isAndroidL()) {
            switch (icon) {
                case 1:
                    icon = R.drawable.network_icon_g_l;
                    break;

                case 2:
                    icon = R.drawable.network_icon_e_l;
                    break;

                case 3:
                    icon = R.drawable.network_icon_3g_l;
                    break;

                case 4:
                    icon = R.drawable.network_icon_h_l;
                    break;

                case 5:
                    icon = R.drawable.network_icon_lte_l;
                    break;

                case 99:
                    icon = R.drawable.network_icon_roam_l;
                    break;

                default:
                    icon = R.drawable.network_icon_off_l;
            }
            return getTintedDrawable(mResources, icon, getForegroundColour());
        } else {
            switch (icon) {
                case 1:
                    icon = R.drawable.network_icon_g;
                    break;

                case 2:
                    icon = R.drawable.network_icon_e;
                    break;

                case 3:
                    icon = R.drawable.network_icon_3g;
                    break;

                case 4:
                    icon = R.drawable.network_icon_h;
                    break;

                case 5:
                    icon = R.drawable.network_icon_lte;
                    break;

                case 99:
                    icon = R.drawable.network_icon_roam;
                    break;

                default:
                    icon = R.drawable.network_icon_off;
            }
            return getTintedDrawable(mResources, icon, getForegroundColour());
        }
    }

    public Drawable getWifiDrawable() {
        int icon;

        if (isAndroidL()) {
            icon = R.drawable.stat_sys_wifi_signal_4_fully_l;
        } else {
            icon = R.drawable.stat_sys_wifi_signal_4_fully;
        }
        return getTintedDrawable(mResources, icon, getForegroundColour());
    }

    public Drawable getTintedDrawable(Resources res, int drawableResId, int colour) {
        Drawable drawable = res.getDrawable(drawableResId);
        drawable.setColorFilter(colour, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
