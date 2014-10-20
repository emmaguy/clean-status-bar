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

public class MainActivity extends Activity {
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

    public static int getAPIValue(Context context, SharedPreferences prefs) {
        String apiValue = prefs.getString(context.getString(R.string.key_api_level), "");
        if (!TextUtils.isEmpty(apiValue)) {
            return Integer.valueOf(apiValue);
        }

        return Build.VERSION_CODES.LOLLIPOP;
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

            if (key.equals(getString(R.string.key_api_level))) {
                updateEnableKitKatGradientOption(sharedPreferences);
            } else if (key.equals(getString(R.string.key_use_24_hour_format))) {
                updateTimePreference();
            }
        }

        private void updateTimePreference() {
            CheckBoxPreference pref = (CheckBoxPreference) findPreference(getString(R.string.key_use_24_hour_format));

            TimePreference timePreference = (TimePreference) findPreference(getString(R.string.key_clock_time));
            timePreference.setIs24HourFormat(pref.isChecked());

            updatePrefsSummary(timePreference);
        }

        private void updateEnableKitKatGradientOption(SharedPreferences sharedPreferences) {
            boolean isKitKat = getAPIValue(getActivity(), sharedPreferences) == Build.VERSION_CODES.KITKAT;
            findPreference(getString(R.string.key_kit_kat_gradient)).setEnabled(isKitKat);
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
                CharSequence[] entryValues = lst.getEntryValues();
                if (index >= 0 && index < entries.length) {
                    // Show info explaining that the small letters e.g. 3G/LTE etc are only shown when WiFi is off - this is standard Android behaviour
                    boolean currentValueIsOffOrEmpty = currentValue.equals(entryValues[0]) || currentValue.equals(entryValues[1]);
                    if (pref.getKey().equals(getString(R.string.key_signal_3g)) && !currentValueIsOffOrEmpty) {
                        pref.setSummary(entries[index] + " - " + getString(R.string.network_icon_info));
                    } else {
                        pref.setSummary(entries[index]);
                    }
                }
            } else if (pref instanceof TimePreference) {
                if (pref.getKey().equals(getString(R.string.key_clock_time))) {
                    String time = getPreferenceManager().getSharedPreferences().getString(getString(R.string.key_clock_time), TimePreference.DEFAULT_TIME_VALUE);
                    pref.setSummary(time);
                }
            }
        }
    }
}
