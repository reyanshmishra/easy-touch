package com.reyansh.easytouch.AsyncTasks;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;

import com.reyansh.easytouch.Activities.MainActivity;
import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.DataBase.DataBaseHelper;
import com.reyansh.easytouch.R;

import java.util.List;

/**
 * Created by reyansh on 1/27/18.
 */

public class InstalledAppsAsyncTask extends AsyncTask<Void, Void, Void> {
    private PackageManager mPackageManager;
    private List<ApplicationInfo> mApplicationInfos;
    private MainActivity mContext;
    private Common mApp;

    public InstalledAppsAsyncTask(MainActivity context) {
        mContext = context;
        mApp = (Common) mContext.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Cursor easyAllApps = mApp.getDBAccessHelper().getAllEasyApps();
        if (easyAllApps != null && easyAllApps.getCount() == 0) {

            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_signal_wifi_off, 0, "", "Wifi");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_lock, 1, "", "Lock");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.drawable_fullscreen, 2, "", "Screenshot");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_star, 3, "", "Favorites");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_rocket, 4, "", "Clean");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_device, 5, "", "Device");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_recent, 6, "", "Recent");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_circle, 7, "", "Home");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_arrow_back, 8, "", "Back");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_silent, 9, "", "Silent");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_bluetooth_disabled, 10, "", "Bluetooth");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_sound, 11, "", "Sound");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_location_on, 12, "", "Location");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_volume_up, 13, "", "Volume Up");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_brightness, 14, "", "Brightness");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_bulb, 15, "", "Flashlight");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_volume_down, 16, "", "Volume Down");
            mApp.getDBAccessHelper().addEasyApps(R.drawable.ic_screen_rotation, 17, "", "Auto Rotate");

            mPackageManager = mContext.getPackageManager();
            mApplicationInfos = mPackageManager.getInstalledApplications(0);

            int orderWeight = 18;

            for (ApplicationInfo app : mApplicationInfos) {
                orderWeight++;
                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 && (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = (String) mPackageManager.getApplicationLabel(app);
                    mApp.getDBAccessHelper().addEasyApps(0, orderWeight, app.packageName, appName);
                }
            }
        }

        if (easyAllApps != null)
            easyAllApps.close();


        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mContext.startService();
    }
}
