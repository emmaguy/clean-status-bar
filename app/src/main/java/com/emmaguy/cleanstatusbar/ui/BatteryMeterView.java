package com.emmaguy.cleanstatusbar.ui;

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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.emmaguy.cleanstatusbar.R;

public class BatteryMeterView extends View {
    public static final float SUBPIXEL = 0.4f;  // inset rects for softer edges

    Paint mFramePaint, mBatteryPaint;
    int mButtonHeight;

    private int mHeight;
    private int mWidth;

    private final RectF mFrame = new RectF();
    private final RectF mButtonFrame = new RectF();
    private final RectF mClipFrame = new RectF();
    private int mBatteryColour;

    public BatteryMeterView(Context context) {
        this(context, null, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFramePaint.setColor(getResources().getColor(R.color.batterymeter_frame_color));
        mFramePaint.setDither(true);
        mFramePaint.setStrokeWidth(0);
        mFramePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFramePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryPaint.setDither(true);
        mBatteryPaint.setStrokeWidth(0);
        mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
    }

    public void setBatteryColour(int colour) {
        mBatteryColour = colour;
    }

    @Override
    public void draw(Canvas c) {
        final int pt = getPaddingTop();
        final int pl = getPaddingLeft();
        final int pr = getPaddingRight();
        final int pb = getPaddingBottom();
        int height = mHeight - pt - pb;
        int width = mWidth - pl - pr;

        mButtonHeight = (int) (height * 0.12f);

        mFrame.set(0, 0, width, height);
        mFrame.offset(pl, pt);

        mButtonFrame.set(
                mFrame.left + width * 0.25f,
                mFrame.top,
                mFrame.right - width * 0.25f,
                mFrame.top + mButtonHeight + 5 /*cover frame border of intersecting area*/);

        mButtonFrame.top += SUBPIXEL;
        mButtonFrame.left += SUBPIXEL;
        mButtonFrame.right -= SUBPIXEL;

        mFrame.top += mButtonHeight;
        mFrame.left += SUBPIXEL;
        mFrame.top += SUBPIXEL;
        mFrame.right -= SUBPIXEL;
        mFrame.bottom -= SUBPIXEL;

        // first, draw the battery shape
        c.drawRect(mFrame, mFramePaint);

        // fill 'er up
        mBatteryPaint.setColor(mBatteryColour);

        c.drawRect(mButtonFrame, mBatteryPaint);

        mClipFrame.set(mFrame);

        c.save(Canvas.CLIP_SAVE_FLAG);
        c.clipRect(mClipFrame);
        c.drawRect(mFrame, mBatteryPaint);
        c.restore();
    }
}
