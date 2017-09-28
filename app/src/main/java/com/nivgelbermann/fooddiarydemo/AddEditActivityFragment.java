package com.nivgelbermann.fooddiarydemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

//    @BindView(R.id.add_edit_toolbar)
//    Toolbar toolbar;
    @BindView(R.id.add_edit_recyclerview)
    RecyclerView recyclerView;

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
//        RecyclerView recyclerView = view.findViewById(R.id.add_edit_recyclerview);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AddEditRecyclerViewAdapter(getContext(), null, false));

//        ((AddEditActivity) getActivity()).setSupportActionBar(toolbar);

        Log.d(TAG, "onCreateView: ends");
        return view;
    }
}
