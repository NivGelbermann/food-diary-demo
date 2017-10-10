package com.nivgelbermann.fooddiarydemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.Serializable;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivityFragment extends Fragment
        implements PassActivityDataToFragment,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddEditActivityFragment";
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

    private static FoodItem mFoodItem;
    private boolean mEditMode = false;
    private boolean mMode24Hours = true; // TODO Incorporate into app settings

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
        mFoodItem = new FoodItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        ButterKnife.bind(this, view);

        if (!mEditMode) {
            // If adding an item, initialize it for right now's date and time
            Calendar now = Calendar.getInstance();
            mFoodItem.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            mFoodItem.setTime(now.getTimeInMillis() / Constants.MILLISECONDS);
        }
        utilDisplayFoodItem();
        utilSetOnClickListeners();
        setHasOptionsMenu(true);

        Log.d(TAG, "onCreateView: ends");
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_add_edit, menu);
//        if (!mEditMode) {
//            menu.getItem(1).setVisible(false);
//            menu.getItem(2).setVisible(false);
//        }
        if(mEditMode) {
            inflater.inflate(R.menu.menu_add_edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
//            case R.id.menu_addedit_cancel:
//                // Do nothing (activity's finish method is called after switch statement)
//                break;

            case R.id.menu_addedit_share:
                // TODO Somehow deletes items!!!! Try sharing while watching logcat
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.add_edit_share_item_info,
                                mFoodItem.getName(),
                                String.valueOf(mFoodItem.getCategory()),
                                FoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"))); //TODO Make getFormattedTime static + revise all method calls
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.add_edit_share_chooser_header)));
                break;

            case R.id.menu_addedit_delete:
                ContentResolver contentResolver = getContext().getContentResolver();
                utilUpdateDeleteItem(contentResolver, null);
                break;

            // Handles home-button behaviour in pre-21sdk
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(getActivity());
                // Do nothing (activity's finish method is called after switch statement)
                break;

            default:
//                throw new InvalidParameterException(TAG + ".onOptionsItemSelected called with invalid MenuItem " + item.getTitle());
                Toast.makeText(getContext(), R.string.add_edit_general_error, Toast.LENGTH_LONG).show();
        }
        getActivity().finish();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method for receiving a Serializable (FoodItem) object from containing activity. <p>
     * If data was received, fragment will edit it and update the DB.
     * Otherwise, fragment will add new data to DB.
     *
     * @param data item to be edited, or null for adding new data.
     */
    @Override
    public void receiveData(Serializable data) {
        mFoodItem = (FoodItem) data;
        if (mFoodItem != null) {
            mEditMode = true;
            input.setText(mFoodItem.getName());
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_white_24dp));
            utilDisplayFoodItem();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Update mFoodItem
        mFoodItem.setDate(year, monthOfYear, dayOfMonth);

        // Update display
        dateContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        Log.d(TAG, "onDateSet: date: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        // Update mFoodItem
        Calendar calendar = Calendar.getInstance();
        calendar.set(mFoodItem.getYear(), mFoodItem.getMonth(), mFoodItem.getDay(), hourOfDay, minute, second);
        mFoodItem.setTime(calendar.getTimeInMillis() / Constants.MILLISECONDS);

        // Update display
        timeContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
        Log.d(TAG, "onTimeSet: time: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
    }

    /**
     * Utility method for displaying the item to be edited
     * in the layout's rows: Category, Date, Time (and more to come).
     */
    private void utilDisplayFoodItem() {
        categoryContent.setText(String.valueOf(mFoodItem.getCategory()));
        Log.d(TAG, "utilDisplayFoodItem: category: " + mFoodItem.getCategory());
        dateContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        Log.d(TAG, "utilDisplayFoodItem: date: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
//        dateContent.setText(String.valueOf(mFoodItem.getDay()) + "/" + String.valueOf(mFoodItem.getMonth()+1) + "/" + String.valueOf(mFoodItem.getYear()));
        timeContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
        Log.d(TAG, "utilDisplayFoodItem: time: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
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
                Toast.makeText(getContext(), "category clicked", Toast.LENGTH_SHORT).show();
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
                DatePickerDialog picker = DatePickerDialog.newInstance(AddEditActivityFragment.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                picker.show(getActivity().getFragmentManager(), DATE_PICKER_TAG);
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
                TimePickerDialog picker = TimePickerDialog.newInstance(AddEditActivityFragment.this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        0,
                        mMode24Hours);
                picker.show(getActivity().getFragmentManager(), TIME_PICKER_TAG);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If no name was entered, display error to user
                // and consume click event
                String name = input.getText().toString();
                if (name.trim().isEmpty()) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.add_edit_name_input_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mFoodItem.setName(name);

                ContentResolver contentResolver = getContext().getContentResolver();
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

                getActivity().finish();
            }
        });
    }

    /**
     * Utility method for simplifying the edit and delete functionality code.
     *
     * @param resolver ContentResolver for this method to use.
     * @param values ContentValues for to update if in edit mode. Otherwise pass Null (to delete).
     */
    private void utilUpdateDeleteItem(ContentResolver resolver, ContentValues values) {
        Cursor cursor = resolver.query(FoodsContract.CONTENT_URI,
                new String[]{FoodsContract.Columns._ID},
                "_id=?",
                new String[]{mFoodItem.getId()},
                null);

        if ((cursor == null) || (!cursor.moveToFirst())) {
            // Cursor wasn't returned or matching item wasn't found
            Toast.makeText(getContext(),
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
