package com.reyansh.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Utils.Constants;

/**
 * Created by reyansh on 1/19/18.
 */

public class AccService extends AccessibilityService {

    Common mApp;

    @SuppressLint("RtlHardcoded")
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (Common) getApplicationContext();
        mApp.setAccessibilityService(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(Constants.TAG, "onServiceConnected");
    }

}