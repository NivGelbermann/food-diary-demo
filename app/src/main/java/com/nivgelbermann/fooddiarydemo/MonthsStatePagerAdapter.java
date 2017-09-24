package com.nivgelbermann.fooddiarydemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Niv on 08-Sep-17.
 * <p>
 * Creates scrollable tabs according to given list of tab titles.
 * Each tab's fragment is handled by {@link PageFragment}.
 */

class MonthsStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MonthsStatePagerAdapter";

    // Contains the strings for each tab title
    private List<String> mTabTitles;

    public MonthsStatePagerAdapter(FragmentManager fm, List<String> tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        // In case different layouts for fragments are required,
        // create separate class+layout files for each required fragment.
        // Then, in here, use a switch(position) to determine
        // which layout is to be used for each tab position.
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Generate title based on item position + set return value accordingly
        return mTabTitles.get(position);
    }

    // TODO Implement only loading a page when it's tab is selected
    // Meaning not loading ALL pages when app starts
}
