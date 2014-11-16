package com.emmaguy.cleanstatusbar.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.emmaguy.cleanstatusbar.R;

// Adapted from https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial/TimePreference.java
public class TimePreference extends DialogPreference {
    public static final String DEFAULT_TIME_VALUE = "12:00";

    private int mLastHour = 0;
    private int mLastMinute = 0;
    private String mTime = DEFAULT_TIME_VALUE;

    private TimePicker mTimePicker = null;

    private boolean mIs24HourFormat;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(R.string.set_time);
        setNegativeButtonText(R.string.cancel);
    }

    public void setIs24HourFormat(boolean is24HourFormat) {
        mIs24HourFormat = is24HourFormat;

        updateTime();
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(true);
        return mTimePicker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        mTimePicker.setCurrentHour(mLastHour);
        mTimePicker.setCurrentMinute(mLastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            updateTime();
        }
    }

    private void updateTime() {
        if (mTimePicker != null) {
            mLastHour = mTimePicker.getCurrentHour();
            mLastMinute = mTimePicker.getCurrentMinute();
        } else {
            String time = getPersistedString(DEFAULT_TIME_VALUE);
            mLastHour = getHour(time);
            mLastMinute = getMinute(time);
        }

        String hourValue = String.valueOf(mLastHour);
        if (mIs24HourFormat) {
            hourValue = toTimeDigits(mLastHour);
        } else {
            if (mLastHour > 12) {
                hourValue = toTimeDigits(String.valueOf(mLastHour - 12));
            }
        }

        mTime = hourValue + ":" + toTimeDigits(mLastMinute);

        if (callChangeListener(mTime)) {
            persistString(String.format("%s:%s", String.valueOf(mLastHour), toTimeDigits(mLastMinute)));
        }
    }

    private String toTimeDigits(int i) {
        String digit = String.valueOf(i);
        return toTimeDigits(digit);
    }

    private String toTimeDigits(String digit) {
        if (digit.length() == 1) {
            digit = "0" + digit;
        }
        return digit;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_TIME_VALUE);
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        mLastHour = getHour(time);
        mLastMinute = getMinute(time);
    }

    private static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    private static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public String getTime() {
        return mTime;
    }
}
