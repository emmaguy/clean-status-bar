package com.emmaguy.cleanstatusbar.config;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.emmaguy.cleanstatusbar.R;

public class LollipopStatusBarConfig extends DefaultStatusBarConfig {
    public LollipopStatusBarConfig(Resources r, AssetManager a) {
        super(r, a);
    }

    @Override
    public int getForegroundColour() {
        return mResources.getColor(android.R.color.white);
    }

    @Override
    public Typeface getFont() {
        return Typeface.createFromAsset(mAssetManager, "fonts/Roboto-Medium.ttf");
    }

    @Override
    public float getFontSize() {
        return 14f;
    }

    @Override
    public Drawable getNetworkIconDrawable(int icon) {
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
    }

    @Override
    public Drawable getGPSDrawable() {
        return getTintedDrawable(mResources, R.drawable.stat_sys_gps_on_19_plus, getForegroundColour());
    }

    @Override
    public Drawable getWifiDrawable() {
        return getTintedDrawable(mResources, R.drawable.stat_sys_wifi_signal_4_fully_l, getForegroundColour());
    }

    @Override
    public int getRightPadding() {
        return dpToPx(8);
    }

    private int getBatteryViewWidth() {
        return dpToPx(10);
    }

    private int getBatteryViewHeight() {
        return dpToPx(15.5f);
    }

    @Override
    public int getNetworkIconPaddingOffset() {
        return dpToPx(3);
    }

    @Override
    public int getWifiPaddingOffset() {
        return dpToPx(4);
    }

    @Override
    public void setBatteryViewDimensions(View v) {
        v.getLayoutParams().width = getBatteryViewWidth();
        v.getLayoutParams().height = getBatteryViewHeight();
    }
}
