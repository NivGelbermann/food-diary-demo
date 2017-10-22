package com.nivgelbermann.fooddiarydemo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);


        AddEditActivityFragment fragment = new AddEditActivityFragment();
        Bundle arguments = getIntent().getExtras();
        boolean editMode = arguments != null;
        fragment.setArguments(arguments);

        if (editMode) {

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
}