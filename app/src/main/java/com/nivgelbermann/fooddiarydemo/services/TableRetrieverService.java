package com.nivgelbermann.fooddiarydemo.services;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.nivgelbermann.fooddiarydemo.data.CategoriesContract;
import com.nivgelbermann.fooddiarydemo.data.FoodsContract;
import com.nivgelbermann.fooddiarydemo.fragments.PageFragment;
import com.nivgelbermann.fooddiarydemo.models.FoodItem;

import java.security.InvalidParameterException;
import java.util.ArrayList;


/**
 * Service designed to retrieve all data in a DB table.
 */
public class TableRetrieverService extends JobIntentService {
    private static final String TAG = "TableRetrieverService";

    public static final String TABLE_NAME = "TableName";
    public static final String MONTH_TAG = "Month";
    public static final String YEAR_TAG = "Year";
    public static final String LIST_RESULT = "ListResult";

    public TableRetrieverService() {
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null) {
            throw new IllegalStateException(TAG + ".onHandleWork: couldn't get ContentResolver.");
        }

        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;
        Cursor cursor;
        Intent broadcastIntent = new Intent();

        switch (intent.getStringExtra(TABLE_NAME)) {
            case FoodsContract.TABLE_NAME:
                projection = new String[]{FoodsContract.Columns._ID,
                        FoodsContract.Columns.FOOD_ITEM,
                        FoodsContract.Columns.DAY,
                        FoodsContract.Columns.MONTH,
                        FoodsContract.Columns.YEAR,
                        FoodsContract.Columns.HOUR,
                        FoodsContract.Columns.CATEGORY_ID};
                selection = FoodsContract.Columns.MONTH + "=? AND "
                        + FoodsContract.Columns.YEAR + "=?";
                selectionArgs = new String[]{
                        String.valueOf(intent.getIntExtra(MONTH_TAG, -1)),
                        String.valueOf(intent.getIntExtra(YEAR_TAG, -1))};
                sortOrder = FoodsContract.Columns.HOUR + " DESC,"
                        + FoodsContract.Columns.FOOD_ITEM + " COLLATE NOCASE DESC";
                cursor = contentResolver.query(FoodsContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                if (cursor == null || !cursor.moveToFirst()) {
                    throw new IllegalStateException(TAG + "onHandleWork: Couldn't move cursor to first");
                }
                ArrayList<FoodItem> items = new ArrayList<>();
                do {
                    items.add(new FoodItem(
                            cursor.getString(cursor.getColumnIndex(FoodsContract.Columns._ID)),
                            cursor.getString(cursor.getColumnIndex(FoodsContract.Columns.FOOD_ITEM)),
                            cursor.getLong(cursor.getColumnIndex(FoodsContract.Columns.HOUR)),
                            cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.DAY)),
                            cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.MONTH)),
                            cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.YEAR)),
                            cursor.getInt(cursor.getColumnIndex(FoodsContract.Columns.CATEGORY_ID))));
                } while (cursor.moveToNext());
                cursor.close();

                broadcastIntent.setAction(PageFragment.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(LIST_RESULT, items);
                sendBroadcast(broadcastIntent);
                break;

            case CategoriesContract.TABLE_NAME:
                break;

            default:
                throw new InvalidParameterException(TAG + ".onHandleWork: called with invalid table name packaged in intent");
        }
    }
}
