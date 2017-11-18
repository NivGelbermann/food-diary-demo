package com.nivgelbermann.fooddiarydemo.adapters;

import android.database.Cursor;
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

    public interface MonthsLoadingListener {
        public void onMonthsLoadFinished();
    }

    //    public MonthsStatePagerAdapter(FragmentManager fm, List<String> tabTitles) {
    public MonthsStatePagerAdapter(FragmentManager fm) {
        super(fm);
//        mTabTitles = tabTitles;
        mTabTitles = new ArrayList<>();
        // TODO Make this class database-aware      https://medium.com/inloop/adventures-with-fragmentstatepageradapter-4f56a643f8e0
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
//        return mTabTitles.size();
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
        String[] segments = mTabTitles.get(position).split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.valueOf(segments[0]));
        calendar.set(Calendar.YEAR, Integer.valueOf(segments[1]));
        return String.format("%tm/%ty", calendar, calendar); // Cheat sheet: https://dzone.com/articles/java-string-format-examples
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
        // TODO Implement
    }

    public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor: starts");
        if (newCursor == mCursor) {
            Log.d(TAG, "swapCursor: ends, returning null because cursor hasn't changed");
            return null;
        }

        final Cursor oldCursor = mCursor;
        notifyDataSetChanged();
        Log.d(TAG, "swapCursor: ends, returning old cursor");
        return oldCursor;
    }
}
