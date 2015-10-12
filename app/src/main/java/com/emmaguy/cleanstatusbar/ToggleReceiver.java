package com.emmaguy.cleanstatusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ToggleReceiver extends BroadcastReceiver {
    private static final String EXTRA_ENABLED = "enabled";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, CleanStatusBarService.class);
        if (intent.getBooleanExtra(EXTRA_ENABLED, true)) {
            context.startService(service);
        } else {
            context.stopService(service);
        }
    }
}
