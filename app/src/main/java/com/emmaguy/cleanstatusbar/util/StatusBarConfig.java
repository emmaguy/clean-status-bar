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

    // Distinguish between the L developer preview and the pre-release screenshots we've seen of Android L,
    // so users can take screenshots of both the developer preview as it is now and as it will be on release
    private static final int VERSION_CODE_L_PRE_RELEASE = 1000;
    public static final int VERSION_CODE_L_DEVELOPER_PREVIEW = 21; // TODO: change to Build.VERSION_CODES.L when it's released

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
            case VERSION_CODE_L_PRE_RELEASE:
            case VERSION_CODE_L_DEVELOPER_PREVIEW:
                colourResId = R.color.android_l_status_bar;
                break;
        }

        return mResources.getColor(colourResId);
    }

    private boolean isAndroidL() {
        return isAndroidLPreRelease() || isAndroidLDeveloperPreview();
    }

    private boolean isAndroidLPreRelease() {
        return mApiLevel == VERSION_CODE_L_PRE_RELEASE;
    }

    private boolean isAndroidLDeveloperPreview() {
        return mApiLevel == VERSION_CODE_L_DEVELOPER_PREVIEW;
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

    public Drawable get3gDrawable(int icon) {
        if (isAndroidLPreRelease()) {
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
        if (isAndroidLPreRelease()) {
            return getTintedDrawable(mResources, R.drawable.stat_sys_wifi_signal_4_fully_l, getForegroundColour());
        } else {
            return getTintedDrawable(mResources, R.drawable.stat_sys_wifi_signal_4_fully, getForegroundColour());
        }
    }

    public Drawable getTintedDrawable(Resources res, int drawableResId, int colour) {
        Drawable drawable = res.getDrawable(drawableResId);
        drawable.setColorFilter(colour, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
