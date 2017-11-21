package com.nivgelbermann.fooddiarydemo.activities;

import android.content.ContentResolver;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener {
    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.main_fab) FloatingActionButton fab;
    @BindView(R.id.recycler_tab_layout) RecyclerTabLayout recyclerTabLayout;

    private MonthsStatePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ArrayList<String> tabTitles = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
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

        mPagerAdapter =
                new MonthsStatePagerAdapter(getSupportFragmentManager(), tabTitles);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(tabTitles.size() - 1);
        recyclerTabLayout.setUpWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilStartAddEditActivity(null);
            }
        });
        Log.d(TAG, "onCreate: ends");
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

}

// TODO Hide ActionBar, leave tabs visible (like in Tasks To Do app)
// TODO Add gap between bottom card and bottom of display, like in Money app, to prevent FAB covering items