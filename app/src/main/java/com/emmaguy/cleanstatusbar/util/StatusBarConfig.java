package com.emmaguy.cleanstatusbar.util;

import android.content.res.Resources;

/*
 * Adapted from https://github.com/jgilfelt/SystemBarTint
 *
 * Copyright 2013 readyState Software Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class StatusBarConfig {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private final Resources mResources;

    public StatusBarConfig(Resources r) {
        mResources = r;
    }

    public int getStatusBarHeight() {
        return getInternalDimensionSize(mResources, STATUS_BAR_HEIGHT_RES_NAME);
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
