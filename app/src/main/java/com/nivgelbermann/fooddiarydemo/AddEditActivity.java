package com.nivgelbermann.fooddiarydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

    private boolean mEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // TODO Pass onto activity the tab (month) from which it was opened (to re-open in MainActivity)

        // Receive food item (if exists) from parent activity
        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra(FoodItem.class.getSimpleName());
        if (foodItem != null) {
            mEditMode = true;

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
        getSupportActionBar().setTitle((mEditMode) ? "Edit Item:" : "Create Item:");
        getSupportActionBar().setElevation(0);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_addedit_cancel:
                onBackPressed();
                break;

            case R.id.menu_addedit_delete:
                // TODO Handle item deletion after implementing opening activity by clicking an item
                break;

            // Handles home-button behaviour in pre-21sdk
            case android.R.id.home:
                onBackPressed();
                break;

            default:
//                throw new InvalidParameterException(TAG + ".onOptionsItemSelected called with invalid MenuItem " + item.getTitle());
                Toast.makeText(this, "An error has occurred, changes have not been saved", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}