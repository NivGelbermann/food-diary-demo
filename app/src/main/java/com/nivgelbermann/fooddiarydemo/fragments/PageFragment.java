package com.nivgelbermann.fooddiarydemo.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.adapters.SectionedRVAdapter;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.DateHeader;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 21-Aug-17.
 * <p>
 * Defines a tab's layout.
 * <p>
 * In case different tab layouts are required,
 * create separate class+layout files for each required fragment.
 */

public class PageFragment extends Fragment {
    private static final String TAG = "PageFragment";

    @BindView(R.id.page_outer_recyclerView) RecyclerView outerRecyclerView;

    public static final String PAGE_MONTH = "PageMonth";
    public static final String PAGE_YEAR = "PageYear";
    private static final int OUTER_LOADER_ID = 0;
    private static final int INNER_LOADER_ID = 1;

    //    private OuterRecyclerViewAdapter mAdapter;
    private SectionedRVAdapter mAdapter;
    // Variables for querying the relevant mMonth from DB
    private int mMonth;
    private int mYear;
    private boolean mIsStarted;
    private boolean mIsVisible;

    //    private ResponseReceiver mResponseReceiver;
    private ArrayList<FoodItem> mFoodItems;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, mMonth + "/" + mYear + " onCreateView: PageFragment created");
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this, view);
//        if (!(getContext() instanceof InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener)) {
        if (!(getContext() instanceof SectionedRVAdapter.ChildViewHolder.FoodItemListener)) {
            throw new ClassCastException(getContext().getClass().getSimpleName()
                    + " must implement FoodItemListener interface");
        }
        mAdapter = new SectionedRVAdapter(getContext(), getDateList(getFoodList()));

        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO Compile to phone with above line commented and below lines un-commented. Check whether scrolling animation is actually smoother.
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        manager.setItemPrefetchEnabled(true);
//        manager.setInitialPrefetchItemCount(10);
//        outerRecyclerView.setLayoutManager(manager);
        outerRecyclerView.setHasFixedSize(true); // Helps performance optimization
        outerRecyclerView.setAdapter(mAdapter);

        return view;
    }

    /**
     * Retrieves a list of DateHeader objects, representing every date with food entries
     * for month represented by this PageFragment.
     *
     * @param foodItems ArrayList containing all food items for month
     * @return ArrayList containing all dates for month
     */
    private ArrayList<DateHeader> getDateList(List<FoodItem> foodItems) { // TODO Can this be changed to use mFoodItems?
        Log.d(TAG, mMonth + "/" + mYear + " getDatesList: called");
        if (foodItems.isEmpty()) {
            return new ArrayList<>();
        }
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

        ArrayList<DateHeader> dates = new ArrayList<>();
        while (cursor.moveToNext()) {
            // Get current day's food items
            final int day = cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY));
            Collection<FoodItem> filteredCollection = Collections2.filter(foodItems,
                    new Predicate<FoodItem>() {
                        @Override
                        public boolean apply(FoodItem input) {
                            return input.getDay() == day;
                        }
                    });
            List<FoodItem> items = new ArrayList<>(filteredCollection);

            // Add new DateHeader to dates list, containing said items
            dates.add(new DateHeader(
                    items,
                    day,
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR))));
        }

        return dates;
    }

    private ArrayList<FoodItem> getFoodList() {
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
            // TODO Delete all items from current month, then deal with exception thrown here
//            throw new IllegalStateException(TAG + " getFoodList: cursor is null.");
            Log.d(TAG, mMonth + "/" + mYear + " getFoodList: cursor null or empty. Assuming no items exist for requested month.");
            return new ArrayList<>();
        }
        ArrayList<FoodItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(new FoodItem(
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns._ID)),
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                    cursor.getLong(cursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID))));
        }
        cursor.close();
        return items;
    }

    /**
     * Refreshes items display.
     */
    public void updateDisplay() {
        Log.d(TAG, "updateDisplay: called");
        mAdapter.notifyDataChanged(getDateList(getFoodList()));
    }
}
