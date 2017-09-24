package com.nivgelbermann.fooddiarydemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Niv on 21-Aug-17.
 *
 * DEPRECATED
 */

class PagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "PagerAdapter";

    private int mTabsCount;

    public PagerAdapter(FragmentManager manager, int tabsCount) {
        super(manager);
        this.mTabsCount = tabsCount;
    }

    @Override
    public Fragment getItem(int position) {
        // In case different tabLayout fragments are required,
        // create seperate class+layout files for each required fragment.
        // Then, in here, use a switch(position) to determine
        // which layout is to be used for each tab position.
        if (position >= 0 && position < mTabsCount) {
            return new PageFragment();
        }
        return null;
//        return new PageFragment();
    }

    @Override
    public int getCount() {
        return mTabsCount;
    }
}