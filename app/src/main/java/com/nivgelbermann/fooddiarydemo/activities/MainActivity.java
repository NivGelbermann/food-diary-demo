package com.nivgelbermann.fooddiarydemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.adapters.InnerRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.fragments.HistoryFragment;
import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener {
    private static final String TAG = "MainActivity";

    @BindView(R.id.main_fab) FloatingActionButton fab;
    @BindView(R.id.main_drawer_layout) DrawerLayout drawerLayout; // Opens & closes nav drawer
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_navigation_view) NavigationView navigationView; // Nav drawer itself

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        setTitle(R.string.this_month);

        Calendar calendar = Calendar.getInstance();
        PageFragment fragment = PageFragment.newInstance(
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
        fragment.setUserVisibleHint(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilStartAddEditActivity(null);
            }
        });

        setupDrawerContent(navigationView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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
     * Sets up listener for navigation drawer item selection.
     *
     * @param navigationView navigation view in which item was selected
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    /**
     * Changes display to fragment selected by user (by nav drawer item click)
     *
     * @param item MenuItem representing the fragment selected by the user
     */
    private void selectDrawerItem(@NonNull MenuItem item) {
        Log.d(TAG, "selectDrawerItem: starts");
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Calendar calendar;
        switch (item.getItemId()) {
            case R.id.menu_navdrawer_this_month:
                calendar = Calendar.getInstance();
                fragment = PageFragment.newInstance(
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                break;


            case R.id.menu_navdrawer_history:
                fragment = HistoryFragment.newInstance();
                break;

            // TODO Fill out with the rest of the fragments as they are created
            // TODO Create matching CategoriesFragment class OR convert CategoriesChooserActivity to a fragment

            default:
                calendar = Calendar.getInstance();
                fragment = PageFragment.newInstance(
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                break;
        }

        // If nav item clicked isn't the currently displayed fragment, then
        // insert the fragment by replacing any existing fragment. Otherwise, skip.
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!fragment.getClass().equals(fragmentManager.getFragments().get(0).getClass())) {
            fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
            fragment.setUserVisibleHint(true);
        }

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close navigation drawer
        drawerLayout.closeDrawers();
        Log.d(TAG, "selectDrawerItem: ends, nav item clicked: " + fragment.getClass());
    }
}


// TODO Format all in-class utility methods in project to either have "util" in method header or not. Adjust documentation accordingly.