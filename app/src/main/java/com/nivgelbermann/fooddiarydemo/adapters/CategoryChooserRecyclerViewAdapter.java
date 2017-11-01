package com.nivgelbermann.fooddiarydemo.adapters;

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
import com.nivgelbermann.fooddiarydemo.data.Category;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 29-Oct-17.
 */

public class CategoryChooserRecyclerViewAdapter
        extends RecyclerView.Adapter<CategoryChooserRecyclerViewAdapter.CategoryViewHolder> {
    private static final String TAG = "CategoryChooserRecycler";

    private Cursor mCursor;
    private CategoryViewHolder.CategoryListener mCategoryListener;
    private int mSelectedCategory;

    public CategoryChooserRecyclerViewAdapter(CategoryViewHolder.CategoryListener listener, int selectedId) {
        mCategoryListener = listener;
        Log.d(TAG, "CategoryChooserRecyclerViewAdapter: listener: " + listener);
        mSelectedCategory = selectedId;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cc_row_category_ic) ImageView icon;
        @BindView(R.id.cc_row_category_name) TextView name;
        @BindView(R.id.cc_row_checked_ic) ImageView checked;
        @BindView(R.id.cc_row_divider) View divider;
        // TODO Copy divider to row_outer_rv_card

        CategoryListener mListener;
        Category mCategory;

        /**
         * Interface to be implemented by activities containing a RecyclerView
         * with {@link CategoryChooserRecyclerViewAdapter}.
         */
        public interface CategoryListener {
            /**
             * Called when CategoryViewHolder is clicked.
             *
             * @param category The Category object contained in the clicked CategoryViewHolder
             */
            void onCategoryClicked(Category category);

            /**
             * Called when CategoryViewHolder is long-clicked.
             *
             * @param category The Category object contained in the clicked CategoryViewHolder
             * @return Returns true if callback consumed the event, false otherwise.
             */
            boolean onCategoryLongClicked(Category category);
        }

        CategoryViewHolder(View view, final CategoryListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mListener = listener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onCategoryClicked(mCategory);
                }
            });
        }

        void setCategory(Category category) {
            mCategory = category;
            icon.setColorFilter(Color.parseColor(category.getColor()));
            name.setText(category.getName());
            setSelected(false);
        }

        void setSelected(boolean selected) {
            checked.setVisibility((selected ? View.VISIBLE : View.GONE));
        }

    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_chooser_category, parent, false);
        return new CategoryViewHolder(view, mCategoryListener);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts with position: " + position);

        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: mCursor empty or null");
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Category row = new Category(
                    mCursor.getString(mCursor.getColumnIndex(CategoriesContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(CategoriesContract.Columns.NAME)),
                    mCursor.getString(mCursor.getColumnIndex(CategoriesContract.Columns.COLOR)));
            holder.setCategory(row);
            if (mSelectedCategory == Integer.parseInt(row.getId())) {
                holder.setSelected(true);
            }
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

        Log.d(TAG, "swapCursor: ends");
        return oldCursor;
    }
}