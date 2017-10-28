package com.nivgelbermann.fooddiarydemo.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Niv on 24-Oct-17.
 */

public class CategoriesContract {

    static final String TABLE_NAME = "Categories";

    // Categories table fields
    public static class Columns implements BaseColumns {
        // If you ever change the database schema,
        // you must increment the database version in AppDatabase.

        // Default-created _id column represents category ID
        // as used in Foods table
        public static final String NAME = "Name";           // TEXT
        public static final String COLOR = "Color";         // INTEGER - represents color resource id

        private Columns() {
            // Private constructor to prevent instantiation
        }
    }

    /**
     * The Uri to access the Categories table in the DB
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildCategoryUri(long categoryId) {
        return ContentUris.withAppendedId(CONTENT_URI, categoryId);
    }

    public static long getCategoryId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
