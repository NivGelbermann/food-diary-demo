package com.nivgelbermann.fooddiarydemo.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.activities.AddEditActivity;
import com.nivgelbermann.fooddiarydemo.adapters.InnerRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.adapters.MonthsStatePagerAdapter;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment
        extends Fragment
        implements InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener {
    private static final String TAG = "HistoryFragment";

    @BindView(R.id.history_toolbar) Toolbar toolbar;
    @BindView(R.id.history_pager) ViewPager viewPager;
//    @BindView(R.id.history_fab) FloatingActionButton fab;
    @BindView(R.id.history_recycler_tab_layout) RecyclerTabLayout recyclerTabLayout;

    private MonthsStatePagerAdapter mPagerAdapter;

    public static HistoryFragment newInstance() {
        // TODO Fill out
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        ArrayList<String> tabTitles = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (contentResolver != null) {
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
                // TODO Add empty database scenario
                while (cursor.moveToNext()) {
                    tabTitles.add(cursor.getString(
                            cursor.getColumnIndex(FoodsContract.Columns.MONTH)) + "/"
                            + cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.YEAR)));
                }
            }
        }
        // Remove current month, as HistoryFragment only displays PAST months.
        // This seems more effective performance-wise than
        // checking each item added to tabTitles in a loop.
        tabTitles.remove(tabTitles.size()-1);

        mPagerAdapter =
                new MonthsStatePagerAdapter(getActivity().getSupportFragmentManager(), tabTitles);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(mPagerAdapter.getCurrentMonthPosition());
        recyclerTabLayout.setUpWithViewPager(viewPager);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                utilStartAddEditActivity(null);
//            }
//        });

        Log.d(TAG, "onCreateView: ends, returning view");
        return view;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onFoodItemClicked(FoodItem item) {
        utilStartAddEditActivity(item);
    }

    @Override
    public boolean onFoodItemLongClicked(FoodItem item) {
        // Ignore long clicks, consume event by returning true
        return true;
    }

    /**
     * Utility method to start an AddEdit activity.
     *
     * @param item Pass item to edit it, otherwise pass null to create a new item
     */
    private void utilStartAddEditActivity(FoodItem item) {
        Log.d(TAG, "utilStartAddEditActivity: called");

        Intent addEditIntent = new Intent(getActivity(), AddEditActivity.class);
        if (item != null) {
            addEditIntent.putExtra(FoodItem.class.getSimpleName(), item);
        }
        startActivity(addEditIntent);
    }

}

// TODO Hide ActionBar, leave tabs visible (like in Tasks To Do app)