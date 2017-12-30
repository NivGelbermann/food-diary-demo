package com.nivgelbermann.fooddiarydemo.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Niv on 09-Sep-17.
 */

public class FoodsContract {

    public static final String TABLE_NAME = "Foods";

    // Foods table fields
    public static class Columns implements BaseColumns {
        // If you ever change the database schema,
        // you must increment the database version in AppDatabase.
        public static final String DAY = "Day";             // INTEGER
        public static final String MONTH = "Month";         // INTEGER where JAN=0, DEC=11
        public static final String YEAR = "Year";           // INTEGER
        public static final String HOUR = "Hour";           // INTEGER as Unix Time, the number of seconds since 1970-01-01 00:00:00 UTC.
        public static final String FOOD_ITEM = "FoodItem";  // TEXT
        public static final String CATEGORY_ID = "CategoryID";   // INTEGER
        // TODO Add column for calories (in all files - loaders, etc...)

        // Sort Order: Year -> Month -> Day -> Hour


        private Columns() {
            // Private constructor to prevent instantiation
        }
    }


    /**
     * The Uri to access the Foods table in the DB
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildFoodItemUri(long foodItemId) {
        return ContentUris.withAppendedId(CONTENT_URI, foodItemId);
    }

    public static long getFoodId(Uri uri) {
        return ContentUris.parseId(uri);
    }


}
