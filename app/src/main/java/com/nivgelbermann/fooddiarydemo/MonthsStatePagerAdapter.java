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
    private static final String CURRENT_POSITION = "CurrentTab";

    // Contains the strings for each tab title
    private List<String> mTabTitles;
//    private int mCurrentPosition;

    public MonthsStatePagerAdapter(FragmentManager fm, List<String> tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
//        mCurrentPosition = getCount() - 1;
    }

//    @Override
//    public Parcelable saveState() {
//        Bundle state = (Bundle) super.saveState();
//        state.putInt(CURRENT_POSITION, mCurrentPosition);
//        return state;
//    }
//
//    @Override
//    public void restoreState(Parcelable state, ClassLoader loader) {
//        Bundle bundle = (Bundle) state;
//        mCurrentPosition = bundle.getInt(CURRENT_POSITION);
//        super.restoreState(state, loader);
//    }
//
//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        if (position != mCurrentPosition) {
//            mCurrentPosition = position;
//        }
//        super.setPrimaryItem(container, position, object);
//    }

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
        if (position == getCount() - 1) {
            return "THIS MONTH";
        } else if (position == getCount() - 2) {
            return "LAST MONTH";
        }
        return mTabTitles.get(position);
    }

//    public int getCurrentPosition() {
//        return mCurrentPosition;
//    }

    // TODO Implement only loading a page when it's tab is selected
    // Meaning not loading ALL pages when app starts
}
