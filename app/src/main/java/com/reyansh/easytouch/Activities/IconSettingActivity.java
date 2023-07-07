package com.reyansh.easytouch.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Dialogs.IconsDialog;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.PreferencesHelper;

/**
 * Created by reyansh on 1/21/18.
 */

public class IconSettingActivity extends AppCompatActivity {


    private SeekBar mSizeSeekBar;
    private SeekBar mAlphaSeekBar;
    private RelativeLayout mIconRelative;
    private Common mApp;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_icon);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.icon_settings);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mApp = (Common) getApplicationContext();
        mSizeSeekBar = findViewById(R.id.size_seekbar);
        mAlphaSeekBar = findViewById(R.id.alpha_seekbar);
        mIconRelative = findViewById(R.id.relative_icon);

        mAlphaSeekBar.setMax(155);
        mAlphaSeekBar.setProgress(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.ICON_ALPHA, 155));

        mSizeSeekBar.setMax(100);
        mSizeSeekBar.setProgress(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.ICON_SIZE, 0));


        mIconRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IconsDialog iconsDialog = new IconsDialog();
                iconsDialog.show(getSupportFragmentManager(), "SHOW_ICONS");

            }
        });


        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mApp.getOverLayService() != null) {
                    mApp.getOverLayService().setSize(100 + progress);
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ICON_SIZE, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mApp.getOverLayService() != null) {
                    mApp.getOverLayService().setAlpha(progress + 100);
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ICON_ALPHA, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


}
