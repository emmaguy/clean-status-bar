package com.emmaguy.cleanstatusbar.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
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
import com.emmaguy.cleanstatusbar.config.StatusBarConfig;

public class StatusBarView extends LinearLayout {
    private static final int NETWORK_STATUS_ICON_OFF = 0;
    private final ImageView m3gView;
    private final ImageView mWifiView;
    private final ImageView mGPSView;
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
        setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        LayoutInflater.from(context).inflate(R.layout.status_bar, this);

        mTimeTextView = (TextView) findViewById(R.id.status_bar_clock_textview);
        mBatteryView = (BatteryMeterView) findViewById(R.id.status_bar_battery);
        m3gView = (ImageView) findViewById(R.id.status_bar_3g);
        mWifiView = (ImageView) findViewById(R.id.status_bar_wifi);
        mGPSView = (ImageView) findViewById(R.id.status_bar_gps);
    }

    public void setStatusBarConfig(StatusBarConfig statusBarConfig, int backgroundColour, String clockTime, boolean shouldShowWifi, int icon3G, boolean shouldShowGps) {
        setPadding(0, 0, statusBarConfig.getRightPadding(), 0);
        statusBarConfig.setBatteryViewDimensions(mBatteryView);

        setClockTime(clockTime);
        setFont(statusBarConfig.getFont());
        setFontSize(statusBarConfig.getFontSize());
        setForegroundColour(statusBarConfig.getForegroundColour());

        if (shouldShowGps) {
            mGPSView.setVisibility(View.VISIBLE);
            mGPSView.setPadding(0, dpToPx(5), 0, dpToPx(5));
            mGPSView.setImageDrawable(statusBarConfig.getGPSDrawable());
        } else {
            mGPSView.setVisibility(View.GONE);
        }

        if(icon3G >= 0) {
            m3gView.setVisibility(View.VISIBLE);
            m3gView.setImageDrawable(statusBarConfig.getNetworkIconDrawable(icon3G));
            m3gView.setPadding(0, 0, statusBarConfig.getNetworkIconPaddingOffset(), 0);
        } else {
            m3gView.setVisibility(View.GONE);
        }

        if(shouldShowWifi) {
            if(icon3G >= 0) {
                m3gView.setImageDrawable(statusBarConfig.getNetworkIconDrawable(NETWORK_STATUS_ICON_OFF));
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWifiView.getLayoutParams();
                params.setMargins(0, 0, dpToPx(-6), 0);
                mWifiView.setPadding(0, 0, statusBarConfig.getWifiPaddingOffset(), 0);
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

        if (statusBarConfig.drawGradient()) {
            Drawable[] layers = {new ColorDrawable(backgroundColour), getResources().getDrawable(R.drawable.status_background)};
            LayerDrawable layerDrawable = new LayerDrawable(layers);

            setBackgroundAndKeepPadding(this, layerDrawable);
        } else {
            setBackgroundColor(backgroundColour);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackgroundAndKeepPadding(View view, Drawable backgroundDrawable) {
        Rect drawablePadding = new Rect();
        backgroundDrawable.getPadding(drawablePadding);
        int top = view.getPaddingTop() + drawablePadding.top;
        int left = view.getPaddingLeft() + drawablePadding.left;
        int right = view.getPaddingRight() + drawablePadding.right;
        int bottom = view.getPaddingBottom() + drawablePadding.bottom;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(backgroundDrawable);
        } else {
            view.setBackgroundDrawable(backgroundDrawable);
        }
        view.setPadding(left, top, right, bottom);
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
