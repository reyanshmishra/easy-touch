package com.reyansh.easytouch.Activities;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.reyansh.easytouch.Dialogs.AdminAccessDialogFragment;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.Constants;

public class LockScreenActivity extends AppCompatActivity {

    private DevicePolicyManager mDeviceManger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        mDeviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        AdminAccessDialogFragment adminAccessDialogFragment = new AdminAccessDialogFragment();
        adminAccessDialogFragment.show(getSupportFragmentManager(), "LOCK_SCREEN_DIALOG");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.ADMIN_ACCESS:
                if (resultCode == Activity.RESULT_OK) {
                    mDeviceManger.lockNow();
                    finish();
                } else {
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}