package com.reyansh.easytouch;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.util.DisplayMetrics;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.reyansh.easytouch.DataBase.DataBaseHelper;
import com.reyansh.easytouch.Services.AccService;
import com.reyansh.easytouch.Services.OverlayService;

/**
 * Created by reyansh on 1/15/18.
 */

public class Common extends Application {

    private static Context mContext;
    private AccService mAccessibilityService;

    private OverlayService mOverLayService;

    public boolean isServiceRunning = false;



    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static int convertDpToPixels(float dp) {
        Resources resources = getInstance().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public DataBaseHelper getDBAccessHelper() {
        return DataBaseHelper.getDatabaseHelper(mContext);
    }

    public static Context getInstance() {
        return mContext;
    }

    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

    public void setAccessibilityService(AccService accessibilityService) {
        mAccessibilityService = accessibilityService;
    }

    public AccService getAccessibilityService() {
        return mAccessibilityService;
    }

    public void setOverLayService(OverlayService overLayService) {
        mOverLayService = overLayService;
    }


    public OverlayService getOverLayService() {
        return mOverLayService;
    }


}
