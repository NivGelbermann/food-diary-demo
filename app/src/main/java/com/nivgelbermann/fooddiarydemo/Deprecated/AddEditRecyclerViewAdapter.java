package com.nivgelbermann.fooddiarydemo.Deprecated;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nivgelbermann.fooddiarydemo.FoodItem;
import com.nivgelbermann.fooddiarydemo.MainActivity;
import com.nivgelbermann.fooddiarydemo.R;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 25-Sep-17.
 */
class AddEditRecyclerViewAdapter extends RecyclerView.Adapter<AddEditRecyclerViewAdapter.ItemContentViewHolder> {
    private static final String TAG = "AddEditRecyclerViewAdap";

    private Context mContext;

    private FoodItem mFoodItem;
    private String mDate;
    private String mTime;
    private static final int POS_CATEGORY = 0;
    private static final int POS_DATE = 1;
    private static final int POS_TIME = 2;

    AddEditRecyclerViewAdapter(Context context, FoodItem foodItem) {
        mContext = context;
        mFoodItem = foodItem;
    }

    class ItemContentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.add_edit_row_ic) ImageView icon;
        @BindView(R.id.add_edit_row_header) TextView header;
        @BindView(R.id.add_edit_row_content) TextView content;

        ItemContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ItemContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_edit_item_details, parent, false);
        return new ItemContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemContentViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

        switch (position) {
            case POS_CATEGORY:
                // TODO Handle changing row icon to match category
                holder.icon.setImageResource(R.drawable.ic_cake_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_category);
                holder.content.setText(R.string.add_edit_recyclerview_item_category_message); // Temporary - until implementing categories
                break;

            case POS_DATE:
                holder.icon.setImageResource(R.drawable.ic_event_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_date);
                if (mFoodItem != null) {
                    holder.content.setText(mFoodItem.getDay() + "/"
                            + mFoodItem.getMonth() + "/"
                            + mFoodItem.getYear());
                } else {
                    holder.content.setText(R.string.add_edit_recyclerview_item_date_message);
                }
                break;

            case POS_TIME:
                holder.icon.setImageResource(R.drawable.ic_access_time_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_time);
                if (mFoodItem != null) {
                    holder.content.setText(MainActivity.utilFormatTime(mFoodItem.getTime(), "HH:mm"));
                } else {
                    holder.content.setText(R.string.add_edit_recyclerview_item_time_message);
                }
                break;

            default:
                throw new InvalidParameterException(TAG + ".onBindViewHolder called with invalid position: " + position);
        }

        Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        // There are always 3 items: Category, Date, Hour
        return 3;
    }

    public void setFoodItem(FoodItem item) {
        Log.d(TAG, "setFoodItem: called");
        mFoodItem = item;
        notifyDataSetChanged();
    }

}
