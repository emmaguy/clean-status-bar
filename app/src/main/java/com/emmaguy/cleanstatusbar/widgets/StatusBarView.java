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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmaguy.cleanstatusbar.R;
import com.emmaguy.cleanstatusbar.util.StatusBarConfig;

public class StatusBarView extends LinearLayout {
    private final ImageView m3gView;
    private final ImageView mWifiView;
    private final TextView mTimeTextView;
    private final BatteryMeterView mBatteryView;

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
        m3gView = (ImageView) findViewById(R.id.status_bar_3g);
        mWifiView = (ImageView) findViewById(R.id.status_bar_wifi);
    }

    public void setStatusBarConfig(StatusBarConfig statusBarConfig, int backgroundColour, String clockTime, boolean shouldShowWifi, boolean shouldShould3g) {
        setClockTime(clockTime);
        setFont(statusBarConfig.getFont());
        setFontSize(statusBarConfig.getFontSize());
        setForegroundColour(statusBarConfig.getForegroundColour());

        if(shouldShould3g) {
            m3gView.setVisibility(View.VISIBLE);
            m3gView.setImageDrawable(statusBarConfig.get3gDrawable());
        } else {
            m3gView.setVisibility(View.GONE);
        }

        if(shouldShowWifi) {
            if(shouldShould3g) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWifiView.getLayoutParams();
                params.setMargins(0, 0, dpToPx(-6), 0);
                mWifiView.setPadding(0, 0, 0, 0);
                mWifiView.setLayoutParams(params);
            } else {
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mWifiView.setPadding(0, 0, dpToPx(2), 0);
                mWifiView.setLayoutParams(params);
            }
            mWifiView.setVisibility(View.VISIBLE);
            mWifiView.setImageDrawable(statusBarConfig.getWifiDrawable());
        } else {
            mWifiView.setVisibility(View.GONE);
        }

        if (statusBarConfig.shouldDrawGradient()) {
            Drawable[] layers = {new ColorDrawable(backgroundColour), getResources().getDrawable(R.drawable.gradient_bg)};
            LayerDrawable layerDrawable = new LayerDrawable(layers);

            setBackgroundDrawable(layerDrawable);
        } else {
            setBackgroundColor(backgroundColour);
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setClockTime(String time) {
        mTimeTextView.setText(time);
    }

    private void setFont(Typeface font) {
        mTimeTextView.setTypeface(font);
    }

    // Note: must be dp to fit in status bar
    private void setFontSize(float fontSize) {
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    private void setForegroundColour(int foregroundColour) {
        mTimeTextView.setTextColor(foregroundColour);
        mBatteryView.setBatteryColour(foregroundColour);
    }
}
