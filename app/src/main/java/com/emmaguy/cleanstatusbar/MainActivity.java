package com.emmaguy.cleanstatusbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.emmaguy.cleanstatusbar.ui.TimePreference;


public class MainActivity extends Activity {
    public static final String PREFS_KEY_CLOCK_TIME = "clock_time";
    public static final String PREFS_KEY_BACKGROUND_COLOUR = "background_colour";
    public static final String PREFS_KEY_FOREGROUND_COLOUR = "foreground_colour";
    public static final String PREFS_KEY_DRAW_GRADIENT = "draw_gradient";

    private static final String PREFS_KEY_TOGGLE_ON_OFF = "toggle_on_off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
        private boolean mIsServiceRunning = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs);

            initialiseClickListener(PREFS_KEY_TOGGLE_ON_OFF);

            updateClockTimeTitle();
        }

        private void updateClockTimeTitle() {
            Preference clockTime = findPreference(PREFS_KEY_CLOCK_TIME);
            if (clockTime != null) {
                clockTime.setTitle(getString(R.string.clock_time) + " - " + getPreferenceManager().getSharedPreferences().getString(PREFS_KEY_CLOCK_TIME, TimePreference.DEFAULT_TIME_VALUE));
            }
        }

        private void initialiseClickListener(String key) {
            Preference resetPref = findPreference(key);
            if (resetPref != null) {
                resetPref.setOnPreferenceClickListener(this);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            toggleCleanStatusBarService(true);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(PREFS_KEY_TOGGLE_ON_OFF)) {
                toggleCleanStatusBarService(!mIsServiceRunning);
                return true;
            }
            return false;
        }

        private void toggleCleanStatusBarService(boolean status) {
            mIsServiceRunning = status;

            if (mIsServiceRunning) {
                startService();
            } else {
                stopService();
            }
        }

        private void stopService() {
            getActivity().stopService(new Intent(getActivity(), CleanStatusBarService.class));
        }

        private void startService() {
            getActivity().startService(new Intent(getActivity(), CleanStatusBarService.class));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PREFS_KEY_CLOCK_TIME)) {
                updateClockTimeTitle();
            }

            if (mIsServiceRunning) {
                stopService();
                startService();
            }
        }
    }
}
