package com.nivgelbermann.fooddiarydemo.adapters;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.CategoriesContract;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;
import com.nivgelbermann.fooddiarydemo.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 06-Sep-17.
 */

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.FoodItemViewHolder> {
    private static final String TAG = "InnerRecyclerViewAdapte";

    private Cursor mCursor;
    private FoodItemViewHolder.FoodItemListener mFoodItemListener;
    private int mRepresentedDayOfMonth;

    InnerRecyclerViewAdapter(FoodItemViewHolder.FoodItemListener listener, int dayOfMonth) {
        mFoodItemListener = listener;
        mRepresentedDayOfMonth = dayOfMonth;
    }

    public static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_icon) ImageView icon;
        @BindView(R.id.food_text) TextView text;
        @BindView(R.id.food_time) TextView time;

        FoodItemListener mListener;
        FoodItem mFoodItem;

        /**
         * Interface to be implemented by activities containing a RecyclerView
         * with {@link InnerRecyclerViewAdapter}.
         */
        public interface FoodItemListener {
            /**
             * Called when FoodItemViewHolder is clicked.
             *
             * @param item The FoodItem object contained in the clicked FoodItemViewHolder
             */
            void onFoodItemClicked(FoodItem item);

            /**
             * Called when FoodItemViewHolder is long-clicked.
             *
             * @param item The FoodItem object contained in the clicked FoodItemViewHolder
             * @return Returns true if callback consumed the event, false otherwise.
             */
            boolean onFoodItemLongClicked(FoodItem item);
        }

        FoodItemViewHolder(View view, final FoodItemListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mListener = listener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFoodItemClicked(mFoodItem);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mListener.onFoodItemLongClicked(mFoodItem);
                }
            });
        }

        void setFoodItem(FoodItem item) {
            try {
                mFoodItem = item;
                text.setText(mFoodItem.getName());
                time.setText(Util.formatTime(mFoodItem.getTime(), "HH:mm"));
            } catch (NullPointerException e) {
                Log.d(TAG, "setFoodItem: NullPointerExcpetion caught, couldn't set viewholder's properties to given item: " + item);
                e.printStackTrace();
            }

            ContentResolver contentResolver = icon.getContext().getContentResolver();
            Cursor cursor = contentResolver.query(
                    CategoriesContract.buildCategoryUri(item.getCategoryId()),
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                icon.setColorFilter(Color.parseColor(
                        cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.COLOR))));
                cursor.close();
            }
        }

        void hide() {
            if (icon != null) {
                icon.setVisibility(View.GONE);
            }
            if (text != null) {
                text.setVisibility(View.GONE);
            }
            if (time != null) {
                time.setVisibility(View.GONE);
            }
        }

        void destroy() {
            if (icon != null) {
                ((ViewGroup) icon.getParent()).removeAllViews();
//            ((ViewGroup) icon.getParent()).removeView(icon);
//            ((ViewGroup) text.getParent()).removeView(text);
//            ((ViewGroup) time.getParent()).removeView(time);
                icon = null;
                text = null;
                time = null;
            }
        }
    }


    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inner_rv_food_item, parent, false);
        return new FoodItemViewHolder(view, mFoodItemListener);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        // Log.d(TAG, "onBindViewHolder: starts with position " + position);

        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: mCursor empty or null");
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            int currentDay = mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.DAY));
            if (currentDay != mRepresentedDayOfMonth) {
                // Commented out logging for testing
                // Log.d(TAG, "onBindViewHolder: DB item at position " + position + " is not from represented day: " + mRepresentedDayOfMonth);
                // Log.d(TAG, "onBindViewHolder: item's current day: " + currentDay);
                holder.destroy();
                holder = null;
                return;
            }

            final FoodItem row = new FoodItem(
                    mCursor.getString(mCursor.getColumnIndex(FoodsContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                    mCursor.getLong(mCursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                    mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.DAY)),
                    mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                    mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.YEAR)),
                    mCursor.getInt(mCursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID)));
            holder.setFoodItem(row);
        }

        // Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
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
        Log.d(TAG, mRepresentedDayOfMonth + " swapCursor: starts");
        if (newCursor == mCursor) {
            Log.d(TAG, mRepresentedDayOfMonth + " swapCursor: ends, returning null because cursor hasn't changed");
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

        Log.d(TAG, mRepresentedDayOfMonth + " swapCursor: ends, returning old cursor");
        return oldCursor;
    }

}