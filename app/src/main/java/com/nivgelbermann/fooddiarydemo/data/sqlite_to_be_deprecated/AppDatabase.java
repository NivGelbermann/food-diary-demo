package com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated;

import android.content.Context;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Basic database class for the application.
 * <p>
 * The only class that should use this class is {@link AppProvider}.
 */

class AppDatabase extends SQLiteAssetHelper {
    private static final String TAG = "AppDatabase";

    private static final String DATABASE_NAME = "FoodDiaryDemo.db";
    private static final int DATABASE_VERSION = 2;

    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        // super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
    }

    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;
    }
}

// TODO Might consider making this a thread-safe singleton, by using a factory class