package com.nivgelbermann.fooddiarydemo.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.nivgelbermann.fooddiarydemo.data.database.FoodDao;
import com.nivgelbermann.fooddiarydemo.data.database.FoodEntry;
import com.nivgelbermann.fooddiarydemo.ui.history.HistoryFragment;

import java.security.InvalidParameterException;
import java.util.Calendar;
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

    private final FoodDao mFoodDao;
    // private final AppExecutors mAppExecutors; // Example in Sunshine project. Useful for sending tasks to different pre-defined threads.

    private FoodRepository(FoodDao foodDao) {
        mFoodDao = foodDao;
        // If observing any network data, register observation here
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

    /* ============================= Database related methods ============================= */

    /**
     * Get all food entries ({@link FoodEntry}).
     *
     * @return
     */
    public LiveData<List<FoodEntry>> getAll() {
        return mFoodDao.getAll();
    }

    /**
     * Get list of all food entries for certain date.
     *
     * @param day   int for day
     * @param month int for month.
     * @param year  int for year.
     * @return {@link LiveData} containing {@link List} of {@link FoodEntry} objects for chosen date.
     */
    public LiveData<List<FoodEntry>> getByDay(int day, int month, int year) {
        if (!(day > 0 && month > 0 && year > 0)) {
            throw new InvalidParameterException(TAG + ".getByDay: invalid date received");
        }
        return mFoodDao.getByDay(day, month, year);
    }

    /**
     * Get list of all food entries for certain month.
     *
     * @param month int for month
     * @param year  int for year
     * @return {@link LiveData} containing {@link List} of {@link FoodEntry} objects for chosen month.
     */
    public LiveData<List<FoodEntry>> getByMonth(int month, int year) {
        if (!(month > 0 && year > 0)) {
            throw new InvalidParameterException(TAG + ".getByMonth: invalid date received");
        }
        return mFoodDao.getByMonth(month, year);
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
     * Get all dates with entries available for selected month
     * @param month
     * @param year
     * @return {@link LiveData} containing {@link List} of {@link Integer} days.
     */
    public LiveData<List<Integer>> getDatesByMonth(int month, int year) {
        return mFoodDao.getValidDatesByMonth(month, year);
    }

    public LiveData<List<String>> getAllDistinctMonths() {
        return mFoodDao.getAllMonths();
    }

    /**
     * Save food entry.
     *
     * @param entry {@link FoodEntry} to save.
     */
    public void save(FoodEntry entry) {
        if (!entryIsValid(entry)) {
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
        if (!entryIsValid(entry)) {
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
    private boolean entryIsValid(FoodEntry entry) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(entry.getTime());
        return entry.getName() != null
                && !entry.getName().trim().isEmpty()
                && entry.getTime() > 0
//                && entry.getYear() > 0
//                && entry.getMonth() >= Calendar.JANUARY && entry.getMonth() <= Calendar.DECEMBER
                && entry.getMonth().isValid()
                && entry.getDay() >= calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                && entry.getDay() <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                && entry.getCategory() > 0;
    }
}

// TODO Do I need/want to implement a cache?