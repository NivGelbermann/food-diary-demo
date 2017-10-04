package com.nivgelbermann.fooddiarydemo;

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

    public static final String ARG_PAGE_POS = "ARG_PAGE_POS";
    public static final int OUTER_LOADER_ID = 0;
    public static final int INNER_LOADER_ID = 1;

    private OuterRecyclerViewAdapter mAdapter;
    //    // Represent the page number (tab identifier).
//    // Meaning, will always equal (tab position)
//    private int mPagePosition;
    // Variables for querying the relevant mMonth from DB
    private int mMonth;
    private int mYear;

    /**
     * @param page Page number to create.
     * @return {@link PageFragment} object that contains given page number.
     */
    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_POS, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pagePosition = getArguments().getInt(ARG_PAGE_POS);
        mYear = Constants.EPOCH + pagePosition / Constants.MONTHS_A_YEAR;
        mMonth = pagePosition % Constants.MONTHS_A_YEAR;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(OUTER_LOADER_ID, null, this);
//        getLoaderManager().initLoader(INNER_LOADER_ID, null, this);
//    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(OUTER_LOADER_ID, null, this);
        getLoaderManager().initLoader(INNER_LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        ButterKnife.bind(this, view);
        mAdapter = new OuterRecyclerViewAdapter(getContext());

        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        outerRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: " + mMonth + "/" + mYear + " starts with id: " + id);
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
//                // Sort by Hour -> Name
//                // 'ORDER BY Foods.Hour, Foods.FoodItem COLLATE NOCASE DESC'
//                sortOrder = FoodsContract.Columns.HOUR + ","
//                        + FoodsContract.Columns.FOOD_ITEM + " COLLATE NOCASE DESC";
                // Sort by Year -> Month -> Day -> Hour -> Name
                // 'ORDER BY Foods.Year, Foods.Month, Foods.Day, Foods.Hour, Foods.FoodItem COLLATE NOCASE DESC'
                sortOrder = FoodsContract.Columns.YEAR + ","
                        + FoodsContract.Columns.MONTH + ","
                        + FoodsContract.Columns.DAY + ","
                        + FoodsContract.Columns.HOUR + ","
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                mAdapter.swapCursor(data);
//                int count = mAdapter.getItemCount();
                break;

            case INNER_LOADER_ID:
                Log.d(TAG, "onLoadFinished: swapping inner cursors");
                mAdapter.swapInnerCursors(data);
//                int count = mAdapter.getInnerAdaptersCount();
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFinished called with invalid loader");
        }

//        Log.d(TAG, "onLoadFinished: ends with count: " + count);
        Log.d(TAG, "onLoadFinished: ends");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

        switch (loader.getId()) {
            case OUTER_LOADER_ID:
                mAdapter.swapCursor(null);
                break;

            case INNER_LOADER_ID:
                mAdapter.swapInnerCursors(null);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFinished called with invalid loader");
        }
    }
}
