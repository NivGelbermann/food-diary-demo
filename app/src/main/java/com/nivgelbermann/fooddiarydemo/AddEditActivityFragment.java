package com.nivgelbermann.fooddiarydemo;

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

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivityFragment extends Fragment implements PassActivityDataToFragment {
    private static final String TAG = "AddEditActivityFragment";

    //    @BindView(R.id.add_edit_recyclerview)
//    RecyclerView recyclerView;
    @BindView(R.id.add_edit_input) EditText input;
    @BindView(R.id.add_edit_fab) FloatingActionButton fab;
    @BindView(R.id.add_edit_ll_category) LinearLayout categoryLayout;
    @BindView(R.id.add_edit_ll_date) LinearLayout dateLayout;
    @BindView(R.id.add_edit_ll_time) LinearLayout timeLayout;
    @BindView(R.id.add_edit_category_content) TextView categoryContent;
    @BindView(R.id.add_edit_date_content) TextView dateContent;
    @BindView(R.id.add_edit_time_content) TextView timeContent;

    //    AddEditRecyclerViewAdapter mAdapter;
    private static FoodItem mFoodItem;
    private boolean mEditMode = false;

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        ButterKnife.bind(this, view);

//        // Initialize fixed-size RecyclerView for item's category, date, time, etc.
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mAdapter = new AddEditRecyclerViewAdapter(getContext(), null);
//        recyclerView.setAdapter(mAdapter);

        if (!mEditMode) {
            // Add mode - display instructions
            categoryContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_category_message));
            dateContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_date_message));
            timeContent.setText(getResources().getString(R.string.add_edit_recyclerview_item_time_message));
        } else {
            // Edit mode - display item's details
            utilDisplayFoodItem();
        }

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
//            mAdapter.setFoodItem(mFoodItem);
            input.setText(mFoodItem.getName());
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_white_24dp));
            utilDisplayFoodItem();
        }
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
                // TODO Implement choosing/displaying date
                Toast.makeText(getContext(), "date clicked", Toast.LENGTH_SHORT).show();
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Implement choosing/displaying time
                Toast.makeText(getContext(), "time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "fab clicked", Toast.LENGTH_SHORT).show();

                // If no name was entered, display error to user
                // and consume click event
                if (input.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.add_edit_name_input_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEditMode) {
                    // If in edit mode - implement editing an item

                } else {
                    // If in add mode - implement adding item to DB

                }
            }

            // TODO If mFoodItem is unnecessary here, do the following:
            // A) change it to an instance variable in receiveData()
            // B) change utilDisplayFoodItem to receive a FoodItem object
            // C) and delete the member variable mFoodItem
        });
    }
}
