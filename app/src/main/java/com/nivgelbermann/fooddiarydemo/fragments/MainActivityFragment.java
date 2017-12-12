package com.nivgelbermann.fooddiarydemo.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nivgelbermann.fooddiarydemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";

    private static final int OUTER_LOADER_ID = 0;
    private static final int INNER_LOADER_ID = 1;

//    @BindView(R.id.main_outer_recyclerview) RecyclerView recyclerView;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        ButterKnife.bind(this, view);



        Log.d(TAG, "onCreateView: ends, returning view");
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
