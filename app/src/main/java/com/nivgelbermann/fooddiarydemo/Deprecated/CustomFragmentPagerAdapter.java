package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Niv on 22-Aug-17.
 *
 * DEPRECATED
 */

class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "CustomFragmentPagerAdap";

    // TEMPORARY measure of finite, pre-determined tabLayout
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Tab 1", "Tab 2", "Tab 3"};
    private Context mContext; // Redundant

    public CustomFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position+1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
