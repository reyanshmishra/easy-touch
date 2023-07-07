package com.reyansh.easytouch.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.PreferencesHelper;


/**
 * Created by reyansh on 12/18/17.
 */

public class ColorDialog extends DialogFragment {

    private ColorPickerPalette mColorPickerPalette;
    private int mColor;
    private Common mApp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_color, null);
        final int[] colors = Common.getInstance().getResources().getIntArray(R.array.colors);
        mApp = (Common) getActivity().getApplicationContext();
        mColor = PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.PANEL_COLOR, Color.parseColor("#000000"));
        mColorPickerPalette = view.findViewById(R.id.palette);
        mColorPickerPalette.setSelected(true);
        mColorPickerPalette.init(2, 6, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mColor = color;
                mColorPickerPalette.drawPalette(colors, mColor);
            }
        });

        mColorPickerPalette.drawPalette(colors, mColor);

        builder.setTitle(R.string.select_background_color);
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.PANEL_COLOR, mColor);
                if (mApp.isServiceRunning) {
                    mApp.getOverLayService().setPanelBackgroundColor(mColor);
                }

            }
        });

        return builder.create();
    }
}
