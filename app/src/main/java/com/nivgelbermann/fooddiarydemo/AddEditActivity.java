package com.nivgelbermann.fooddiarydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        boolean editMode = false;

        // Receive food item (if exists) from parent activity
        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra(FoodItem.class.getSimpleName());
        if (foodItem != null) {
            editMode = true;

            // Verify: the fragment that will receive the food item is capable of receiving data
            Fragment fragment = getSupportFragmentManager().getFragments().get(0);
            if (!(fragment instanceof PassActivityDataToFragment)) {
                throw new ClassCastException(fragment.getClass().getSimpleName()
                        + " must implement PassActivityDataToFragment interface");
            } else {
                PassActivityDataToFragment callback = (PassActivityDataToFragment) fragment;
                callback.receiveData(foodItem);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setTitle((editMode) ? "Edit Item:" : "Create Item:");
        getSupportActionBar().setElevation(0);
        Log.d(TAG, "onCreate: ends");
    }

}