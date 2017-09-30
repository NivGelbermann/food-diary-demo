package com.nivgelbermann.fooddiarydemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivityFragment extends Fragment implements PassActivityDataToFragment {
    private static final String TAG = "AddEditActivityFragment";

    @BindView(R.id.add_edit_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.add_edit_input)
    EditText input;
    @BindView(R.id.add_edit_fab)
    FloatingActionButton fab;

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

        // Initialize fixed-size RecyclerView for item's category, date, time, etc.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AddEditRecyclerViewAdapter(getContext(), mFoodItem));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Temporary - replace with updating DB according to mEditMode
                Log.d(TAG, "onClick: " + ((mEditMode) ? "SAVE" : "SEND") + " clicked");
            }
        });

        Log.d(TAG, "onCreateView: ends");
        return view;
    }

    /**
     * Callback method for receiving a Serializable (FoodItem) object from containing activity. <p>
     * If data was received, fragment will edit it and update the DB.
     * Otherwise, fragment will add new data to DB.
     * @param data item to be edited, or null for adding new data.
     */
    @Override
    public void receiveData(Serializable data) {
        mFoodItem = (FoodItem) data;
        if (mFoodItem != null) {
            mEditMode = true;
            input.setText(mFoodItem.getName());
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_white_24dp));
        }
    }
}
