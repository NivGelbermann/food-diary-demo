package com.nivgelbermann.fooddiarydemo.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.nivgelbermann.fooddiarydemo.R;
import com.nivgelbermann.fooddiarydemo.data.SectionHeaderDate;
import com.nivgelbermann.fooddiarydemo.data.database.FoodEntry;
import com.nivgelbermann.fooddiarydemo.utilities.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SectionedAdapter
        extends SectionRecyclerViewAdapter<
        SectionHeaderDate,
        FoodEntry,
        SectionedAdapter.SectionViewHolder,
        SectionedAdapter.ChildViewHolder> {
    private static final String TAG = "SectionedAdapter";

    private Context mContext; // TODO Replace with WeakReference??

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sec_header_date) TextView date;
        @BindView(R.id.sec_header_weekday) TextView dayOfWeek;
        @BindView(R.id.sec_header_month) TextView month;
        @BindView(R.id.sec_header_year) TextView year;

        SectionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        // TODO Fix documentation, and document FoodEntry as section child. Fix interface+method+variable names.

        @BindView(R.id.sec_child_food_icon) ImageView icon;
        @BindView(R.id.sec_child_food_text) TextView text;
        @BindView(R.id.sec_child_food_time) TextView time;

        SectionedAdapter.ChildViewHolder.FoodItemListener mListener;
        FoodEntry mSectionChildFood;

        /**
         * Interface to be implemented by activities containing a RecyclerView
         * with {@link SectionedAdapter}.
         */
        public interface FoodItemListener {
            /**
             * Called when FoodItemViewHolder is clicked.
             *
             * @param item The SectionChildFood object contained in the clicked FoodItemViewHolder
             */
            void onFoodItemClicked(FoodEntry item);

            /**
             * Called when FoodItemViewHolder is long-clicked.
             *
             * @param item The SectionChildFood object contained in the clicked FoodItemViewHolder
             * @return Returns true if callback consumed the event, false otherwise.
             */
            boolean onFoodItemLongClicked(FoodEntry item);
        }

        ChildViewHolder(View view, final SectionedAdapter.ChildViewHolder.FoodItemListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mListener = listener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFoodItemClicked(mSectionChildFood);
                }
            });
            // Following lines commented for optimization, because
            // creating anonymous listeners is considered heavy
            /* view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mListener.onFoodItemLongClicked(mSectionChildFood);
                }
            }); */
        }

        void setFoodItem(FoodEntry item, String color) {
            try {
                mSectionChildFood = item;
                text.setText(mSectionChildFood.getName().trim());
                time.setText(Util.formatTime(mSectionChildFood.getTime(), "HH:mm"));
                icon.setColorFilter(Color.parseColor(color));
            } catch (NullPointerException e) {
                Log.d(TAG, "setFoodItem: NullPointerExcpetion caught, couldn't set viewholder's properties to given item: " + item);
                e.printStackTrace();
            }
        }

/*
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
                icon = null;
                text = null;
                time = null;
            }
        }
*/
    }

    public SectionedAdapter(Context context, List<SectionHeaderDate> sectionItemList) {
        super(context, sectionItemList);
        mContext = context;
    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_rv_section_header, sectionViewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_rv_section_child, childViewGroup, false);
        if (!(mContext instanceof ChildViewHolder.FoodItemListener)) {
            throw new ClassCastException(mContext.getClass().getSimpleName()
                    + " must implement FoodItemListener interface");
        }
        return new ChildViewHolder(view, (ChildViewHolder.FoodItemListener) mContext);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, SectionHeaderDate sectionHeaderDate) {
        Log.d(TAG, "onBindSectionViewHolder: sectionHeaderDate: " + sectionHeaderDate.toString());
        sectionViewHolder.date.setText(String.valueOf(sectionHeaderDate.getDate()));
        sectionViewHolder.month.setText(sectionHeaderDate.getMonthName());
        sectionViewHolder.year.setText(String.valueOf(sectionHeaderDate.getYear()));
        sectionViewHolder.dayOfWeek.setText(sectionHeaderDate.getDayOfWeek());
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int sectionPosition, int childPosition, FoodEntry sectionChildFood) {
        // TODO Replace hard-coded string with getting color values either from DB or from a new Map<int id, Category c> in Util class. Later on repalce with switch for choosing category icon.
        childViewHolder.setFoodItem(sectionChildFood, "#9E9E9E");
    }
}