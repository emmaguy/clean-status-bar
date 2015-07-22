package com.emmaguy.cleanstatusbar;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.emmaguy.cleanstatusbar.prefs.TimePreference;

/**
 * Created by emma on 21/06/15.
 */
public class CleanStatusBarPreferences {
    private final SharedPreferences mSharedPreferences;
    private final Resources mResources;

    public CleanStatusBarPreferences(SharedPreferences sharedPreferences, Resources resources) {
        mSharedPreferences = sharedPreferences;
        mResources = resources;
    }

    public int getApiValue() {
        String apiValue = mSharedPreferences.getString(mResources.getString(R.string.key_api_level), "");
        if (!TextUtils.isEmpty(apiValue)) {
            return Integer.valueOf(apiValue);
        }

        return Build.VERSION_CODES.LOLLIPOP;
    }

    public String getClockTime() {
        String[] time = mSharedPreferences.getString(mResources.getString(R.string.key_clock_time), TimePreference.DEFAULT_TIME_VALUE).split(":");
        if (!mSharedPreferences.getBoolean(mResources.getString(R.string.key_use_24_hour_format), false)) {
            if (Integer.parseInt(time[0]) > 12) {
                time[0] = String.valueOf(Integer.parseInt(time[0]) - 12);
            }
            if (time[0].length() == 1) {
                time[0] = "0" + time[0];
            }
        }
        return String.format("%s:%s", time[0], time[1]);
    }

    public int getBackgroundColour() {
        return mSharedPreferences.getInt(mResources.getString(R.string.key_background_colour), 0);
    }

    public boolean isKitKatGradientEnabled() {
        return mSharedPreferences.getBoolean(mResources.getString(R.string.key_kit_kat_gradient), false);
    }

    public boolean showGpsIcon() {
        return mSharedPreferences.getBoolean(mResources.getString(R.string.key_gps), false);
    }

    public boolean showWifiIcon() {
        return mSharedPreferences.getBoolean(mResources.getString(R.string.key_signal_wifi), false);
    }

    public int show3gIcon() {
        return Integer.parseInt(mSharedPreferences.getString(mResources.getString(R.string.key_signal_3g), "-1"));
    }

    public boolean isLightModeEnabled() {
        return mSharedPreferences.getBoolean(mResources.getString(R.string.key_m_light_status_bar), false);
    }
}
