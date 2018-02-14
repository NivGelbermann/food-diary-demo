package com.nivgelbermann.fooddiarydemo.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.data.database.FoodDao;
import com.nivgelbermann.fooddiarydemo.data.database.FoodEntry;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Handles data operations. Acts as a mediator between any data source
 * (for now only {@link FoodDao}) and the app.
 */

/* TODO Move database handling to disk thread?
   https://codelabs.developers.google.com/codelabs/build-app-with-arch-components/index.html?index=..%2F..%2Findex#10
   In above codelab, under "Your Turn: Observe the LiveData" clause 3, we're instructed to move
   DB handling to a different thread using local class AppExecutors.
   Doesn't Room do that for us with every Dao method?
 */

public class FoodRepository {
    private static final String TAG = "FoodRepository";

    // Singleton instantiation
    private static final Object LOCK = new Object();
    private static FoodRepository sInstance;
    private LiveData<List<FoodEntry>> mFoodCache = null;

    private final FoodDao mFoodDao;
    // private final AppExecutors mAppExecutors; // Example in Sunshine project. Useful for sending tasks to different pre-defined threads.

    private FoodRepository(FoodDao foodDao) {
        mFoodDao = foodDao;

        initializeData(); // TODO Is this necessary? Do I ever actually need the WHOLE list of entries?

        // If observing any network data, register observation here
    }

    /**
     * Initialize the repository by populating the local cache with data from the database.
     */
    private void initializeData() {
        // Only initialize once per app lifetime.
        if (mFoodCache != null) { // if local cache has not been initialized with data, the repository has just been created
            return;
        }
        mFoodCache = getAll();
    }

    public synchronized static FoodRepository getInstance(FoodDao foodDao) {
        Log.d(TAG, "getInstance: getting repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: creating new repository");
                sInstance = new FoodRepository(foodDao);
            }
        }
        return sInstance;
    }

    /* ============= Database related methods ============= */

    /**
     * Get all food entries ({@link FoodEntry}).
     *
     * @return
     */
    public LiveData<List<FoodEntry>> getAll() {
        if (mFoodCache == null) {
            return mFoodDao.getAll();
        }
        return mFoodCache;
    }

    /**
     * Get list of all food entries for certain date.
     *
     * @param day   int for day. May be null to receive all food entries for this month.
     * @param month int for month.
     * @param year  int for year.
     * @return {@link LiveData} containing {@link List} of {@link FoodEntry} objects for chosen date.
     */
    public LiveData<List<FoodEntry>> getByDate(@Nullable Integer day, int month, int year) {
        // TODO Conditions can probably be streamlined
        if (!(month > 0 && year > 0)) {
            throw new InvalidParameterException(TAG + ".getByDate: invalid date received");
        }
        if (day != null) {
            if (!(day > 0)) {
                return mFoodDao.getByTime(day, month, year);
            }
            throw new InvalidParameterException(TAG + ".getByDate: invalid date received");
        }
        return mFoodDao.getByTime(month, year);
    }

    /**
     * Get food entry by id
     *
     * @param id int id.
     * @return {@link LiveData} containing {@link FoodEntry}.
     */
    public LiveData<FoodEntry> getById(int id) {
        return mFoodDao.getById(id);
    }

    /**
     * Save food entry.
     *
     * @param entry {@link FoodEntry} to save.
     */
    public void save(FoodEntry entry) {
        if (!entryValid(entry)) {
            throw new InvalidParameterException(TAG + ".save: invalid entry received");
        }
        mFoodDao.insert(entry);
    }

    /**
     * Delete food entry.
     *
     * @param entry {@link FoodEntry} to delete.
     */
    public void delete(FoodEntry entry) {
        if (!entryValid(entry)) {
            throw new InvalidParameterException(TAG + ".delete: invalid entry received");
        }
        mFoodDao.delete(entry);
    }

    /**
     * Verify given food entry's validity.
     *
     * @param entry {@link FoodEntry} to verify.
     * @return true if valid, otherwise false.
     */
    private boolean entryValid(FoodEntry entry) {
        return entry.getName() != null
                && !entry.getName().trim().isEmpty()
                && entry.getTime() != null
                && entry.getCategory() > 0;
    }
}
