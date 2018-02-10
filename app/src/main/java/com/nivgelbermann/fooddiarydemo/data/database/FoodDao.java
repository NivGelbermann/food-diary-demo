package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * {@link Dao} which provides an API for all data operations
 * on 'food' table in database.
 */

// TODO MAJOR TASK! Test Room!
// https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests.html
// https://commonsware.com/AndroidArch/previews/testing-room

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
    // TODO Test. Parameters could be ints or strings.
    @Query("SELECT * FROM food WHERE datetime(time, '%m') = :month " +
            "AND datetime(time, '%y') = :year")
    LiveData<List<FoodEntry>> getByTime(int month, int year);

    /**
     * Gets all food entries matching a given time.
     *
     * @param day
     * @param month
     * @param year
     * @return {@link LiveData} of {@link List} containing {@link FoodEntry} objects
     */
    // TODO Test. Parameters could be ints or strings.
    @Query("SELECT * FROM food WHERE datetime(time, '%d') = :day " +
            "AND datetime(time, '%m') = :month " +
            "AND datetime(time, '%y') = :year")
    LiveData<List<FoodEntry>> getByTime(int day, int month, int year);

    /**
     * Gets the {@link FoodEntry} matching a given id
     *
     * @param id - given id
     * @return Matching {@link FoodEntry}
     */
    @Query("SELECT * FROM food WHERE _id = :id")
    LiveData<FoodEntry> getById(int id);

    /**
     * Inserts a single {@link FoodEntry} into database
     *
     * @param food FoodEntry to save
     */
    @Insert
    void insert(FoodEntry entry);

    /**
     * Deletes a single entry from database
     *
     * @param food {@link FoodEntry} to delete
     */
    @Delete
    void delete(FoodEntry entry);

//    /**
//     * Inserts a list of {@link FoodEntry} into database
//     *
//     * @param food A list of food entries to save
//     */
//    @Insert
//    void insertAll(FoodEntry... food);
//
//    /**
//     * Deletes a single entry from database
//     *
//     * @param id id of entry to delete
//     */
//    @Query("DELETE FROM food WHERE _id = :id")
//    void delete(int id);
}
