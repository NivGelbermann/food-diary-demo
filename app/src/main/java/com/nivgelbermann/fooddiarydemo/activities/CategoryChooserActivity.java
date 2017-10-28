package com.nivgelbermann.fooddiarydemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nivgelbermann.fooddiarydemo.R;

public class CategoryChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
