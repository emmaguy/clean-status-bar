package com.emmaguy.cleanstatusbar.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;
import com.emmaguy.cleanstatusbar.R;

public class StatusBarView extends LinearLayout {
    private RobotoTextView mTimeTextView;
    private BatteryMeterView mBatteryView;

    public StatusBarView(Context context) {
        this(context, null, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.RIGHT);

        LinearLayout batteryAndClockLayout = new LinearLayout(context);
        batteryAndClockLayout.setOrientation(LinearLayout.HORIZONTAL);
        batteryAndClockLayout.setLayoutParams(getBatteryAndClockLayoutParams());

        initialiseBatteryView(context, batteryAndClockLayout);
        initialiseTimeTextView(context, batteryAndClockLayout);

        setForegroundColour(getResources().getColor(R.color.battery_fill_light_grey));

        addView(batteryAndClockLayout);
    }

    private void initialiseBatteryView(Context context, ViewGroup parent) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.height = dpToPx(16);
        params.width = dpToPx(10.5f);
        params.bottomMargin = dpToPx(0.33f);
        params.leftMargin = dpToPx(4);
        params.gravity = Gravity.CENTER_VERTICAL;

        mBatteryView = new com.emmaguy.cleanstatusbar.ui.BatteryMeterView(context);
        mBatteryView.setLayoutParams(params);

        parent.addView(mBatteryView);
    }

    private void initialiseTimeTextView(Context context, ViewGroup parent) {
        mTimeTextView = new RobotoTextView(context);
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        setFont(context, true);

        LayoutParams timeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        timeParams.gravity = Gravity.CENTER_VERTICAL;
        mTimeTextView.setLayoutParams(timeParams);
        mTimeTextView.setPadding(dpToPx(6), 0, dpToPx(5), 0);

        parent.addView(mTimeTextView);
    }

    public void setFont(Context context, boolean isMediumWeight) {
        Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                context,
                RobotoTypefaceManager.FontFamily.ROBOTO,
                isMediumWeight ? RobotoTypefaceManager.TextWeight.MEDIUM : RobotoTypefaceManager.TextWeight.NORMAL,
                RobotoTypefaceManager.TextStyle.NORMAL);
        RobotoTextViewUtils.setTypeface(mTimeTextView, typeface);
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, isMediumWeight ? 16 : 17);
    }

    public void setForegroundColour(int foregroundColour) {
        mTimeTextView.setTextColor(foregroundColour);
        mBatteryView.setBatteryColour(foregroundColour);
    }

    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    public ViewGroup.LayoutParams getBatteryAndClockLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        return params;
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
