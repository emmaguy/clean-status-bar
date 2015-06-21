package com.emmaguy.cleanstatusbar.config;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.emmaguy.cleanstatusbar.R;

public class DefaultStatusBarConfig implements StatusBarConfig {
    private static final String RESOURCE_NAME_STATUS_BAR_HEIGHT = "status_bar_height";

    protected final Resources mResources;
    protected final AssetManager mAssetManager;

    public DefaultStatusBarConfig(Resources r, AssetManager a) {
        mResources = r;
        mAssetManager = a;
    }

    @Override
    public int getStatusBarHeight() {
        return getInternalDimensionSize(mResources, RESOURCE_NAME_STATUS_BAR_HEIGHT);
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean drawGradient() {
        return false;
    }

    @Override
    public int getForegroundColour() {
        return mResources.getColor(R.color.jellybean_status_bar);
    }

    @Override
    public Typeface getFont() {
        return null;
    }

    @Override
    public float getFontSize() {
        return 16.0f;
    }

    @Override
    public Drawable getNetworkIconDrawable(int icon) {
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

    @Override
    public Drawable getGPSDrawable() {
        return getTintedDrawable(mResources, R.drawable.stat_sys_gps_on_16, getForegroundColour());
    }

    @Override
    public Drawable getWifiDrawable() {
        return getTintedDrawable(mResources, R.drawable.stat_sys_wifi_signal_4_fully, getForegroundColour());
    }

    @Override
    public int getRightPadding() {
        return dpToPx(6);
    }

    private int getBatteryViewWidth() {
        return dpToPx(10.5f);
    }

    private int getBatteryViewHeight() {
        return dpToPx(16);
    }

    @Override
    public int getNetworkIconPaddingOffset() {
        return 0;
    }

    @Override
    public int getWifiPaddingOffset() {
        return 0;
    }

    @Override
    public void setBatteryViewDimensions(View v) {
        v.getLayoutParams().width = getBatteryViewWidth();
        v.getLayoutParams().height = getBatteryViewHeight();
        ((LinearLayout.LayoutParams) v.getLayoutParams()).bottomMargin = dpToPx(0.33f);
    }

    protected int dpToPx(float dp) {
        return (int) (dp * mResources.getDisplayMetrics().density);
    }

    protected Drawable getTintedDrawable(Resources res, int drawableResId, int colour) {
        Drawable drawable = res.getDrawable(drawableResId);
        drawable.setColorFilter(colour, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
