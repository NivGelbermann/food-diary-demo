package com.nivgelbermann.fooddiarydemo.adapters;

import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.DateCard;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 28-Aug-17.
 */

public class OuterRecyclerViewAdapter extends RecyclerView.Adapter<OuterRecyclerViewAdapter.CardDateViewHolder> {
    private static final String TAG = "OuterRecyclerViewAdapte";

//    private Context mContext;
    private Cursor mCursor;
    private Cursor mInnerCursor;

    // Variable to collect all the InnerRecyclerViewAdapters that have been
    // bound to a ViewHolder and displayed in OuterRecyclerView
    private ArrayList<InnerRecyclerViewAdapter> mInnerAdapters;
    private InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener mInnerAdapterListener;

    static class CardDateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_header_date) TextView date;
        @BindView(R.id.card_header_weekday) TextView dayOfWeek;
        @BindView(R.id.card_header_month) TextView month;
        @BindView(R.id.card_header_year) TextView year;
        @BindView(R.id.card_recyclerview) RecyclerView innerRecyclerView;

        CardDateViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

//    public OuterRecyclerViewAdapter(Context context) {
    public OuterRecyclerViewAdapter(InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener listener) {
//        mContext = context;
        mInnerAdapterListener = listener;
        mInnerAdapters = new ArrayList<>();
    }

    @Override
    public CardDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_outer_rv_card, parent, false);
        return new CardDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardDateViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts with position " + position);

        boolean isHolderNew = (holder.date.getText().toString().trim().isEmpty());

        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            // Do nothing for now
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final DateCard date = new DateCard(
                    mCursor.getLong(mCursor.getColumnIndex(FoodsContract.Columns.DAY)),
                    "Tuesday",
                    mCursor.getLong(mCursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    mCursor.getLong(mCursor.getColumnIndex(FoodsContract.Columns.YEAR)));

            holder.date.setText(String.valueOf(date.getDate()));
            holder.dayOfWeek.setText(date.getDayOfWeek());
            holder.month.setText(String.valueOf(date.getMonth() + 1));
            holder.year.setText(String.valueOf(date.getYear()));
        }

        if (isHolderNew) {
            // If holder is new, create a new LinearLayoutManager for the holder's RecyclerView.
            // Otherwise, do nothing, holder's RecyclerView will re-use its LinearLayoutManager.
//            holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.innerRecyclerView.getContext()));
        }
        InnerRecyclerViewAdapter adapter = new InnerRecyclerViewAdapter(mInnerAdapterListener);
//                (InnerRecyclerViewAdapter.FoodItemViewHolder.FoodItemListener) mContext);
        mInnerAdapters.add(adapter);
        holder.innerRecyclerView.setAdapter(adapter);

        // By default, the inner loader in PageFragment has finished loading
        // before this OuterAdapter has even started initializing the adapters for
        // all the InnerAdapters.
        // That's why we need to force the InnerAdapter to swap cursor
        // (actually swapping with the same cursor it already has)
        // to make it refresh its rows.
        adapter.swapCursor(mInnerCursor);

        Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        if (mCursor == null || mCursor.getCount() == 0) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Swap in a new Cursor, returning the old Cursor. <p>
     * The returned old Cursor in <em>not</em> closed.
     *
     * @param newCursor The new Cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor: starts");
        if (newCursor == mCursor) {
            Log.d(TAG, "swapCursor: ends, returning null because cursor hasn't changed");
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            // Notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // Notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        Log.d(TAG, "swapCursor: ends, returning old cursor");
        return oldCursor;
    }

    public Cursor swapInnerCursors(Cursor newCursor) {
        Log.d(TAG, "swapInnerCursors: starts");
        Cursor oldCursor = null;
        for (InnerRecyclerViewAdapter adapter : mInnerAdapters) {
            oldCursor = adapter.swapCursor(newCursor);
        }
        mInnerCursor = newCursor;
        Log.d(TAG, "swapInnerCursors: ends");
        return oldCursor;
    }

}
