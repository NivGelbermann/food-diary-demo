package com.nivgelbermann.fooddiarydemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Niv on 10-Sep-17.
 * <p>
 * Provider for the FoodDiaryDemo app.
 * This is the only class that knows about {@link AppDatabase}.
 * <p>
 * Every class extending {@link ContentProvider} (e.g this class) should be declared
 * in the AndroidManifest.xml file.
 * <a href="https://developer.android.com/guide/topics/manifest/provider-element.html">For more information click here.</a>
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.nivgelbermann.fooddiarydemo.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int FOODS = 100;
    private static final int FOOD_ID = 101;
    // Add 2 similar constants for every future table

    // Can be replaced by a static initialization block
    // as seen in the UriMatcher google dev guide
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //eg. content://com.nivgelbermann.fooddiarydemo.provider/Foods
        matcher.addURI(CONTENT_AUTHORITY, FoodsContract.TABLE_NAME, FOODS);
        //eg. content://com.nivgelbermann.fooddiarydemo.provider/Foods/8
        matcher.addURI(CONTENT_AUTHORITY, FoodsContract.TABLE_NAME + "/#", FOOD_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        // Might have to change this method to perform on a separate thread
        // because creating a DB is performance-heavy and might cause the app to freeze
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);   // Will return FOODS or FOOD_ID
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case FOODS:
                queryBuilder.setTables(FoodsContract.TABLE_NAME);
                break;

            case FOOD_ID:
                queryBuilder.setTables(FoodsContract.TABLE_NAME);
                long foodId = FoodsContract.getFoodId(uri);
                queryBuilder.appendWhere(FoodsContract.Columns._ID + " = " + foodId);
                break;

            // Add 2 cases for every table in DB

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOODS:
                return FoodsContract.CONTENT_TYPE;

            case FOOD_ID:
                return FoodsContract.CONTENT_ITEM_TYPE;

            // Add 2 cases for every table in DB

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Entering insert, called with URI: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case FOODS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(FoodsContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = FoodsContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            // Add a case for every table in DB

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Log.d(TAG, "Exiting insert, returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete called with URI: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case FOODS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(FoodsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case FOOD_ID:
                db = mOpenHelper.getWritableDatabase();
                long foodId = FoodsContract.getFoodId(uri);
                selectionCriteria = FoodsContract.Columns._ID + " = " + foodId;

                if(selection != null && selection.length()>0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(FoodsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.d(TAG, "Exiting delete, returning " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with URI: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case FOODS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(FoodsContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case FOOD_ID:
                db = mOpenHelper.getWritableDatabase();
                long foodId = FoodsContract.getFoodId(uri);
                selectionCriteria = FoodsContract.Columns._ID + " = " + foodId;

                if(selection != null && selection.length()>0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(FoodsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }
}
