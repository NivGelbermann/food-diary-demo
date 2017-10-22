package com.nivgelbermann.fooddiarydemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivityFragment extends Fragment
//        implements PassActivityDataToFragment,
        implements
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

    private FoodItem mFoodItem;
    private boolean mEditMode = false;
    private boolean mMode24Hours = true; // TODO Incorporate into app settings
    private OnFinished mFinishedListener = null;

    /**
     * Interface for notifying the containing activity that
     * the fragment has finished its task, and needs to be removed.
     */
    interface OnFinished {
        void onAddEditFinished();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
        mFoodItem = new FoodItem();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: called");
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks
        Activity activity = getActivity();
        if(!(activity instanceof OnFinished)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + "must implement AddEditActivityFragment.OnFinished interface");
        }
        mFinishedListener = (OnFinished) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mFoodItem = (FoodItem) arguments.getSerializable(FoodItem.class.getSimpleName());
            if (mFoodItem != null) {
                mEditMode = true;
//                fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_save_white_24dp));
            }
        }

        if (!mEditMode) {
            // If adding an item, initialize it for right now's date and time
            Calendar now = Calendar.getInstance();
            mFoodItem.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            mFoodItem.setTime(now.getTimeInMillis() / Constants.MILLISECONDS);
        }
        utilDisplayFoodItem();
        utilSetOnClickListeners();
        setHasOptionsMenu(mEditMode);

        Log.d(TAG, "onCreateView: ends");
        return view;
    }

//    @Override
//    public void onDestroyView() {
//        Log.d(TAG, "onDestroyView: called");
//        mFoodItem = null;
//        fab.setVisibility(View.GONE);
//        super.onDestroyView();
//    }


    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: called");
        super.onDetach();
        mFinishedListener = null;
        fab = null;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mEditMode) {
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
                ContentResolver contentResolver = getContext().getContentResolver();
                utilUpdateDeleteItem(contentResolver, null);
                break;

            // Handles home-button behaviour in pre-21sdk
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(getActivity());
                break;

            default:
                // throw new InvalidParameterException(TAG + ".onOptionsItemSelected called with invalid MenuItem " + item.getTitle());
                Toast.makeText(getContext(), R.string.add_edit_general_error, Toast.LENGTH_LONG).show();
        }

        if(mFinishedListener != null) {
            mFinishedListener.onAddEditFinished();
        }
        return true;
    }

//    /**
//     * Callback method for receiving a Serializable (FoodItem) object from containing activity. <p>
//     * If data was received, fragment will edit it and update the DB.
//     * Otherwise, fragment will add new data to DB.
//     *
//     * @param data item to be edited, or null for adding new data.
//     */
//    @Override
//    public void receiveData(Serializable data) {
//        Log.d(TAG, "receiveData: called");
//        mFoodItem = (FoodItem) data;
//        if (mFoodItem != null) {
//            mEditMode = true;
////            input.setText(mFoodItem.getName());
//            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_white_24dp));
//            Log.d(TAG, "receiveData: changed fab icon");
//            utilDisplayFoodItem();
//        }
//    }

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
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_white_24dp));
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

                if(mFinishedListener != null) {
                    mFinishedListener.onAddEditFinished();
                }
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
