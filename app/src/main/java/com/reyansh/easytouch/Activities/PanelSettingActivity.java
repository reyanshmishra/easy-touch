package com.reyansh.easytouch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Dialogs.ColorDialog;
import com.reyansh.easytouch.R;

/**
 * Created by reyansh on 1/21/18.
 */

public class PanelSettingActivity extends AppCompatActivity {


    private Common mApp;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_panel);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.panel_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mApp = (Common) getApplicationContext();
        findViewById(R.id.relative_panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PanelSettingActivity.this, ArrangePanelActivity.class));
            }
        });

        findViewById(R.id.relative_panel_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDialog colorPickerDialog = new ColorDialog();
                colorPickerDialog.show(getSupportFragmentManager(), "");
            }
        });

    }

}
