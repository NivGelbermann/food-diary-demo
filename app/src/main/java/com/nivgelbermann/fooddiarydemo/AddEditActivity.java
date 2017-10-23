package com.nivgelbermann.fooddiarydemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivity extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddEditActivity";

    private static final String DATE_PICKER_TAG = "DatePickerDialog";
    private static final String TIME_PICKER_TAG = "TimePickerDialog";

    @BindView(R.id.add_edit_input) EditText input;
    @BindView(R.id.add_edit_fab) FloatingActionButton fab;
    @BindView(R.id.add_edit_ll_category) LinearLayout categoryLayout;
    @BindView(R.id.add_edit_ll_date) LinearLayout dateLayout;
    @BindView(R.id.add_edit_ll_time) LinearLayout timeLayout;
    @BindView(R.id.add_edit_category_content) TextView categoryContent;
    @BindView(R.id.add_edit_date_content) TextView dateContent;
    @BindView(R.id.add_edit_time_content) TextView timeContent;

    private FoodItem mFoodItem;
    private boolean mEditMode = false;
    private boolean mMode24Hours = true; // TODO Incorporate into app settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);

        mEditMode = false;
        mFoodItem = new FoodItem();

        Bundle arguments = getIntent().getExtras();
        mEditMode = arguments != null;
        if (mEditMode) {
            mFoodItem = (FoodItem) arguments.getSerializable(FoodItem.class.getSimpleName());
        } else {
            // If adding an item, initialize it for right now's date and time
            Calendar now = Calendar.getInstance();
            mFoodItem.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            mFoodItem.setTime(now.getTimeInMillis() / Constants.MILLISECONDS);
        }

        utilDisplayFoodItem();
        utilSetOnClickListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        Resources resources = getResources();
        getSupportActionBar().setTitle((mEditMode)
                ? resources.getString(R.string.add_edit_activity_header_edit)
                : resources.getString(R.string.add_edit_activity_header_add));
        getSupportActionBar().setElevation(0);
//        setHasOptionsMenu(mEditMode);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If in edit mode, inflate menu
        if(mEditMode) {
            getMenuInflater().inflate(R.menu.menu_add_edit, menu);
            return true;
        }
        // If in add mode, do not inflate menu
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.menu_addedit_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.add_edit_share_item_info,
                                mFoodItem.getName(),
                                String.valueOf(mFoodItem.getCategory()),
                                FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy HH:mm")));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.add_edit_share_chooser_header)));
                break;

            case R.id.menu_addedit_delete:
                ContentResolver contentResolver = getContentResolver();
                utilUpdateDeleteItem(contentResolver, null);
                break;

            // Handles home-button behaviour in pre-21sdk
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                break;

            default:
                // throw new InvalidParameterException(TAG + ".onOptionsItemSelected called with invalid MenuItem " + item.getTitle());
                Toast.makeText(this, R.string.add_edit_general_error, Toast.LENGTH_LONG).show();
        }

        finish();
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Update mFoodItem
        mFoodItem.setDate(year, monthOfYear, dayOfMonth);

        // Update display
        dateContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        Log.d(TAG, "onDateSet: date: " + FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        // Update mFoodItem
        Calendar calendar = Calendar.getInstance();
        calendar.set(mFoodItem.getYear(), mFoodItem.getMonth(), mFoodItem.getDay(), hourOfDay, minute, second);
        mFoodItem.setTime(calendar.getTimeInMillis() / Constants.MILLISECONDS);

        // Update display
        timeContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
        Log.d(TAG, "onTimeSet: time: " + FoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
    }

    /**
     * Utility method for displaying the item to be edited
     * in the layout's rows: Category, Date, Time (and more to come).
     */
    private void utilDisplayFoodItem() {
        input.setText(mFoodItem.getName());
        categoryContent.setText(String.valueOf(mFoodItem.getCategory()));
        dateContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        timeContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));

        if (mEditMode) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_save_white_24dp));
        }

        Log.d(TAG, "utilDisplayFoodItem: " + mFoodItem);
    }

    /**
     * Utility method for simplifying onCreateView, by delegating
     * the setting of OnClickListeners to each row of food item content -
     * Category, Date, Time (and more to come).
     */
    private void utilSetOnClickListeners() {
        categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Implement handling categories
                Toast.makeText(AddEditActivity.this, "category clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar;
                if (!mEditMode) {
                    calendar = Calendar.getInstance();
                } else {
                    calendar = mFoodItem.getCalendarDate();
                }
                DatePickerDialog picker = DatePickerDialog.newInstance(AddEditActivity.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                picker.show(getFragmentManager(), DATE_PICKER_TAG);
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar;
                if (!mEditMode) {
                    calendar = Calendar.getInstance();
                } else {
                    calendar = mFoodItem.getCalendarTime();
                }
                TimePickerDialog picker = TimePickerDialog.newInstance(AddEditActivity.this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        0,
                        mMode24Hours);
                picker.show(getFragmentManager(), TIME_PICKER_TAG);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If no name was entered, display error to user
                // and consume click event
                String name = input.getText().toString();
                if (name.trim().isEmpty()) {
                    Toast.makeText(AddEditActivity.this,
                            getResources().getString(R.string.add_edit_name_input_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mFoodItem.setName(name);

                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(FoodsContract.Columns.FOOD_ITEM, mFoodItem.getName());
                values.put(FoodsContract.Columns.YEAR, mFoodItem.getYear());
                values.put(FoodsContract.Columns.MONTH, mFoodItem.getMonth());
                values.put(FoodsContract.Columns.DAY, mFoodItem.getDay());
                values.put(FoodsContract.Columns.HOUR, mFoodItem.getTime());
                values.put(FoodsContract.Columns.CATEGORY_ID, mFoodItem.getCategory());

                if (mFoodItem.isValid()) {
                    if (mEditMode) {
                        utilUpdateDeleteItem(contentResolver, values);
                    } else {
                        // Insert new record into DB.
                        contentResolver.insert(FoodsContract.CONTENT_URI, values);
                    }
                }

                finish();
            }
        });
    }

    /**
     * Utility method for simplifying the edit and delete functionality code.
     *
     * @param resolver ContentResolver for this method to use.
     * @param values   ContentValues for to update if in edit mode. Otherwise pass Null (to delete).
     */
    private void utilUpdateDeleteItem(ContentResolver resolver, ContentValues values) {
        Cursor cursor = resolver.query(FoodsContract.CONTENT_URI,
                new String[]{FoodsContract.Columns._ID},
                "_id=?",
                new String[]{mFoodItem.getId()},
                null);

        if ((cursor == null) || (!cursor.moveToFirst())) {
            // Cursor wasn't returned or matching item wasn't found
            Toast.makeText(this,
                    getResources().getString(R.string.add_edit_general_error),
                    Toast.LENGTH_LONG).show();
            String errorSource = (values == null) ? "Menu.Delete" : "FAB";
            Log.d(TAG, errorSource + ".onClick: a match in the DB wasn't found for the food item loaded in edit mode. Exiting AddEditActivity.");
        } else {
            long itemId = cursor.getLong(cursor.getColumnIndex(FoodsContract.Columns._ID));
            cursor.close();
            if (values != null) {
                resolver.update(FoodsContract.buildFoodItemUri(itemId), values, null, null);
            } else {
                resolver.delete(FoodsContract.buildFoodItemUri(itemId), null, null);
            }
        }
    }
}
