package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Niv on 09-Sep-17.
 * <p>
 * Basic database class for the application.
 * <p>
 * The only class that should use this class is {@link AppProvider}.
 */

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    // If you ever change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "FoodDiaryDemo.db";
    public static final int DATABASE_VERSION = 1;

    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object.
     *
     * @param context The content provider's context.
     * @return A SQLite database helper object.
     */
    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;    // Use a string variable to facilitate logging
        sSQL = "CREATE TABLE " + FoodsContract.TABLE_NAME + " (" +
                FoodsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                FoodsContract.Columns.DAY + " INTEGER NOT NULL, " +
                FoodsContract.Columns.MONTH + " INTEGER NOT NULL, " +
                FoodsContract.Columns.YEAR + " INTEGER NOT NULL, " +
                FoodsContract.Columns.HOUR + " INTEGER NOT NULL, " +
                FoodsContract.Columns.FOOD_ITEM + " TEXT NOT NULL, " +
                FoodsContract.Columns.CATEGORY_ID + " INTEGER NOT NULL)";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TABLE " + CategoriesContract.TABLE_NAME + " (" +
                CategoriesContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                CategoriesContract.Columns.NAME + " TEXT NOT NULL, " +
                CategoriesContract.Columns.COLOR + " INTEGER NOT NULL, " +
                CategoriesContract.Columns.SORTORDER + " INTEGER NOT NULL);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}


// TODO Might consider making this a thread-safe singleton, by using a factory class