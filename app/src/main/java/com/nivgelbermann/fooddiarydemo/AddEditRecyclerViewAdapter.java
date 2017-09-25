package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 25-Sep-17.
 */

class AddEditRecyclerViewAdapter extends RecyclerView.Adapter<AddEditRecyclerViewAdapter.ItemContentViewHolder> {
    private static final String TAG = "AddEditRecyclerViewAdap";

    private Context mContext;

    private FoodItem mItem;

    AddEditRecyclerViewAdapter(Context context, FoodItem foodItem) {
        mContext = context;
        mItem = foodItem;
    }

    class ItemContentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.add_edit_row_ic)
        ImageView icon;
        @BindView(R.id.add_edit_row_header)
        TextView header;
        @BindView(R.id.add_edit_row_content)
        TextView content;

        ItemContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ItemContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ItemContentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        // There are always 3 items: Category, Date, Hour
        return 3;
    }
}
