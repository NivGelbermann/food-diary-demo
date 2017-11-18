package com.nivgelbermann.fooddiarydemo.fragments;

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

import java.security.InvalidParameterException;

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

    @BindView(R.id.outerRecyclerView) RecyclerView outerRecyclerView;

    //    public static final String ARG_PAGE_POS = "ARG_PAGE_POS";
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

//    /**
//     * @param page Page number to create.
//     * @return {@link PageFragment} object that contains given page number.
//     */
//    public static PageFragment newInstance(int page) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE_POS, page);
//        PageFragment fragment = new PageFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * @param monthAndYear String in format of "month/year"
     * @return {@link PageFragment} object that contains given page number.
     */
    public static PageFragment newInstance(String monthAndYear) {
        if (!monthAndYear.contains("/")) {
            throw new InvalidParameterException(TAG + ".newInstance received invalid monthAndYear string parameter, which does not contain \"/\" divider");
        }
        String[] segments = monthAndYear.split("/");
        Bundle args = new Bundle();
        args.putInt(PAGE_MONTH, Integer.valueOf(segments[0]));
        args.putInt(PAGE_YEAR, Integer.valueOf(segments[1]));
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int pagePosition = getArguments().getInt(ARG_PAGE_POS);
//        mYear = Constants.EPOCH + pagePosition / Constants.MONTHS_A_YEAR;
//        mMonth = pagePosition % Constants.MONTHS_A_YEAR;
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException(TAG + ".onCreate called without arguments, cannot load required data");
        }
        mMonth = args.getInt(PAGE_MONTH);
        mYear = args.getInt(PAGE_YEAR);
//        mFragmentLoaded = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: PageFragment created for MM/yy: " + mMonth + "/" + mYear);
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this, view);
        if (!(getContext() instanceof InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener)) {
            throw new ClassCastException(getContext().getClass().getSimpleName()
                    + " must implement FoodItemListener interface");
        }
        mAdapter = new OuterRecyclerViewAdapter((InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener) getContext());

        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        outerRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mIsStarted = true;
        if (mIsVisible) {
            utilInitLoaders();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStarted = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        if (mIsVisible && mIsStarted) {
            utilInitLoaders();
        } else {
            mIsStarted = false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: " + mMonth + "/" + mYear + " called with id: " + id);
        String[] projection;
        String sortOrder;

        String selection = FoodsContract.Columns.MONTH + "=? AND "
                + FoodsContract.Columns.YEAR + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMonth), String.valueOf(mYear)};

        switch (id) {
            case OUTER_LOADER_ID:
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
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        Log.d(TAG, "onLoadFinished: starts");
//        Log.d(TAG, "onLoadFinished: " + mMonth + "/" + mYear + " finished with id: " + id);

        if (cursor == null) {
            throw new InvalidParameterException(TAG + ".onLoadFinished called with null cursor");
        }

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                Log.d(TAG, "onLoadFinished: " + mMonth + "/" + mYear + " starts with id OUTER_LOADER_ID");
                mAdapter.swapCursor(cursor);
//                int count = mAdapter.getItemCount();
                break;

            case INNER_LOADER_ID:
                Log.d(TAG, "onLoadFinished: " + mMonth + "/" + mYear + " starts with id INNER_LOADER_ID");
                Log.d(TAG, "onLoadFinished: swapping inner cursors");
                mAdapter.swapInnerCursors(cursor);
//                int count = mAdapter.getInnerAdaptersCount();
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFinished called with invalid loader");
        }

//        Log.d(TAG, "onLoadFinished: ends with count: " + count);
        Log.d(TAG, "onLoadFinished: ends with item count: " + mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                mAdapter.swapCursor(null);
                break;

            case INNER_LOADER_ID:
                mAdapter.swapInnerCursors(null);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoaderReset called with invalid loader");
        }
    }

    private void utilInitLoaders() {
        Log.d(TAG, "utilInitLoaders: called, initiating loaders");
        getLoaderManager().initLoader(OUTER_LOADER_ID, null, this);
        getLoaderManager().initLoader(INNER_LOADER_ID, null, this);
    }
}
