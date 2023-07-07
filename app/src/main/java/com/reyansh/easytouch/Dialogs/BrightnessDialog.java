package com.reyansh.easytouch.Dialogs;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;

import com.reyansh.easytouch.Activities.ScreenShotActivity;
import com.reyansh.easytouch.BroadcastReceivers.DeviceAdmin;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.Constants;

/**
 * Created by reyansh on 1/21/18.
 */

public class BrightnessDialog extends DialogFragment {

    private SeekBar mBrightness;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.brightness_dialog, null);
        builder.setView(R.layout.brightness_dialog);

        mBrightness = view.findViewById(R.id.seekbar);
        mBrightness.setMax(255);

        mBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getActivity().finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().finish();
            }
        });
        return builder.create();
    }


    public void setScreenBrightness(int brightnessValue) {

        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }

}
