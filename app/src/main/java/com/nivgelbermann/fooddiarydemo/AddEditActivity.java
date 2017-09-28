package com.nivgelbermann.fooddiarydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

//    @BindView(R.id.add_edit_toolbar)
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // TODO If a back button does not exist in runtime, uncomment these lines and play around with the App Theme for all 3 add_edit files
        // OR!!! choose R.id.add_edit_toolbar as the toolbar in question


//        setSupportActionBar(toolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
