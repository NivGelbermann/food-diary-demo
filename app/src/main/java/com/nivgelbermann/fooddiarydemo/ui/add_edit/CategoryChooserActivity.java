package com.nivgelbermann.fooddiarydemo.ui.add_edit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated.CategoriesContract;
import com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated.Category;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryChooserActivity extends AppCompatActivity
        implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CategoryChooserRecyclerViewAdapter.CategoryViewHolder.CategoryListener {
    private static final String TAG = "CategoryChooserActivity";

    @BindView(R.id.category_chooser_recyclerview) RecyclerView recyclerView;

    private static final int CATEGORIES_LOADER_ID = 0;

    private CategoryChooserRecyclerViewAdapter mAdapter;
    private int mSelectedCategory = 1;
    private boolean mCategoryChosen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chooser);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            mSelectedCategory = arguments.getInt(CategoriesContract.Columns._ID);
        }

        mAdapter = new CategoryChooserRecyclerViewAdapter(this, mSelectedCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mCategoryChosen) {
            setResult(RESULT_CANCELED);
        }
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

    @Override
    public void onCategoryClicked(Category category) {
        // Return selected category to AddEditActivity
        Intent data = new Intent();
        data.putExtra(Category.class.getSimpleName(), category);
        mCategoryChosen = true;
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCategoryLongClicked(Category category) {
        // Ignore long clicks - do nothing, consume event.
        return true;
    }
}
