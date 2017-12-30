package com.nivgelbermann.fooddiarydemo.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.adapters.InnerRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.adapters.OuterRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nivgelbermann.fooddiarydemo.services.TableRetrieverService;

import java.security.InvalidParameterException;
import java.util.ArrayList;

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

public class PageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "PageFragment";

    @BindView(R.id.page_outer_recyclerView) RecyclerView outerRecyclerView;

    public static final String PAGE_MONTH = "PageMonth";
    public static final String PAGE_YEAR = "PageYear";
    private static final int OUTER_LOADER_ID = 0;
    private static final int INNER_LOADER_ID = 1;

    private OuterRecyclerViewAdapter mAdapter;
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

//        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        mResponseReceiver = new ResponseReceiver();
//        getContext().registerReceiver(mResponseReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, mMonth + "/" + mYear + " onCreateView: PageFragment created");
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this, view);
        if (!(getContext() instanceof InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener)) {
            throw new ClassCastException(getContext().getClass().getSimpleName()
                    + " must implement FoodItemListener interface");
        }
//        mAdapter = new OuterRecyclerViewAdapter((InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener) getContext());
        mAdapter = new OuterRecyclerViewAdapter(getContext(), getList());

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

    @Override
    public void onStart() {
        Log.d(TAG, mMonth + "/" + mYear + " onStart: starts");
        super.onStart();
        mIsStarted = true;
        if (mIsVisible) {
            Log.d(TAG, mMonth + "/" + mYear + " onStart: page is visible, initiating loaders");
            initilalizeLoaders();
        }
        Log.d(TAG, mMonth + "/" + mYear + " onStart: ends");
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStarted = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, mMonth + "/" + mYear + " " + "setUserVisibleHint: starts");
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        if (mIsVisible && mIsStarted) {
            Log.d(TAG, mMonth + "/" + mYear + " " + "setUserVisibleHint: page is visible and is started, initiating loaders");
            initilalizeLoaders();
        } else {
            Log.d(TAG, mMonth + "/" + mYear + " " + "setUserVisibleHint: page isn't started");
            mIsStarted = false;
        }
        Log.d(TAG, mMonth + "/" + mYear + " " + "setUserVisibleHint: ends");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection;
        String sortOrder;

        String selection = FoodsContract.Columns.MONTH + "=? AND "
                + FoodsContract.Columns.YEAR + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMonth), String.valueOf(mYear)};

        switch (id) {
            case OUTER_LOADER_ID:
                Log.d(TAG, mMonth + "/" + mYear + " onCreateLoader: called with id OUTER_LOADER ID");
                // SOME UGLY SHIT SQL injection - alternative solution: https://stackoverflow.com/questions/24877815/distinct-query-for-cursorloader
                projection = new String[]{"DISTINCT " + FoodsContract.Columns.DAY,
                        FoodsContract.Columns.MONTH,
                        FoodsContract.Columns.YEAR};
                // Sort by Year -> Month -> Day
                // 'ORDER BY Foods.Year, Foods.Month, Foods.Day DESC'
                sortOrder = FoodsContract.Columns.YEAR + ","
                        + FoodsContract.Columns.MONTH + ","
                        + FoodsContract.Columns.DAY + " DESC";
                return new CursorLoader(getActivity(),
                        FoodsContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);

            case INNER_LOADER_ID:
                Log.d(TAG, mMonth + "/" + mYear + " onCreateLoader: called with id INNER_LOADER_ID");
                projection = new String[]{FoodsContract.Columns._ID,
                        FoodsContract.Columns.FOOD_ITEM,
                        FoodsContract.Columns.DAY,
                        FoodsContract.Columns.MONTH,
                        FoodsContract.Columns.YEAR,
                        FoodsContract.Columns.HOUR,
                        FoodsContract.Columns.CATEGORY_ID};
                // Sort by Year -> Month -> Day -> Hour -> Name
                // 'ORDER BY Foods.Year, Foods.Month, Foods.Day, Foods.Hour, Foods.FoodItem COLLATE NOCASE DESC'
                sortOrder = FoodsContract.Columns.HOUR + " DESC,"
                        + FoodsContract.Columns.FOOD_ITEM + " COLLATE NOCASE DESC";
                return new CursorLoader(getActivity(),
                        FoodsContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);

            // Add cases for other Loader Id's for loading information from other databases

            default:
                throw new InvalidParameterException(TAG + " " + mMonth + "/" + mYear + " .onCreateLoader called with invalid loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            throw new InvalidParameterException(TAG + " " + mMonth + "/" + mYear + " .onLoadFinished called with null cursor");
        }

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                Log.d(TAG, mMonth + "/" + mYear + " onLoadFinished: starts with id OUTER_LOADER_ID");
                mAdapter.swapCursor(cursor);
//                int count = mAdapter.getItemCount();
                break;

            case INNER_LOADER_ID:
                Log.d(TAG, mMonth + "/" + mYear + " onLoadFinished: starts with id INNER_LOADER_ID");
                Log.d(TAG, mMonth + "/" + mYear + " onLoadFinished: swapping inner cursors");
                mAdapter.swapInnerCursors(cursor);
//                int count = mAdapter.getInnerAdaptersCount();
                break;

            default:
                throw new InvalidParameterException(TAG + " " + mMonth + "/" + mYear + " .onLoadFinished called with invalid loader");
        }

//        Log.d(TAG, "onLoadFinished: ends with count: " + count);
        Log.d(TAG, mMonth + "/" + mYear + " onLoadFinished: ends with item count: " + mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, mMonth + "/" + mYear + " onLoaderReset: called");

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                mAdapter.swapCursor(null);
                break;

            case INNER_LOADER_ID:
                mAdapter.swapInnerCursors(null);
                break;

            default:
                throw new InvalidParameterException(TAG + " " + mMonth + "/" + mYear + " .onLoaderReset called with invalid loader");
        }
    }

    /**
     * Small method for better readability when calling it.
     * Initializes all loaders for PageFragment.
     */
    private void initilalizeLoaders() {
        Log.d(TAG, mMonth + "/" + mYear + " initilalizeLoaders: called, initiating loaders");
        getLoaderManager().initLoader(OUTER_LOADER_ID, null, this);
        getLoaderManager().initLoader(INNER_LOADER_ID, null, this);
    }

    private ArrayList<FoodItem> getList() {
        ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver == null) {
            throw new IllegalStateException(TAG + ".onHandleWork: couldn't get ContentResolver.");
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
        if (cursor == null || !cursor.moveToFirst()) {
            throw new IllegalStateException(TAG + "onHandleWork: Couldn't move cursor to first");
        }
        ArrayList<FoodItem> items = new ArrayList<>();
        do {
            items.add(new FoodItem(
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns._ID)),
                    cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                    cursor.getLong(cursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR)),
                    cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID))));
        } while (cursor.moveToNext());
        cursor.close();
        return items;
    }


    public class ResponseReceiver extends BroadcastReceiver {
        private static final String TAG = "ResponseReceiver";

        public static final String ACTION_RESP = "com.nivgelbermann.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: starts");
            try {
                mFoodItems = (ArrayList<FoodItem>)
                        intent.getSerializableExtra(TableRetrieverService.LIST_RESULT);
            } catch (ClassCastException e) {
                Log.d(TAG, "onReceive: could not cast received object to ArrayList<FoodItem>.");
            }
            Log.d(TAG, "onReceive: ends");
        }
    }

}
