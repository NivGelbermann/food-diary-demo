package com.nivgelbermann.fooddiarydemo.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.adapters.InnerRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.adapters.MonthsStatePagerAdapter;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nivgelbermann.fooddiarydemo.utils.Constants;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//public class MainActivity extends AppCompatActivity /*implements PageFragment.OnDateSelectedInterface*/ {
public class MainActivity
        extends AppCompatActivity
        implements InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivity";

    //    public static final int ADD_FOODITEM_DIALOG = 0;
    private static final int MONTHS_PAGER_LOADER_ID = 0;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.main_fab) FloatingActionButton fab;
    @BindView(R.id.recycler_tab_layout) RecyclerTabLayout recyclerTabLayout;

    private MonthsStatePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        utilInitLoaders();
//        utilLogDatabase();

        // Create a list to hold all page titles (MM/yyyy)
        List<String> tabTitles = new ArrayList<>();
//        int startYear = EPOCH;
//
//        for (int i = startYear; i <= CURRENT_YEAR; i++) {
//            for (int j = Calendar.JANUARY; j <= Calendar.DECEMBER; j++) {
//                // If loop has passed current month and year, stop adding tab titles
//                if (i == CURRENT_YEAR && j > CURRENT_MONTH) {
//                    break;
//                }
//                StringBuilder month = new StringBuilder(String.valueOf(j + 1));
//                if (j < 9) {
//                    month.insert(0, 0);
//                }
//                tabTitles.add(month.toString() + "/" + String.valueOf(i));
//            }
//        }


//        ContentResolver contentResolver = getContentResolver();
//        if (contentResolver != null) {
//            String[] projection = new String[]{"DISTINCT " + FoodsContract.Columns.MONTH,
//                    FoodsContract.Columns.YEAR};
//            String sortOrder = FoodsContract.Columns.YEAR + ","
//                    + FoodsContract.Columns.MONTH + " ASC";
//            Cursor cursor = contentResolver.query(
//                    FoodsContract.CONTENT_URI,
//                    projection,
//                    null,
//                    null,
//                    sortOrder);
//            if (cursor == null || cursor.getCount() == 0) {
//                Log.d(TAG, "onCreate: cursor null or empty");
//            } else {
//                // TODO This method generates tabs only for months that exist in the DB. Make it generate a tab for every month since the FIRST IN THE DB to the CURRENT DATE
//                // Right now, if the DB contains items in June and August but no items in July, a tab for July would not be created
//                // TODO Add empty database scenario
//                while (cursor.moveToNext()) {
//                    tabTitles.add(cursor.getString(
//                            cursor.getColumnIndex(FoodsContract.Columns.MONTH)) + "/"
//                            + cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.YEAR)));
//                }
//            }
//        }

//        MonthsStatePagerAdapter adapter =
//                new MonthsStatePagerAdapter(getSupportFragmentManager(), tabTitles);
        mPagerAdapter =
                new MonthsStatePagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
        viewPager.setAdapter(mPagerAdapter);
//        viewPager.setCurrentItem(CURRENT_YEAR - startYear + Constants.MONTHS_A_YEAR * CURRENT_MONTH);
//        viewPager.setCurrentItem(tabTitles.size() - 1);
//        viewPager.setCurrentItem(mPagerAdapter.getCount() - 1);
        recyclerTabLayout.setUpWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilStartAddEditActivity(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader: called with id: " + id);
        String[] projection;
        String sortOrder;

        switch (id) {
            case MONTHS_PAGER_LOADER_ID:
                projection = new String[]{
                        "DISTINCT " + FoodsContract.Columns.MONTH,
                        FoodsContract.Columns.YEAR};
                sortOrder = FoodsContract.Columns.MONTH + ", "
                        + FoodsContract.Columns.YEAR + " ASC";
                return new CursorLoader(this,
                        FoodsContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            throw new InvalidParameterException(TAG + ".onLoadFinished called with null cursor");
        }

        switch (loader.getId()) {
            case MONTHS_PAGER_LOADER_ID:
                Log.d(TAG, "onLoadFinished: starts with MONTHS_PAGER_LOADER_ID");
                mPagerAdapter.swapCursor(cursor);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFinished called with invalid loader");
        }
        viewPager.setCurrentItem(mPagerAdapter.getCount() - 1);
        Log.d(TAG, "onLoadFinished: ends with item count: " + mPagerAdapter.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");
        switch (loader.getId()) {
            case MONTHS_PAGER_LOADER_ID:
                mPagerAdapter.swapCursor(null);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoaderReset called with invalid loader");
        }
    }

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

        Intent addEditIntent = new Intent(this, AddEditActivity.class);
        if (item != null) {
            addEditIntent.putExtra(FoodItem.class.getSimpleName(), item);
        }
        startActivity(addEditIntent);
    }

    /**
     * Utility method for converting time in Epoch format to
     * a formatted String.
     *
     * @param time       long, representing time as seconds since Epoch
     * @param timeFormat String format for return value
     * @return String for time formatted
     */
    public static String utilFormatTime(long time, String timeFormat) {
        // TODO Merge this utility method with the one in FoodItem.java, and move into a util class?
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(new Date(time * Constants.MILLISECONDS));
    }

    private void utilInitLoaders() {
        Log.d(TAG, "utilInitLoaders: called, initiating loaders");
        getLoaderManager().initLoader(MONTHS_PAGER_LOADER_ID, null, this);
    }
}

// TODO Hide ActionBar, leave tabs visible (like in Tasks To Do app)
// TODO Add gap between bottom card and bottom of display, like in Money app, to prevent FAB covering items