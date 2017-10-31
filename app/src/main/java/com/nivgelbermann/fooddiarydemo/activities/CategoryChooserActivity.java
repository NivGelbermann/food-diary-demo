package com.nivgelbermann.fooddiarydemo.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.adapters.CategoryChooserRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.data.CategoriesContract;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryChooserActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "CategoryChooserActivity";
    private static final int CATEGORIES_LOADER_ID = 0;

    @BindView(R.id.category_chooser_recyclerview) RecyclerView recyclerView;

    private CategoryChooserRecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mAdapter = new CategoryChooserRecyclerViewAdapter(
                (CategoryChooserRecyclerViewAdapter.CategoryViewHolder.CategoryListener)
                        this.getParent());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(CATEGORIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: called with id:" + id);

        switch (id) {
            case CATEGORIES_LOADER_ID:
                return new CursorLoader(this,
                        CategoriesContract.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            // Add cases for other Loader Id's for loading information from other databases

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");
        if (data == null) {
            throw new InvalidParameterException(TAG + ".onLoadFinished called with null cursor");
        }

        switch (loader.getId()) {
            case CATEGORIES_LOADER_ID:
                Log.d(TAG, "onLoadFinished: starts with id CATEGORIES_LOADER_ID");
                mAdapter.swapCursor(data);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFInished called with invalid loader");
        }
        Log.d(TAG, "onLoadFinished: ends with item count: " + mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");

        switch (loader.getId()) {
            case CATEGORIES_LOADER_ID:
                mAdapter.swapCursor(null);
                break;

            default:
                throw new InvalidParameterException(TAG + ".onLoadFInished called with invalid loader");
        }
    }
}
