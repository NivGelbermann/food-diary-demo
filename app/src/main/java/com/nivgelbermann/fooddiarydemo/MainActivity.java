package com.nivgelbermann.fooddiarydemo;

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

import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nivgelbermann.fooddiarydemo.Constants.CURRENT_MONTH;
import static com.nivgelbermann.fooddiarydemo.Constants.CURRENT_YEAR;
import static com.nivgelbermann.fooddiarydemo.Constants.EPOCH;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int ADD_FOODITEM_DIALOG = 0;

    //    @BindView(R.id.tabs)
//    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_tab_layout)
    RecyclerTabLayout recyclerTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        utilLogDatabase();

        // Create a list to hold all page titles (MM/yyyy)
        List<String> tabTitles = new ArrayList<>();
        int startYear = EPOCH;

        for (int i = startYear; i <= CURRENT_YEAR; i++) {
            for (int j = Calendar.JANUARY; j <= Calendar.DECEMBER; j++) {
                // If loop has passed current month and year, stop adding tab titles
                if (i == CURRENT_YEAR && j > CURRENT_MONTH) {
                    break;
                }
                StringBuilder month = new StringBuilder(String.valueOf(j + 1));
                if (j < 9) {
                    month.insert(0, 0);
                }
                tabTitles.add(month.toString() + "/" + String.valueOf(i));
            }
        }

        MonthsStatePagerAdapter adapter =
                new MonthsStatePagerAdapter(getSupportFragmentManager(), tabTitles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(CURRENT_YEAR - startYear + Constants.MONTHS_A_YEAR * CURRENT_MONTH);
        recyclerTabLayout.setUpWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEditIntent = new Intent(getApplication(), AddEditActivity.class);
                startActivity(addEditIntent);
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

    private void utilLogDatabase()  {
        Log.d(TAG, "utilLogDatabase: ====================");
        Log.d(TAG, "utilLogDatabase:   LOGGING DATABASE");
        Log.d(TAG, "utilLogDatabase: ====================");

        String[] projection = {
                FoodsContract.Columns._ID,
                FoodsContract.Columns.FOOD_ITEM,
                FoodsContract.Columns.CATEGORY_ID,
                FoodsContract.Columns.HOUR,
                FoodsContract.Columns.DAY,
                FoodsContract.Columns.MONTH,
                FoodsContract.Columns.YEAR};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(FoodsContract.CONTENT_URI,
                projection,
                null,
                null,
                FoodsContract.Columns.YEAR + ", "
                        + FoodsContract.Columns.MONTH + ", "
                        + FoodsContract.Columns.DAY + ", "
                        + FoodsContract.Columns.HOUR);
        if (cursor != null) {
            Log.d(TAG, "utilLogDatabase: number of rows: " + cursor.getCount());
            Log.d(TAG, "utilLogDatabase: ====================");
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "utilLogDatabase: ====================");
            }
            cursor.close();
        }
    }

}
