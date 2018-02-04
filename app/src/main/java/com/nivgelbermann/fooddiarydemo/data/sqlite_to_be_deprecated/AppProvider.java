package com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated;

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
 * Provider for the FoodDiaryDemo app.
 * This is the only class that knows about {@link AppDatabase}.
 * <p>
 * Every class extending {@link ContentProvider} (e.g this class) should be declared
 * in the AndroidManifest.xml file.
 * <a href="https://developer.android.com/guide/topics/manifest/provider-element.html">For more information click here.</a>
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mAssetHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.nivgelbermann.fooddiarydemo.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int FOODS = 100;
    private static final int FOOD_ID = 101;
    private static final int CATEGORIES = 200;
    private static final int CATEGORY_ID = 201;
    // Add 2 similar constants for every future table

    // Can be replaced by a static initialization block
    // as seen in the UriMatcher google dev guide
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //eg. content://com.nivgelbermann.fooddiarydemo.provider/Foods
        matcher.addURI(CONTENT_AUTHORITY, FoodsContract.TABLE_NAME, FOODS);
        //eg. content://com.nivgelbermann.fooddiarydemo.provider/Foods/8
        matcher.addURI(CONTENT_AUTHORITY, FoodsContract.TABLE_NAME + "/#", FOOD_ID);
        matcher.addURI(CONTENT_AUTHORITY, CategoriesContract.TABLE_NAME, CATEGORIES);
        matcher.addURI(CONTENT_AUTHORITY, CategoriesContract.TABLE_NAME + "/#", CATEGORY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        // Might have to change this method to perform on a separate thread
        // because creating a DB is performance-heavy and might cause the app to freeze
        mAssetHelper = AppDatabase.getInstance(getContext());
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

            case CATEGORIES:
                queryBuilder.setTables(CategoriesContract.TABLE_NAME);
                break;

            case CATEGORY_ID:
                queryBuilder.setTables(CategoriesContract.TABLE_NAME);
                long categoryId = CategoriesContract.getCategoryId(uri);
                queryBuilder.appendWhere(CategoriesContract.Columns._ID + " = " + categoryId);
                break;

            // Add 2 cases for every table in DB

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mAssetHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Set this cursor to listen for DB content changes
        try {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (NullPointerException e) {
            Log.d(TAG, "query: NullPointerException caught, couldn't get ContentResolver instance:");
            e.printStackTrace();
            return null;
        }
        return cursor;
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

            case CATEGORIES:
                return CategoriesContract.CONTENT_TYPE;

            case CATEGORY_ID:
                return CategoriesContract.CONTENT_ITEM_TYPE;

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
                db = mAssetHelper.getWritableDatabase();
                recordId = db.insert(FoodsContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = FoodsContract.buildFoodItemUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case CATEGORIES:
                db = mAssetHelper.getWritableDatabase();
                recordId = db.insert(CategoriesContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = CategoriesContract.buildCategoryUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            // Add a case for every table in DB

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        db.close();
        if (recordId >= 0) {
            // If something was inserted, notify any listeners about changes
            Log.d(TAG, "insert: settings notifyChanged with: " + uri);
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d(TAG, "query: NullPointerException caught, couldn't get ContentResolver instance:");
                e.printStackTrace();
                return null;
            }
        } else {
            Log.d(TAG, "insert: nothing inserted");
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
                db = mAssetHelper.getWritableDatabase();
                count = db.delete(FoodsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case FOOD_ID:
                db = mAssetHelper.getWritableDatabase();
                long foodId = FoodsContract.getFoodId(uri);
                selectionCriteria = FoodsContract.Columns._ID + " = " + foodId;
                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(FoodsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case CATEGORIES:
                db = mAssetHelper.getWritableDatabase();
                count = db.delete(CategoriesContract.TABLE_NAME, selection, selectionArgs);
                break;

            case CATEGORY_ID:
                db = mAssetHelper.getWritableDatabase();
                long categoryId = CategoriesContract.getCategoryId(uri);
                selectionCriteria = CategoriesContract.Columns._ID + " = " + categoryId;
                if (selection!=null && selection.length()>0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(CategoriesContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        db.close();
        if (count > 0) {
            // If something was deleted, notify any listeners about changes
            Log.d(TAG, "delete: setting notifyChange with: " + uri);
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d(TAG, "query: NullPointerException caught, couldn't get ContentResolver instance:");
                e.printStackTrace();
                return 0;
            }
        } else {
            Log.d(TAG, "delete: nothing deleted");
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
                db = mAssetHelper.getWritableDatabase();
                count = db.update(FoodsContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case FOOD_ID:
                db = mAssetHelper.getWritableDatabase();
                long foodId = FoodsContract.getFoodId(uri);
                selectionCriteria = FoodsContract.Columns._ID + " = " + foodId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(FoodsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case CATEGORIES:
                db = mAssetHelper.getWritableDatabase();
                count = db.update(CategoriesContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CATEGORY_ID:
                db = mAssetHelper.getWritableDatabase();
                long categoryId = CategoriesContract.getCategoryId(uri);
                selectionCriteria = CategoriesContract.Columns._ID + " = " + categoryId;
                if (selection!=null && selection.length()>0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(CategoriesContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        db.close();
        if (count > 0) {
            // If something was updated, notify any listeners about changes
            Log.d(TAG, "update: setting notifyChange with: " + uri);
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d(TAG, "query: NullPointerException caught, couldn't get ContentResolver instance:");
                e.printStackTrace();
                return 0;
            }
        } else {
            Log.d(TAG, "update: nothing updated");
        }

        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }
}
