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
import com.nivgelbermann.fooddiarydemo.activities.MainActivity;
import com.nivgelbermann.fooddiarydemo.data.CategoriesContract;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 06-Sep-17.
 */

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.FoodItemViewHolder> {
    private static final String TAG = "InnerRecyclerViewAdapte";

    private Cursor mCursor;
    private FoodItemViewHolder.FoodItemListener mFoodItemListener;

    InnerRecyclerViewAdapter(FoodItemViewHolder.FoodItemListener listener) {
        mFoodItemListener = listener;
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

//        void setFoodItem(FoodItem item, ContentResolver contentResolver) {
        void setFoodItem(FoodItem item) {
            mFoodItem = item;
            // TODO Handle changing row icon to match category
            text.setText(mFoodItem.getName());
            time.setText(MainActivity.utilFormatTime(mFoodItem.getTime(), "HH:mm"));

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
    }


    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inner_rv_food_item, parent, false);
        return new FoodItemViewHolder(view, mFoodItemListener);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts with position " + position);

        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: mCursor empty or null");
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
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

        Log.d(TAG, "onBindViewHolder: ends");
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
}


// General tip - if you ever need to find the current position, just call getAdapterPosition().