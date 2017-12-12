package com.nivgelbermann.fooddiarydemo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;

import java.util.ArrayList;
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

    public MonthsStatePagerAdapter(FragmentManager fm, ArrayList<String> tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        // In case different layouts for fragments are required,
        // create separate class+layout files for each required fragment.
        // Then, in here, use a switch(position) to determine
        // which layout is to be used for each tab position.
        String[] segments = mTabTitles.get(position).split("/");
        int month = Integer.valueOf(segments[0]);
        int year = Integer.valueOf(segments[1]);
        return PageFragment.newInstance(month, year);
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Build and format tab title: MM/yy
        String[] segments = mTabTitles.get(position).split("/");
        int month = Integer.valueOf(segments[0]);
        int year = Integer.valueOf(segments[1]);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (month == currentMonth && year == currentYear) {
            return "THIS MONTH"; // TODO Convert to use string resource
        } else if ((month == currentMonth - 1 && year == currentYear)
                || (month == 11 && year == currentYear - 1)) {
            return "LAST MONTH"; // TODO Convert to use string resource
        }
        return utilFormatPageTitle(Integer.valueOf(segments[0]), Integer.valueOf(segments[1])); // TODO Change to local variables month & year
    }

    public int getCurrentMonthPosition() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        for (int i = 0; i < getCount(); i++) {
            String[] segments = mTabTitles.get(i).split("/");
            int month = Integer.valueOf(segments[0]);
            int year = Integer.valueOf(segments[1]);
            if (month == currentMonth && year == currentYear) {
                return i;
            }
        }
        return getCount()-1;
    }

    /**
     * Formats given integer values to "MM/yy"
     *
     * @param month Integer value of month
     * @param year  Integer value of year
     * @return formatted String
     */
    private String utilFormatPageTitle(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return String.format("%tm/%ty", calendar, calendar); // Cheat sheet: https://dzone.com/articles/java-string-format-examples
    }
}
