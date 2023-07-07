package com.reyansh.easytouch.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.reyansh.easytouch.Fragments.FragmentsAdapter;
import com.reyansh.easytouch.R;

public class ArrangePanelActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_panel);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.panel_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.home_panel);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportActionBar().setTitle(R.string.home_panel);
                } else if (position == 1) {
                    getSupportActionBar().setTitle(R.string.setting_panel);
                } else if (position == 2) {
                    getSupportActionBar().setTitle(R.string.favorite_panel);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
