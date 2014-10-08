package com.emmaguy.cleanstatusbar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.emmaguy.cleanstatusbar.prefs.TimePreference;
import com.emmaguy.cleanstatusbar.util.StatusBarConfig;

public class MainActivity extends Activity {
    public static final String PREFS_KEY_API_VALUE = "api_level";
    public static final String PREFS_KEY_CLOCK_TIME = "clock_time";
    public static final String PREFS_KEY_USE_24_HOUR_FORMAT = "use_24_hour";
    public static final String PREFS_KEY_KIT_KAT_GRADIENT = "enable_kitkat_gradient";
    public static final String PREFS_KEY_BACKGROUND_COLOUR = "background_colour";
    public static final String PREFS_KEY_SIGNAL_3G = "signal_network_icon";
    public static final String PREFS_KEY_SIGNAL_WIFI = "signal_wifi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSwitch();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    private void initSwitch() {
        Switch masterSwitch = new Switch(this);
        masterSwitch.setChecked(CleanStatusBarService.isRunning());
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startService(MainActivity.this);
                } else {
                    stopService(MainActivity.this);
                }
            }
        });

        final ActionBar bar = getActionBar();
        final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.master_switch_margin_right);
        bar.setCustomView(masterSwitch, lp);
        bar.setDisplayShowCustomEnabled(true);
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, CleanStatusBarService.class));
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, CleanStatusBarService.class));
    }

    public static int getAPIValue(SharedPreferences prefs) {
        String apiValue = prefs.getString(MainActivity.PREFS_KEY_API_VALUE, "");
        if (!TextUtils.isEmpty(apiValue)) {
            return Integer.valueOf(apiValue);
        }

        return StatusBarConfig.VERSION_CODE_L_DEVELOPER_PREVIEW;
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs);

            initSummary();
            updateEnableKitKatGradientOption(getPreferenceManager().getSharedPreferences());
            updateTimePreference();
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefsSummary(findPreference(key));

            if (CleanStatusBarService.isRunning()) {
                stopService(getActivity());
                startService(getActivity());
            }

            if (key.equals(PREFS_KEY_API_VALUE)) {
                updateEnableKitKatGradientOption(sharedPreferences);
            } else if (key.equals(PREFS_KEY_USE_24_HOUR_FORMAT)) {
                updateTimePreference();
            }
        }

        private void updateTimePreference() {
            CheckBoxPreference pref = (CheckBoxPreference) findPreference(PREFS_KEY_USE_24_HOUR_FORMAT);

            TimePreference timePreference = (TimePreference) findPreference(PREFS_KEY_CLOCK_TIME);
            timePreference.setIs24HourFormat(pref.isChecked());

            updatePrefsSummary(timePreference);
        }

        private void updateEnableKitKatGradientOption(SharedPreferences sharedPreferences) {
            boolean isKitKat = getAPIValue(sharedPreferences) == Build.VERSION_CODES.KITKAT;
            findPreference(PREFS_KEY_KIT_KAT_GRADIENT).setEnabled(isKitKat);
        }

        protected void initSummary() {
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initPrefsSummary(getPreferenceScreen().getPreference(i));
            }
        }

        protected void initPrefsSummary(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory cat = (PreferenceCategory) p;
                for (int i = 0; i < cat.getPreferenceCount(); i++) {
                    initPrefsSummary(cat.getPreference(i));
                }
            } else {
                updatePrefsSummary(p);
            }
        }

        protected void updatePrefsSummary(Preference pref) {
            if (pref == null) {
                return;
            }

            if (pref instanceof ListPreference) {
                ListPreference lst = (ListPreference) pref;
                String currentValue = lst.getValue();

                int index = lst.findIndexOfValue(currentValue);
                CharSequence[] entries = lst.getEntries();
                if (index >= 0 && index < entries.length) {
                    pref.setSummary(entries[index]);
                }
            } else if (pref instanceof TimePreference) {
                if (pref.getKey().equals(PREFS_KEY_CLOCK_TIME)) {
                    String time = getPreferenceManager().getSharedPreferences().getString(PREFS_KEY_CLOCK_TIME, TimePreference.DEFAULT_TIME_VALUE);
                    pref.setSummary(time);
                }
            }
        }
    }
}
