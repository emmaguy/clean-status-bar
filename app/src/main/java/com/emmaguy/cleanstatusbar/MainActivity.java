package com.emmaguy.cleanstatusbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {
    private boolean mIsServiceRunning = false;
    private Button mStartStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStartStopButton = (Button) findViewById(R.id.button);
        mStartStopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            mIsServiceRunning = !mIsServiceRunning;

            if (mIsServiceRunning) {
                mStartStopButton.setText(R.string.back_to_normal);

                startService(new Intent(this, com.emmaguy.cleanstatusbar.CleanStatusBarService.class));
            } else {
                mStartStopButton.setText(R.string.clean_status_bar);

                stopService(new Intent(this, com.emmaguy.cleanstatusbar.CleanStatusBarService.class));
            }
        }
    }
}
