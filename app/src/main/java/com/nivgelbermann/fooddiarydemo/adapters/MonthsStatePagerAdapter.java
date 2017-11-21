package com.nivgelbermann.fooddiarydemo.adapters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
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
    private Cursor mCursor;

    public MonthsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // In case different layouts for fragments are required,
        // create separate class+layout files for each required fragment.
        // Then, in here, use a switch(position) to determine
        // which layout is to be used for each tab position.
        return PageFragment.newInstance(mTabTitles.get(position));
    }

    @Override
    public int getCount() {
//        return mTabTitles.size();
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == getCount() - 1) {
            return "THIS MONTH";
        } else if (position == getCount() - 2) {
            return "LAST MONTH";
        }
        // Build and format tab title: MM/yy
        if (mTabTitles == null || mTabTitles.size() == 0) {
            // The next line sometimes throws a runtime exception. For example: "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0"
            String[] segments = mTabTitles.get(position).split("/");
            return utilFormatPageTitle(Integer.valueOf(segments[0]), Integer.valueOf(segments[1]));
        } else {
            return "HOYL DANG";
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        PageFragment fragment = (PageFragment) object;
        Bundle args = fragment.getArguments();
        if (args != null) {
            String title = utilFormatPageTitle(args.getInt(PageFragment.PAGE_MONTH),
                    args.getInt(PageFragment.PAGE_YEAR));
            for (int i = 0; i < mTabTitles.size(); i++) {
                if (mTabTitles.get(i).equals(title)) {
                    return i;
                }
            }
        }
        return POSITION_NONE;
    }

    public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor: starts");
        if (newCursor == mCursor) {
            Log.d(TAG, "swapCursor: ends, returning null because cursor hasn't changed");
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;

        mTabTitles = new ArrayList<>();
        if (mCursor == null || mCursor.getCount() == 0) {
            Log.d(TAG, "MonthsStatePagerAdapter: mCursor null or empty");
        } else {
            while (mCursor.moveToNext()) {
                // TODO This method generates tabs only for months that exist in the DB. Make it generate a tab for every month since the FIRST IN THE DB to the CURRENT DATE
                // TODO Add empty database scenario
                mTabTitles.add(mCursor.getString(
                        mCursor.getColumnIndex(FoodsContract.Columns.MONTH)) + "/"
                        + mCursor.getString(mCursor.getColumnIndex(FoodsContract.Columns.YEAR)));
            }
        }

        notifyDataSetChanged();
        Log.d(TAG, "swapCursor: ends, returning old cursor");
        return oldCursor;
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
