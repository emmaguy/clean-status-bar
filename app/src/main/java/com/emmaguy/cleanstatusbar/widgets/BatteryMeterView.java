package com.emmaguy.cleanstatusbar.widgets;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.emmaguy.cleanstatusbar.R;

public class BatteryMeterView extends View {
    private float mButtonHeightFraction;
    private float mSubpixelSmoothingLeft;
    private float mSubpixelSmoothingRight;
    private final Paint mFramePaint, mBatteryPaint;

    private int mHeight;
    private int mWidth;

    private final RectF mFrame = new RectF();
    private final RectF mButtonFrame = new RectF();
    private final Path mShapePath = new Path();
    private int mBatteryColour;

    public BatteryMeterView(Context context) {
        this(context, null, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mButtonHeightFraction = context.getResources().getFraction(R.fraction.battery_button_height_fraction, 1, 1);
        mSubpixelSmoothingLeft = context.getResources().getFraction(R.fraction.battery_subpixel_smoothing_left, 1, 1);
        mSubpixelSmoothingRight = context.getResources().getFraction(R.fraction.battery_subpixel_smoothing_right, 1, 1);

        mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFramePaint.setColor(getResources().getColor(R.color.batterymeter_frame_color));
        mFramePaint.setDither(true);
        mFramePaint.setStrokeWidth(0);
        mFramePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryPaint.setDither(true);
        mBatteryPaint.setStrokeWidth(0);
        mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mHeight = h;
        mWidth = w;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas c) {
        final int pt = getPaddingTop();
        final int pl = getPaddingLeft();
        final int pr = getPaddingRight();
        final int pb = getPaddingBottom();
        final int height = mHeight - pt - pb;
        final int width = mWidth - pl - pr;

        final int buttonHeight = (int) (height * mButtonHeightFraction);

        mFrame.set(0, 0, width, height);
        mFrame.offset(pl, pt);

        // button-frame: area above the battery body
        mButtonFrame.set(mFrame.left + Math.round(width * 0.25f),
                mFrame.top,
                mFrame.right - Math.round(width * 0.25f),
                mFrame.top + buttonHeight);

        mButtonFrame.top += mSubpixelSmoothingLeft;
        mButtonFrame.left += mSubpixelSmoothingLeft;
        mButtonFrame.right -= mSubpixelSmoothingRight;

        // frame: battery body area
        mFrame.top += buttonHeight;
        mFrame.left += mSubpixelSmoothingLeft;
        mFrame.top += mSubpixelSmoothingLeft;
        mFrame.right -= mSubpixelSmoothingRight;
        mFrame.bottom -= mSubpixelSmoothingRight;

        // set the battery charging color
        mBatteryPaint.setColor(mBatteryColour);

        // define the battery shape
        mShapePath.reset();
        mShapePath.moveTo(mButtonFrame.left, mButtonFrame.top);
        mShapePath.lineTo(mButtonFrame.right, mButtonFrame.top);
        mShapePath.lineTo(mButtonFrame.right, mFrame.top);
        mShapePath.lineTo(mFrame.right, mFrame.top);
        mShapePath.lineTo(mFrame.right, mFrame.bottom);
        mShapePath.lineTo(mFrame.left, mFrame.bottom);
        mShapePath.lineTo(mFrame.left, mFrame.top);
        mShapePath.lineTo(mButtonFrame.left, mFrame.top);
        mShapePath.lineTo(mButtonFrame.left, mButtonFrame.top);

        // draw the battery shape background
        c.drawPath(mShapePath, mFramePaint);

        mFrame.top = mButtonFrame.top;
        c.drawPath(mShapePath, mBatteryPaint);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setBatteryColour(int colour) {
        mBatteryColour = colour;
        invalidate();
    }
}

