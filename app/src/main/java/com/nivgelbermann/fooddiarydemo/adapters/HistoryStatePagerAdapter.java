package com.nivgelbermann.fooddiarydemo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Creates scrollable tabs according to given list of tab titles.
 * Each tab's fragment is handled by {@link PageFragment}.
 */

public class HistoryStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "HistoryStatePagerAdapte";

    // Contains the strings for each tab title in format: month/year
    private List<String> mTabTitles;
    private WeakReference<Context> mContext;

    /**
     * Interface for communicating with child fragments.
     * Used for querying whether fragment data was changed, and its display needs to be updated.
     */
    public interface QueryFragmentUpdateStatus {
        /**
         * Checks whether fragment data was updated, then resets its update status.
         * @return true if fragment display needs to be updated, otherwise false.
         */
        public boolean wasDataUpdated();
    }

    public HistoryStatePagerAdapter(FragmentManager fm, ArrayList<String> tabTitles, Context context) {
        super(fm);
        mTabTitles = tabTitles;
        mContext = new WeakReference<>(context);
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
            return mContext.get().getResources().getString(R.string.this_month_upp);
        } else if ((month == currentMonth - 1 && year == currentYear)
                || (month == 11 && year == currentYear - 1)) {
            return mContext.get().getResources().getString(R.string.last_month_upp);
        }
        return formatPageTitle(month, year);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Log.d(TAG, "getItemPosition: called");
        PageFragment fragment = (PageFragment) object;
        if(fragment.wasDataUpdated()) {
            Log.d(TAG, "getItemPosition: ended with POSITION_NONE");
            return POSITION_NONE;
        }
        Log.d(TAG, "getItemPosition: ended with POSITION_UNCHANGED");
        return POSITION_UNCHANGED;
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
        return getCount() - 1;
    }

    /**
     * Formats given integer values to "MM/yy"
     *
     * @param month Integer value of month
     * @param year  Integer value of year
     * @return formatted String
     */
    private String formatPageTitle(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return String.format("%tm/%ty", calendar, calendar); // Cheat sheet: https://dzone.com/articles/java-string-format-examples
    }
}
