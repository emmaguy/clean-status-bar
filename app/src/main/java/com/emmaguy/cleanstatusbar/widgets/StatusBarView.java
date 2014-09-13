package com.emmaguy.cleanstatusbar.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmaguy.cleanstatusbar.R;
import com.emmaguy.cleanstatusbar.util.StatusBarConfig;

public class StatusBarView extends LinearLayout {
    private TextView mTimeTextView;
    private BatteryMeterView mBatteryView;

    public StatusBarView(Context context) {
        this(context, null, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.RIGHT);

        LayoutInflater.from(context).inflate(R.layout.status_bar, this);

        mTimeTextView = (TextView) findViewById(R.id.status_bar_clock_textview);
        mBatteryView = (BatteryMeterView) findViewById(R.id.status_bar_battery);
    }

    public void setStatusBarConfig(StatusBarConfig statusBarConfig, int backgroundColour, String clockTime) {
        setClockTime(clockTime);
        setFont(statusBarConfig.getFont());
        setFontSize(statusBarConfig.getFontSize());
        setForegroundColour(statusBarConfig.getForegroundColour());

        if (statusBarConfig.shouldDrawGradient()) {
            Drawable[] layers = {new ColorDrawable(backgroundColour), getResources().getDrawable(R.drawable.gradient_bg)};
            LayerDrawable layerDrawable = new LayerDrawable(layers);

            setBackgroundDrawable(layerDrawable);
        } else {
            setBackgroundColor(backgroundColour);
        }
    }

    private void setClockTime(String time) {
        mTimeTextView.setText(time);
    }

    private void setFont(Typeface font) {
        mTimeTextView.setTypeface(font);
    }

    private void setFontSize(float fontSize) {
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void setForegroundColour(int foregroundColour) {
        mTimeTextView.setTextColor(foregroundColour);
        mBatteryView.setBatteryColour(foregroundColour);
    }
}
