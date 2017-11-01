package com.nivgelbermann.fooddiarydemo.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.CategoriesContract;
import com.nivgelbermann.fooddiarydemo.data.Category;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nivgelbermann.fooddiarydemo.utils.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivity extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddEditActivity";

    private static final String DATE_PICKER_TAG = "DatePickerDialog";
    private static final String TIME_PICKER_TAG = "TimePickerDialog";
    private static final int REQUEST_CODE_CHOOSE_CATEGORY = 1;

    @BindView(R.id.add_edit_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.add_edit_input) EditText input;
    @BindView(R.id.add_edit_fab) FloatingActionButton fab;
    @BindView(R.id.add_edit_ll_category) LinearLayout categoryLayout;
    @BindView(R.id.add_edit_ll_date) LinearLayout dateLayout;
    @BindView(R.id.add_edit_ll_time) LinearLayout timeLayout;
    @BindView(R.id.add_edit_category_ic) ImageView categoryIcon;
    @BindView(R.id.add_edit_category_content) TextView categoryContent;
    @BindView(R.id.add_edit_date_content) TextView dateContent;
    @BindView(R.id.add_edit_time_content) TextView timeContent;

    private FoodItem mFoodItem;
    private boolean mEditMode;
    private boolean mMode24Hours = true; // TODO Incorporate into app settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);

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
        if (mEditMode) {
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
                                categoryContent.getText().toString(),
                                FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy @ HH:mm")));
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
        String formattedTime = FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy");
        dateContent.setText(formattedTime);
        Log.d(TAG, "onDateSet: date: " + formattedTime);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: called with result code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_CHOOSE_CATEGORY) {
            throw new InvalidParameterException(TAG + ".onActivityResult called with invalid request code: " + requestCode);
        }
        switch (resultCode) {
            case RESULT_OK:
                Log.d(TAG, "onActivityResult: result code OK");
                if (data != null) {
                    Category category = (Category) data.getSerializableExtra(Category.class.getSimpleName());
                    if (category != null) {
                        Log.d(TAG, "onActivityResult: displaying chosen category");
                        categoryIcon.setColorFilter(Color.parseColor(category.getColor()));
                        categoryContent.setText(category.getName());
                        mFoodItem.setCategoryId(Integer.parseInt(category.getId()));
                        Log.d(TAG, "onActivityResult: category #" + category.getId() + ": " + category.getName());
                        Log.d(TAG, "onActivityResult: item category: " + mFoodItem.getCategoryId());
                    }
                }
                break;

            case RESULT_CANCELED:
                Log.d(TAG, "onActivityResult: result code CANCELED");
                // Do nothing
                break;

            default:
                throw new InvalidParameterException(TAG + ".onActivityResult called with invalid result code: " + resultCode);
        }
    }

    /**
     * Utility method for displaying the item to be edited
     * in the layout's rows: Category, Date, Time (and more to come).
     */
    private void utilDisplayFoodItem() {
        input.setText(mFoodItem.getName());
        dateContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        timeContent.setText(FoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));

        if (mEditMode) {
            // Set fab icon to "save"
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_save_white_24dp));

            // Set category text and icon color according to food item's category
            ContentResolver contentResolver = getContentResolver();
            String[] projection = new String[]{CategoriesContract.Columns.NAME, CategoriesContract.Columns.COLOR};
            Cursor cursor = contentResolver.query(CategoriesContract.buildCategoryUri(mFoodItem.getCategoryId()),
                    projection,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                throw new IllegalStateException("Couldn't move cursor to first");
            }
            categoryIcon.setColorFilter(Color.parseColor(
                    cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.COLOR))));
            categoryContent.setText(
                    cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.NAME)));
            cursor.close();
        } else {
            categoryContent.setText("Other"); // TODO After enabling user-defined categories, set this to load the default one
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
                Intent categoryIntent = new Intent(AddEditActivity.this, CategoryChooserActivity.class);
                categoryIntent.putExtra(CategoriesContract.Columns._ID, mFoodItem.getCategoryId());
                startActivityForResult(categoryIntent, REQUEST_CODE_CHOOSE_CATEGORY);
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
                    // TODO Replace all Toasts with Snackbars
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
                values.put(FoodsContract.Columns.CATEGORY_ID, mFoodItem.getCategoryId());

                if (mFoodItem.isValid()) {
                    if (mEditMode) {
                        utilUpdateDeleteItem(contentResolver, values);
                    } else {
                        // Insert new record into DB.
                        Log.d(TAG, "fab.onClick: adding new item");
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
                Log.d(TAG, "fab.onClick: updating item");
                resolver.update(FoodsContract.buildFoodItemUri(itemId), values, null, null);
            } else {
                Log.d(TAG, "fab.onClick: deleting item");
                resolver.delete(FoodsContract.buildFoodItemUri(itemId), null, null);
            }
        }
    }
}
