package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;

import java.util.Calendar;

/**
 * Defines the schema of a table in {@link Room} for a single food item.
 */

@Entity(tableName = "food")
public class FoodEntry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "time")
    private Calendar mTime;

    @ColumnInfo(name = "category")
    private int mCategory;

    /**
     * Constructor for creating {@link FoodEntry} objects to be inserted into the DB.
     *
     * @param name     Entry name
     * @param time     Entry time and date
     * @param category Entry category
     */
    @Ignore
    public FoodEntry(String name, Calendar time, int category) {
        mName = name;
        mTime = time;
        mCategory = category;
    }

    /**
     * Constructor for creating {@link FoodEntry} objects by the DB.
     * Should not be used manually.
     *
     * @param id       Entry id
     * @param name     Entry name
     * @param time     Entry time and date
     * @param category Entry category
     */
    public FoodEntry(int id, String name, Calendar time, int category) {
        mId = id;
        mName = name;
        mTime = time;
        mCategory = category;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Calendar getTime() {
        return mTime;
    }

    public int getCategory() {
        return mCategory;
    }

    @Override
    public String toString() {
        return "FoodEntry{" +
                "id=" + mId +
                ", name='" + mName + '\'' +
                ", time='" + mTime.getTimeInMillis() + '\'' +
                ", category=" + mCategory +
                '}';
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof FoodEntry) {
            FoodEntry anFoodEntry = (FoodEntry) anObject;

            // TODO This equals method does not compare id!!!!!! Might cause issues later on, used for testing dao for now
            return mName.equals(anFoodEntry.getName())
                    && mTime.equals(anFoodEntry.getTime())
                    && mCategory == anFoodEntry.getCategory();
        }
        return false;
    }
}
