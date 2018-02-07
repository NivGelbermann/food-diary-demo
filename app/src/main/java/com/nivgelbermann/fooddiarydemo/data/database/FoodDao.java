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

@Dao
public interface FoodDao {

    /**
     * Gets all food entries from database
     *
     * @return {@link LiveData} of {@link List} containing food entries
     */
    @Query("SELECT * FROM food")
    LiveData<List<FoodEntry>> getAll();

    /**
     * Gets the {@link FoodEntry} matching a given id
     *
     * @param id - given id
     * @return Matching {@link FoodEntry}
     */
    @Query("SELECT * FROM food WHERE _id = :id")
    FoodEntry get(int id);

    /**
     * Inserts a list of {@link FoodEntry} into database
     *
     * @param food A list of food entries to insert
     */
    @Insert
    void insertAll(FoodEntry... food);

    /**
     * Inserts a single {@link FoodEntry} into database
     *
     * @param food FoodEntry to insert
     */
    @Insert
    void insert(FoodEntry food);

    /**
     * Deletes a single entry from database
     *
     * @param food {@link FoodEntry} to delete
     */
    @Delete
    void delete(FoodEntry food);

    /**
     * Deletes a single entry from database
     *
     * @param id id of entry to delete
     */
    @Query("DELETE FROM food WHERE _id = :id")
    void delete(int id);

}
