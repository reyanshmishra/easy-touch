package com.reyansh.easytouch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by reyansh on 1/27/18.
 */

public class FragmentsAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
        mFragmentTags = new HashMap<>();

        HomePanelFragment homePanelFragment = new HomePanelFragment();

        Bundle home = new Bundle();
        home.putString("FOR_WHICH", "HOME");
        homePanelFragment.setArguments(home);

        fragments.add(homePanelFragment);

        HomePanelFragment settingsPanelFragment = new HomePanelFragment();

        Bundle settings = new Bundle();
        settings.putString("FOR_WHICH", "SETTINGS");
        settingsPanelFragment.setArguments(settings);

        fragments.add(settingsPanelFragment);

        HomePanelFragment favoritesPanelFragment = new HomePanelFragment();

        Bundle favorites = new Bundle();
        favorites.putString("FOR_WHICH", "FAVORITES");
        favoritesPanelFragment.setArguments(favorites);

        fragments.add(favoritesPanelFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }


    @Override
    public int getCount() {
        return 3;
    }
}