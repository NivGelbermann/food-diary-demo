package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Calendar;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * {@link Dao} which provides an API for all data operations
 * on 'food' table in database.
 */

@Dao
public interface FoodDao {

    /**
     * Gets all food entries from database
     *
     * @return {@link LiveData} of {@link List} containing {@link FoodEntry} objects
     */
    @Query("SELECT * FROM food")
    LiveData<List<FoodEntry>> getAll();

    /**
     * Gets all food entries matching a given time.
     *
     * @param month
     * @param year
     * @return {@link LiveData} of {@link List} containing {@link FoodEntry} objects
     */
    @Query("SELECT * FROM food WHERE month = :month " +
            "AND mYear = :year")
    LiveData<List<FoodEntry>> getByMonth(int month, int year);

    /**
     * Gets all food entries matching a given time.
     *
     * @param day
     * @param month
     * @param year
     * @return {@link LiveData} of {@link List} containing {@link FoodEntry} objects
     */
    @Query("SELECT * FROM food WHERE day = :day " +
            "AND month = :month " +
            "AND mYear = :year")
    LiveData<List<FoodEntry>> getByDay(int day, int month, int year);

    /**
     * Gets the {@link FoodEntry} matching a given id
     *
     * @param id - given id
     * @return Matching {@link FoodEntry}
     */
    @Query("SELECT * FROM food WHERE _id = :id")
    LiveData<FoodEntry> getById(int id);

    /**
     * Gets all dates in selected month where food entries exist
     * @param month
     * @param year
     * @return {@link LiveData} of {@link List} containing {@link Integer} dates
     */
    @Query("SELECT DISTINCT day FROM food WHERE month = :month AND mYear = :year ORDER BY day DESC")
    LiveData<List<Integer>> getValidDatesByMonth(int month, int year); // TODO Switch to using HistoryFragment.Month ?

    /**
     *
     * @return
     */
    @Query("SELECT DISTINCT month,mYear FROM food ORDER BY mYear,month ASC")
    LiveData<Calendar> getAllMonths();

    /**
     * Inserts a single entry into database
     *
     * @param entry {@link FoodEntry} to save
     */
    @Insert(onConflict = REPLACE)
    void insert(FoodEntry entry);

    /**
     * Inserts a list of {@link FoodEntry} into database
     *
     * @param food A list of food entries to save
     */
    @Insert(onConflict = REPLACE)
    void insertAll(FoodEntry... food);

    /**
     * Deletes a single entry from database
     *
     * @param entry {@link FoodEntry} to delete
     */
    @Delete
    int delete(FoodEntry entry);

//
//    /**
//     * Deletes a single entry from database
//     *
//     * @param id id of entry to delete
//     */
//    @Query("DELETE FROM food WHERE _id = :id")
//    void delete(int id);
}
