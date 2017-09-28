package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niv on 25-Sep-17.
 */
class AddEditRecyclerViewAdapter extends RecyclerView.Adapter<AddEditRecyclerViewAdapter.ItemContentViewHolder>
                                 implements RecyclerItemClickListener.OnRecyclerClickListener{
    private static final String TAG = "AddEditRecyclerViewAdap";

    private Context mContext;

    private FoodItem mItem;
    // True if AddEdit activity was opened by an item
    // False if AddEdit activity was opened by FAB
    private boolean mEditMode;
    private static final int POS_CATEGORY = 0;
    private static final int POS_DATE = 1;
    private static final int POS_TIME = 2;

    AddEditRecyclerViewAdapter(Context context, FoodItem foodItem, boolean editMode) {
        mContext = context;
        mItem = foodItem;
        mEditMode = editMode;
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
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_edit_item_details, parent, false);
        return new ItemContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemContentViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");
        
        switch (position) {
            case POS_CATEGORY:
                holder.icon.setImageResource(R.drawable.ic_cake_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_category);
                holder.content.setText("FOOD"); // Temporary - until implementing categories
                break;
                
            case POS_DATE:
                holder.icon.setImageResource(R.drawable.ic_event_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_date);
                holder.content.setText("11/11/2017"); // Temporary - until implementing dates in FoodItem
                break;
                
            case POS_TIME:
                holder.icon.setImageResource(R.drawable.ic_access_time_grey_700_24dp);
                holder.header.setText(R.string.add_edit_recyclerview_item_time);
                holder.content.setText("08:17"); // Temporary - until implementing time in FoodItem
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
    
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: called");
        
        // Temporary click verification
        Toast.makeText(mContext, "Row " + position + " clicked", Toast.LENGTH_SHORT).show();
        
        switch (position) {
            // TODO Handle each case 
            case POS_CATEGORY:
                // select category
                break;
                
            case POS_DATE:
                // display dialog for choosing date
                break;
                
            case POS_TIME:
                // display dialog for choosing time
                break;
                
            default:
                throw new InvalidParameterException(TAG + " onItemClick called with invalid position " + position);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: called");

        // Temporary long-click confirmation
        Toast.makeText(mContext, "Row " + position + " long clicked, ignoring event", Toast.LENGTH_SHORT).show();
    }
}
