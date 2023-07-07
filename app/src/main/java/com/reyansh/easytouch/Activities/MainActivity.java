package com.reyansh.easytouch.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.reyansh.easytouch.AsyncTasks.InstalledAppsAsyncTask;
import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Services.AccService;
import com.reyansh.easytouch.Services.OverlayService;
import com.reyansh.easytouch.Utils.Constants;

public class MainActivity extends Activity {

    private Button mStartStopButton;
    private Common mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (Common) getApplicationContext();


        mStartStopButton = findViewById(R.id.start_stop_button);

        new InstalledAppsAsyncTask(this).execute();

        findViewById(R.id.panel_setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PanelSettingActivity.class));
            }
        });


        findViewById(R.id.icon_setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, IconSettingActivity.class);
                startActivity(intent1);
            }
        });

        mStartStopButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mApp.getOverLayService() != null) {
                            stopService(new Intent(MainActivity.this, OverlayService.class));
                            stopService(new Intent(MainActivity.this, AccService.class));
                            mStartStopButton.setText(R.string.start);
                        } else {
                            Intent intent = new Intent(MainActivity.this, OverlayService.class);
                            startService(intent);

                            Intent accessIntent = new Intent(MainActivity.this, AccService.class);
                            startService(accessIntent);
                            mStartStopButton.setText(R.string.stop);
                        }
                    }
                });


        findViewById(R.id.uninstall_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                startActivityForResult(intent, Constants.UNINSTALL_REQUEST_CODE);
            }
        });


        if (mApp.isServiceRunning) {
            mStartStopButton.setText(R.string.stop);
        } else {
            mStartStopButton.setText(R.string.start);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Toast.makeText(mApp, R.string.failed_to_uninstall, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startService() {

//        Intent intent = new Intent(this, OverlayService.class);
//        startService(intent);
//
//        Intent accessIntent = new Intent(this, AccService.class);
//        startService(accessIntent);

    }
}
