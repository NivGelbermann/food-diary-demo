package com.nivgelbermann.fooddiarydemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
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

//            // Add mode - display instructions
//            categoryContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_category_message));
//            dateContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_date_message));
//            timeContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_time_message));
//
//        } else {
//            // Edit mode - display item's details
//            utilDisplayFoodItem();
//        }
        }
        utilDisplayFoodItem();

        utilSetOnClickListeners();

        Log.d(TAG, "onCreateView: ends");
        return view;
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
        mFoodItem.setDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        if (mEditMode) {
            calendar.set(mFoodItem.getYear(), mFoodItem.getMonth(), mFoodItem.getDay(), hourOfDay, minute, second);

        }
        mFoodItem.setTime(calendar.getTimeInMillis() / Constants.MILLISECONDS);
        // Might cause errors when trying to set the time before/without setting the date
    }

    /**
     * Utility method for displaying the item to be edited
     * in the layout's rows: Category, Date, Time (and more to come).
     */
    private void utilDisplayFoodItem() {
        categoryContent.setText(String.valueOf(mFoodItem.getCategory()));
        Log.d(TAG, "onCreateView: category: " + mFoodItem.getCategory());
        dateContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
        Log.d(TAG, "onCreateView: date: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "dd/MM/yy"));
//        dateContent.setText(String.valueOf(mFoodItem.getDay()) + "/" + String.valueOf(mFoodItem.getMonth()+1) + "/" + String.valueOf(mFoodItem.getYear()));
        timeContent.setText(mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
        Log.d(TAG, "onCreateView: time: " + mFoodItem.getFormattedTime(mFoodItem.getTime(), "HH:mm"));
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
                Calendar now = Calendar.getInstance();
                DatePickerDialog picker = DatePickerDialog.newInstance(AddEditActivityFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                picker.show(getActivity().getFragmentManager(), DATE_PICKER_TAG);
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog picker = TimePickerDialog.newInstance(AddEditActivityFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND),
                        mMode24Hours);
                picker.show(getActivity().getFragmentManager(), TIME_PICKER_TAG);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "fab clicked", Toast.LENGTH_SHORT).show();

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

                if (mFoodItem.isValid()) {
                    if (mEditMode) {
                        // If in edit mode - implement editing an item

                        // Item changes should have been made by user via pickers and dialogs.
                        // Query the db to find if an item identical to mFoodItem exists
                        // (meaning no changes have been made)
                        // and update DB accordingly.
                    } else {
                        // Verify mFoodItem has all its properties set
                        // and insert into DB.

                        ContentValues values = new ContentValues();
                        values.put(FoodsContract.Columns.FOOD_ITEM, mFoodItem.getName());
                        values.put(FoodsContract.Columns.YEAR, mFoodItem.getYear());
                        values.put(FoodsContract.Columns.MONTH, mFoodItem.getMonth());
                        values.put(FoodsContract.Columns.DAY, mFoodItem.getDay());
                        values.put(FoodsContract.Columns.HOUR, mFoodItem.getTime());
                        values.put(FoodsContract.Columns.CATEGORY_ID, mFoodItem.getCategory());

                        contentResolver.insert(FoodsContract.CONTENT_URI, values);

                        // =======================================================================================================
                        // Make sure MainActivity refreshes
                    }
                }

                getActivity().onBackPressed();
            }

            // TODO If mFoodItem is unnecessary here, do the following:
            // A) change it to an instance variable in receiveData()
            // B) change utilDisplayFoodItem to receive a FoodItem object
            // C) and delete the member variable mFoodItem
        });
    }
}
