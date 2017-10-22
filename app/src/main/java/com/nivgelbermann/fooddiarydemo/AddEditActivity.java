package com.nivgelbermann.fooddiarydemo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity
        implements AddEditActivityFragment.OnFinished {
    private static final String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

//        boolean editMode = false;
//
//        // Receive food item (if exists) from parent activity
//        Intent intent = getIntent();
//        FoodItem foodItem = (FoodItem) intent.getSerializableExtra(FoodItem.class.getSimpleName());
//        if (foodItem != null) {
//            editMode = true;
//
//              // Verify: the fragment that will receive the food item is capable of receiving data
//            Fragment fragment = getSupportFragmentManager().getFragments().get(0);
//            if (!(fragment instanceof PassActivityDataToFragment)) {
//                throw new ClassCastException(fragment.getClass().getSimpleName()
//                        + " must implement PassActivityDataToFragment interface");
//            } else {
//                PassActivityDataToFragment callback = (PassActivityDataToFragment) fragment;
//                callback.receiveData(foodItem);
//            }
//        }
        AddEditActivityFragment fragment = new AddEditActivityFragment();
        Bundle arguments = getIntent().getExtras();
        boolean editMode = arguments != null;
        fragment.setArguments(arguments);

        if (editMode) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment oldFragment = fragmentManager.getFragments().get(0);
//            oldFragment.onDestroyView();
//            fragmentManager.popBackStackImmediate();

//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.add_edit_fragment, fragment)
//                    .commit();

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.add_edit_fragment));
//            fragmentManager.executePendingTransactions();
//            fragmentTransaction.add(R.id.add_edit_fragment, fragment).commit();
//
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.add_edit_fragment)).commit();
            fragmentManager.executePendingTransactions();
            fragmentManager.beginTransaction().add(R.id.add_edit_fragment, fragment).commit();

//            utilClearBackStack();
//            getSupportFragmentManager().popBackStackImmediate(R.id.add_edit_fragment, 0);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.add_edit_fragment, fragment)
//                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        Resources resources = getResources();
        getSupportActionBar().setTitle((editMode)
                ? resources.getString(R.string.add_edit_activity_header_edit)
                : resources.getString(R.string.add_edit_activity_header_add));
        getSupportActionBar().setElevation(0);
        Log.d(TAG, "onCreate: ends");
    }

//    private void utilClearBackStack() {
//        FragmentManager manager = getSupportFragmentManager();
//        if(manager.getBackStackEntryCount() > 0) {
//            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
//            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
//    }


    @Override
    public void onAddEditFinished() {
        Log.d(TAG, "onAddEditFinished: called");
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(R.id.add_edit_fragment);
//        if (fragment != null) {
//            fragmentManager.beginTransaction()
//                    .remove(fragment)
//                    .commit();
//        }
        finish();
    }
}