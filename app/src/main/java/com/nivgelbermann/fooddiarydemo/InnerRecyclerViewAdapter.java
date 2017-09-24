package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 06-Sep-17.
 */

class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.FoodItemViewHolder> {
    private static final String TAG = "InnerRecyclerViewAdapte";

    private Context mContext;
    private Cursor mCursor;

    InnerRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_icon)
        ImageView icon;
        @BindView(R.id.food_text)
        TextView text;
        @BindView(R.id.food_time)
        TextView time;

        FoodItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

//        holder.text.setText("item number #" + position);
//        holder.time.setText("13:24");

        if((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: mCursor empty or null");
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final FoodItem row = new FoodItem(mCursor.getString(mCursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                    mCursor.getLong(mCursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                    mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID)));

            // TODO Handle changing row icon to match category
            holder.text.setText(row.getName());
            holder.time.setText(utilEpochToFormattedTime(row.getHour(), "HH:mm"));
        }

        Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
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
    Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor: starts");
        if (newCursor == mCursor) {
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

        return oldCursor;
    }

    /**
     * Utility method for converting time in Epoch format to
     * a formatted String.
     *
     * @param time       long, representing time as seconds since Epoch
     * @param timeFormat String format for return value
     * @return String for time formatted
     */
    private String utilEpochToFormattedTime(long time, String timeFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(new Date(time * Constants.MILLISECONDS));
    }
}
