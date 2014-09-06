package com.emmaguy.cleanstatusbar.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;
import com.emmaguy.cleanstatusbar.R;

public class StatusBarView extends LinearLayout {
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
        setBackgroundColor(getResources().getColor(android.R.color.black));
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.RIGHT);

        LinearLayout batteryAndClockLayout = new LinearLayout(context);
        batteryAndClockLayout.setOrientation(LinearLayout.HORIZONTAL);
        batteryAndClockLayout.setLayoutParams(getBatteryAndClockLayoutParams());

        initialiseBatteryView(context, batteryAndClockLayout);
        initialiseTimeTextView(context, batteryAndClockLayout);

        addView(batteryAndClockLayout);
    }

    private void initialiseBatteryView(Context context, ViewGroup parent) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.height = dpToPx(16);
        params.width = dpToPx(10.5f);
        params.bottomMargin = dpToPx(0.33f);
        params.leftMargin = dpToPx(4);
        params.gravity = Gravity.CENTER_VERTICAL;

        com.emmaguy.cleanstatusbar.views.BatteryMeterView batteryView = new com.emmaguy.cleanstatusbar.views.BatteryMeterView(context);
        batteryView.setLayoutParams(params);
        batteryView.setBatteryColour(getResources().getColor(R.color.battery_fill_light_grey));

        parent.addView(batteryView);
    }

    private void initialiseTimeTextView(Context context, ViewGroup parent) {
        TextView timeTextView = new RobotoTextView(context);
        timeTextView.setText("12:00");
        timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        timeTextView.setTextColor(getResources().getColor(R.color.battery_fill_light_grey));
        Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                context,
                RobotoTypefaceManager.FontFamily.ROBOTO,
                RobotoTypefaceManager.TextWeight.MEDIUM,
                RobotoTypefaceManager.TextStyle.NORMAL);
        RobotoTextViewUtils.setTypeface(timeTextView, typeface);

        LayoutParams timeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        timeParams.gravity = Gravity.CENTER_VERTICAL;
        timeTextView.setLayoutParams(timeParams);
        timeTextView.setPadding(dpToPx(6), 0, dpToPx(5), 0);

        parent.addView(timeTextView);
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
