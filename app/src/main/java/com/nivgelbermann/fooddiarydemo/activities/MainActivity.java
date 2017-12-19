package com.nivgelbermann.fooddiarydemo.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.fragments.HistoryFragment;
import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nivgelbermann.fooddiarydemo.utils.Util;

import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener {
    private static final String TAG = "MainActivity";

    private static final String CURRENT_FRAGMENT_TAG = "CurrentFragment";
    private static final String PAGE_FRAGMENT_TAG = "PageFragment";
    private static final String HISTORY_FRAGMENT_TAG = "HistoryFragment";

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

        // If activity is created for the first time, use PageFragment as content.
        // Otherwise, set last-displayed fragment as content.
        if (savedInstanceState == null) {
            Calendar calendar = Calendar.getInstance();
            PageFragment fragment = PageFragment.newInstance(
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment, PAGE_FRAGMENT_TAG)
                    .commit();
            fragment.setUserVisibleHint(true);
        } else {
            // Get last-displayed fragment tag from bundle
            String tag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment, tag)
                    .commit();
            fragment.setUserVisibleHint(true);
        }

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
            case R.id.menu_main_generate_items:
                generateRandomItems();
                return true;

            case R.id.action_settings:
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: starts");
        super.onSaveInstanceState(outState);
        // More general use-case, which doesn't apply to my current needs:
        // String tag = getSupportFragmentManager()
        //        .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
        //        .getName();
        String tag = getSupportFragmentManager().getFragments().get(0).getTag();
        outState.putString(CURRENT_FRAGMENT_TAG, tag);
        Log.d(TAG, "onSaveInstanceState: ends with tag: " + tag);
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
        String tag;
        Calendar calendar;
        switch (item.getItemId()) {
            case R.id.menu_navdrawer_this_month:
                calendar = Calendar.getInstance();
                fragment = PageFragment.newInstance(
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                tag = PAGE_FRAGMENT_TAG;
                break;


            case R.id.menu_navdrawer_history:
                fragment = HistoryFragment.newInstance();
                tag = HISTORY_FRAGMENT_TAG;
                break;

            // TODO Fill out with the rest of the fragments as they are created
            // TODO Create matching CategoriesFragment class OR convert CategoriesChooserActivity to a fragment

            default:
                calendar = Calendar.getInstance();
                fragment = PageFragment.newInstance(
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                tag = PAGE_FRAGMENT_TAG;
                break;
        }

        // If nav item clicked isn't the currently displayed fragment, then
        // insert the fragment by replacing any existing fragment. Otherwise, skip.
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!fragment.getClass().equals(fragmentManager.getFragments().get(0).getClass())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment, tag)
                    .commit();
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

    /**
     * Generates between 1 and 3 food items for each day of the month.
     * Currently works only for the current month.
     */
    private void generateRandomItems() {
        Log.d(TAG, "generateRandomItems: called");
        String[] foods = {"Avacado", "Watercress Sandwich", "Flan", "Calamari",
                "Raspberry Lemon Meringue Pie", "Baked Potato Soup", "Oysters Rockefeller",
                "Sticky Toffee Pudding", "Chicken Fried Steak", "Cinnamon Bread",
                "Maple Bacon Doughnut", "Bagel and Lox", "Persimmon", "Eggplant", "Udon",
                "Hibiscus Tea", "Cactus Fries", "Pomelo", "Jumbalaya", "Chicken Noodle Soup",
                "Pho", "Black Forest Cake", "Butter Chicken", "Philly Cheese Steak",
                "Fettucini Alfredo", "Spaghetti Squash", "Frittata", "Eel", "Profiteroles",
                "Cream Cheese Frosting", "Pineapple", "Arugula Blackberry Salad", "Dragonfruit",
                "Carbonara", "Chia Pudding", "Mango Lassi", "Corned Beef Sandwich", "Bubble Tea",
                "Chocolate Raspberry Brownies", "Lamb Chops", "Smith Island Cake",
                "Cheese Stuffed Jalapenos", "Mexican Street Corn", "Chow Mein", "Corn Chowder",
                "Coconut Cream Pie", "Banh Mi", "Empanadas", "Vanilla Pudding",
                "Blue Moon Ice Cream", "Blueberry Pineapple Smoothie", "Elk", "Eggplant Parmesan",
                "Swedish Meatballs", "Squared Watermelon", "Crêpe Suzette", "Pico De Gallo",
                "Cucumber Sandwiches", "Caramel Latte", "Lemon Chicken", "Saltine Crackers",
                "Chorizo Pizza", "Sausage, Peppers and Onions on a Hoagie", "Fried Green Tomatoes",
                "Blueberry Cream Cheese French Toast Casserole", "Snickerdoodles",
                "Roast Turkey and Stuffing", "Rueben Sandwich", "Lemon Cookies",
                "Strawberry Rhubarb Pie", "Bacon Wrapped Pineapple", "Marzipan Dates",
                "Pistachio Muffin", "Spaghetti Bolognese", "Potato Cake", "Gazpacho",
                "Huckleberry Ice Cream"};
        Integer[] categories = {6, 3, 8, 4, 8, 3, 4, 8, 4, 8, 1, 4, 6, 5, 3, 7, 3, 6, 4, 4, 4, 8,
                4, 4, 2, 3, 1, 4, 8, 8, 6, 5, 6, 3, 8, 7, 4, 7, 8, 4, 8, 2, 5, 3, 3, 8, 4, 4, 8, 8,
                7, 4, 2, 4, 6, 8, 5, 3, 7, 4, 3, 3, 1, 5, 8, 8, 4, 4, 8, 8, 1, 8, 8, 4, 3, 1, 8};

        ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null) {
            Log.d(TAG, "generateRandomItems: could not get content resolver");
        }

        Calendar calendar = Calendar.getInstance();
        Random random = new Random();
        for (int dayOfMonth = 1; dayOfMonth < calendar.getActualMaximum(Calendar.MONTH); dayOfMonth++) {
            Log.d(TAG, "generateRandomItems: adding items to day: " + dayOfMonth);
//            int items = ThreadLocalRandom.current().nextInt(1, foods.length);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Template: random.nextInt((max - min) + 1) + min;
//            int items = random.nextInt((8 - 5) + 1) + 5;
            int items = random.nextInt((4 - 2) + 1) + 2;
            for (int i = 0; i < items; i++) {
                int itemId = random.nextInt(foods.length - 1) + 1;
                ContentValues values = new ContentValues();
                values.put(FoodsContract.Columns.FOOD_ITEM, foods[itemId]);
                values.put(FoodsContract.Columns.YEAR, calendar.get(Calendar.YEAR));
                values.put(FoodsContract.Columns.MONTH, calendar.get(Calendar.MONTH));
                values.put(FoodsContract.Columns.DAY, calendar.get(Calendar.DAY_OF_MONTH));
                values.put(FoodsContract.Columns.HOUR, calendar.getTimeInMillis() / Util.MILLISECONDS);
                values.put(FoodsContract.Columns.CATEGORY_ID, categories[itemId]);
                contentResolver.insert(FoodsContract.CONTENT_URI, values);
                Log.d(TAG, "generateRandomItems: item saved: " + values.toString());
            }
        }
    }
}


// TODO  Format all in-class utility methods in project to either have "util" in method header or not. Adjust documentation accordingly.