package com.nivgelbermann.fooddiarydemo.ui.history;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.SectionChildFood;
import com.nivgelbermann.fooddiarydemo.ui.add_edit.AddEditActivity;
import com.nivgelbermann.fooddiarydemo.ui.SectionedRvAdapter;
import com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated.FoodsContract;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment
        extends Fragment
        implements SectionedRvAdapter.ChildViewHolder.FoodItemListener {
    private static final String TAG = "HistoryFragment";

    //    @BindView(R.id.history_toolbar) Toolbar toolbar;
    @BindView(R.id.history_pager) ViewPager viewPager;
    //    @BindView(R.id.history_fab) FloatingActionButton fab;
    @BindView(R.id.history_tab_layout) TabLayout tabLayout;
    @BindView(R.id.history_empty_pager_textview) TextView emptyDBMessage;

    private ArrayList<String> mTabTitles;
    private HistoryStatePagerAdapter mPagerAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);

        mTabTitles = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (contentResolver == null) {
            throw new IllegalStateException(TAG + ".onCreate: couldn't get ContentResolver.");
        }
        String[] projection = new String[]{"DISTINCT " + FoodsContract.Columns.MONTH,
                FoodsContract.Columns.YEAR};
        String sortOrder = FoodsContract.Columns.YEAR + ","
                + FoodsContract.Columns.MONTH + " ASC";
        Cursor cursor = contentResolver.query(
                FoodsContract.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "onCreate: cursor null or empty");
        } else {
            // TODO This method generates tabs only for months that exist in the DB. Make it generate a tab for every month since the FIRST IN THE DB to the CURRENT DATE
            // Right now, if the DB contains items in June and August but no items in July, a tab for July would not be created
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);
            while (cursor.moveToNext()) {
                String month = cursor.getString(
                        cursor.getColumnIndex(FoodsContract.Columns.MONTH));
                String year = cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.YEAR));
                if (Integer.valueOf(month) == currentMonth && Integer.valueOf(year) == currentYear) {
                    break;
                }
                mTabTitles.add(month + "/" + year);
            }
            cursor.close();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        mPagerAdapter =
                new HistoryStatePagerAdapter(getChildFragmentManager(), mTabTitles, getContext());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(mPagerAdapter.getCurrentMonthPosition());
        tabLayout.setupWithViewPager(viewPager);

        if (mTabTitles.size() == 0) {
            toggleEmptyDisplay();
        }

        Log.d(TAG, "onCreateView: ends, returning view");
        return view;
    }

    @Override
    public void onFoodItemClicked(SectionChildFood item) {
        startAddEditActivity(item);
    }

    @Override
    public boolean onFoodItemLongClicked(SectionChildFood item) {
        // Ignore long clicks, consume event by returning true
        return true;
    }

    /**
     * Utility method to start an AddEdit activity.
     *
     * @param item Pass item to edit it, otherwise pass null to create a new item
     */
    private void startAddEditActivity(SectionChildFood item) {
        Log.d(TAG, "startAddEditActivity: called");

        Intent addEditIntent = new Intent(getActivity(), AddEditActivity.class);
        if (item != null) {
            addEditIntent.putExtra(SectionChildFood.class.getSimpleName(), item);
        }
        startActivity(addEditIntent);
    }

    /**
     * Refreshes pages display.
     */
    public void updateDisplay() {
        Log.d(TAG, "updateDisplay: called");
        toggleEmptyDisplay();
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Switches between displaying empty database message, and history tabs,
     * according the what's currently displayed.
     */
    private void toggleEmptyDisplay() {
        // TODO When adding items to an empty HistoryFragment, make it update correctly and display new items.
        // Right now it hides empty DB message and displays tabs, but the tabs themselves aren't updated correctly.
        Log.d(TAG, "toggleEmptyDisplay: called");
        if (/*mPagerAdapter.getCount() == 0 && */emptyDBMessage.getVisibility() == View.GONE) {
            Log.d(TAG, "toggleEmptyDisplay: display set to empty");
            emptyDBMessage.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        } else if (/*mPagerAdapter.getCount() != 0 && */ emptyDBMessage.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "toggleEmptyDisplay: display to to items");
            emptyDBMessage.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }
}

// TODO Hide ActionBar, leave tabs visible (like in Tasks To Do app)
// https://stackoverflow.com/questions/33009127/hide-tablayout-on-scroll-of-content-instead-of-toolbar
// https://android.jlelse.eu/scrolling-behavior-for-appbars-in-android-41aff9c5c468
// https://mzgreen.github.io/2015/06/23/How-to-hideshow-Toolbar-when-list-is-scrolling(part3)/