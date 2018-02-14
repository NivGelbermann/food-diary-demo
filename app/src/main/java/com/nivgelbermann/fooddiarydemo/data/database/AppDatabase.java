package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

/**
 * {@link AppDatabase} database for the application, including
 * a table for {@link FoodEntry} with the DAO {@link FoodDao}.
 */

@Database(entities = {FoodEntry.class}, version = 1)
@TypeConverters({CalendarConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "food-diary-database";

    // Singleton instantiation
    private static AppDatabase sInstance;
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        Log.d(TAG, "getInstance: getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME).build();
                Log.d(TAG, "getInstance: instantiated new database");
            }
        }
        return sInstance;
    }

    /**
     * Exposes DAO for interacting with 'food' table in database
     *
     * @return {@link FoodDao}
     */
    public abstract FoodDao foodDao();

    /* =========== Example Migration class ===========
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(=== Enter SQLite query to alter database here ===);
            // Also, add the following before calling build() in getInstance:
            // .addMigrations(MIGRATION_1_2).
            // Android documentation guides also explain how to test migrations!
        }
    }; */


}
