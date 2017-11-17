package com.nivgelbermann.fooddiarydemo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Niv on 08-Sep-17.
 * <p>
 * Creates scrollable tabs according to given list of tab titles.
 * Each tab's fragment is handled by {@link PageFragment}.
 */

public class MonthsStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MonthsStatePagerAdapter";

    // Contains the strings for each tab title in format: month/year
    private List<String> mTabTitles;

    public MonthsStatePagerAdapter(FragmentManager fm, List<String> tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
        // TODO Make this class database-aware      https://medium.com/inloop/adventures-with-fragmentstatepageradapter-4f56a643f8e0
    }

    @Override
    public Fragment getItem(int position) {
        // In case different layouts for fragments are required,
        // create separate class+layout files for each required fragment.
        // Then, in here, use a switch(position) to determine
        // which layout is to be used for each tab position.
//        return PageFragment.newInstance(position);
        return PageFragment.newInstance(mTabTitles.get(position));
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == getCount() - 1) {
            return "THIS MONTH";
        } else if (position == getCount() - 2) {
            return "LAST MONTH";
        }
        // Build and format tab title: MM/yy
        // Cheat sheet: https://dzone.com/articles/java-string-format-examples
        String[] segments = mTabTitles.get(position).split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.valueOf(segments[0]));
        calendar.set(Calendar.YEAR, Integer.valueOf(segments[1]));
        return String.format("%tm/%ty", calendar, calendar);
    }
}
