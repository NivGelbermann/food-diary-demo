package com.nivgelbermann.fooddiarydemo.ui;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.FoodRepository;
import com.nivgelbermann.fooddiarydemo.data.SectionHeaderDate;
import com.nivgelbermann.fooddiarydemo.data.database.AppDatabase;
import com.nivgelbermann.fooddiarydemo.data.database.FoodEntry;
import com.nivgelbermann.fooddiarydemo.ui.history.HistoryStatePagerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Defines a tab's layout.
 * <p>
 * In case different tab layouts are required,
 * create separate class+layout files for each required fragment.
 */

public class PageFragment extends Fragment
        implements HistoryStatePagerAdapter.QueryFragmentUpdateStatus {
    private static final String TAG = "PageFragment";

    @BindView(R.id.page_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.page_empty_rv_textview) TextView emptyDBMessage;

    public static final String PAGE_MONTH = "PageMonth";
    public static final String PAGE_YEAR = "PageYear";

    private SectionedAdapter mAdapter;
    // Variables for querying the relevant mMonth from DB
    private int mMonth;
    private int mYear;
    private boolean mUpdated;

    private FoodRepository mRepository;

    /**
     * @param month month represented by page
     * @param year  year represented by page
     * @return {@link PageFragment} object representing given month and year
     */
    public static PageFragment newInstance(int month, int year) {
        Bundle args = new Bundle();
        args.putInt(PAGE_MONTH, month);
        args.putInt(PAGE_YEAR, year);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, mMonth + "/" + mYear + " onCreate: called");
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException(TAG + " " + mMonth + "/" + mYear + " .onCreate called without arguments, cannot load required data");
        }
        mMonth = args.getInt(PAGE_MONTH);
        mYear = args.getInt(PAGE_YEAR);
        mUpdated = false;

        // TODO Implement custom/opensource injection and replace following line throughout the application:
        mRepository = FoodRepository.getInstance(AppDatabase.getInstance(getContext().getApplicationContext()).foodDao());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, mMonth + "/" + mYear + " onCreateView: PageFragment created");
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this, view);
//        if (!(getContext() instanceof InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener)) {
        if (!(getContext() instanceof SectionedAdapter.ChildViewHolder.FoodItemListener)) {
            throw new ClassCastException(getContext().getClass().getSimpleName()
                    + " must implement FoodItemListener interface");
        }

        List<SectionHeaderDate> dateList = getDateList(getFoodList());
        if (dateList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyDBMessage.setVisibility(View.VISIBLE);
            return view;
        }

        mAdapter = new SectionedAdapter(getContext(), dateList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true); // Helps performance optimization
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public boolean wasDataUpdated() {
        boolean updateStatus = mUpdated;
        mUpdated = false;
        return updateStatus;
    }

    /**
     * Retrieves a list of SectionHeaderDate objects, representing every date with food entries
     * for month represented by this PageFragment.
     *
     * @param sectionChildFoods ArrayList containing all food items for month
     * @return ArrayList containing all dates for month
     */
    private ArrayList<SectionHeaderDate> getDateList(List<FoodEntry> sectionChildFoods) {
        Log.d(TAG, mMonth + "/" + mYear + " getDatesList: called");
        if (sectionChildFoods == null || sectionChildFoods.isEmpty()) {
            return new ArrayList<>();
        }
        /*
        ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver == null) {
            throw new IllegalStateException(TAG + " " + mMonth + "/" + mYear + " .onHandleWork: couldn't get ContentResolver.");
        }

        String[] projection = new String[]{"DISTINCT " + FoodsContract.Columns.DAY,
                FoodsContract.Columns.MONTH,
                FoodsContract.Columns.YEAR};
        String selection = FoodsContract.Columns.MONTH + "=? AND "
                + FoodsContract.Columns.YEAR + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMonth), String.valueOf(mYear)};
        String sortOrder = FoodsContract.Columns.YEAR + ","
                + FoodsContract.Columns.MONTH + ","
                + FoodsContract.Columns.DAY + " DESC";
        Cursor cursor = contentResolver.query(FoodsContract.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        if (cursor == null || cursor.getCount() == 0) {
            throw new IllegalStateException(TAG + " " + mMonth + "/" + mYear + " getDatesList: couldn't move cursor to first");
        }

        ArrayList<SectionHeaderDate> headerDates = new ArrayList<>();
        while (cursor.moveToNext()) {
            // Get current day's food items
            final int day = cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY));
            Collection<SectionChildFood> filteredCollection = Collections2.filter(sectionChildFoods,
                    new Predicate<SectionChildFood>() {
                        @Override
                        public boolean apply(SectionChildFood input) {
                            return input.getDay() == day;
                        }
                    });
            List<SectionChildFood> items = new ArrayList<>(filteredCollection);

            // Add new SectionHeaderDate to headerDates list, containing said items
            headerDates.add(new SectionHeaderDate(
                    items,
                    day,
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR))));
        }
        cursor.close();
        */

        ArrayList<SectionHeaderDate> headerDates = new ArrayList<>();
        ArrayList<Integer> dates = (ArrayList<Integer>) mRepository
                .getDatesByMonth(mMonth, mYear).getValue();
        for (final int day : dates) {
            // Get current day's food items
            Collection<FoodEntry> filteredCollection = Collections2.filter(sectionChildFoods,
                    new Predicate<FoodEntry>() {
                        @Override
                        public boolean apply(FoodEntry input) {
                            return input.getDay() == day;
                        }
                    });
            List<FoodEntry> entriesForDay = new ArrayList<>(filteredCollection);

            // Add new SectionHeaderDate to headerDates list, containing said items
            headerDates.add(new SectionHeaderDate(entriesForDay, day, mMonth, mYear));
        }

        mUpdated = true;
        return headerDates;
    }

    private ArrayList<FoodEntry> getFoodList() {
        /*
        ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver == null) {
            throw new IllegalStateException(TAG + " " + mMonth + "/" + mYear + " .onHandleWork: couldn't get ContentResolver.");
        }

        String[] projection = new String[]{FoodsContract.Columns._ID,
                FoodsContract.Columns.FOOD_ITEM,
                FoodsContract.Columns.DAY,
                FoodsContract.Columns.MONTH,
                FoodsContract.Columns.YEAR,
                FoodsContract.Columns.HOUR,
                FoodsContract.Columns.CATEGORY_ID};
        String selection = FoodsContract.Columns.MONTH + "=? AND "
                + FoodsContract.Columns.YEAR + "=?";
        String[] selectionArgs = new String[]{
                String.valueOf(mMonth),
                String.valueOf(mYear)};
        String sortOrder = FoodsContract.Columns.HOUR + " DESC,"
                + FoodsContract.Columns.FOOD_ITEM + " COLLATE NOCASE DESC";
        Cursor cursor = contentResolver.query(FoodsContract.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, mMonth + "/" + mYear + " getFoodList: cursor null or empty. Assuming no entries exist for requested month.");
            return new ArrayList<>();
        }
        ArrayList<SectionChildFood> entries = new ArrayList<>();
        while (cursor.moveToNext()) {
            entries.add(new SectionChildFood(
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns._ID)),
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                    cursor.getLong(cursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID))));
        }
        cursor.close();
        */

        LiveData<List<FoodEntry>> data = mRepository.getByMonth(mMonth, mYear);
        ArrayList<FoodEntry> entries = (ArrayList<FoodEntry>) data.getValue();

        return entries;
    }

    /**
     * Refreshes page display.
     */
    public void updateDisplay() {
        Log.d(TAG, "updateDisplay: called");

        List<SectionHeaderDate> dateList = getDateList(getFoodList());
        if (dateList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyDBMessage.setVisibility(View.VISIBLE);
            mAdapter = null;
            return;
        }

        emptyDBMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (mAdapter == null) {
            mAdapter = new SectionedAdapter(getContext(), dateList);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataChanged(dateList);
        }
    }
}
