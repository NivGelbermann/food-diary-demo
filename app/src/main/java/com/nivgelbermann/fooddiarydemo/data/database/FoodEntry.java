package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;

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
    private long mTime;

    @ColumnInfo(name = "day")
    private int day;

    @ColumnInfo(name = "month")
    private int month;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "category")
    private int mCategory;

    /**
     * Constructor for creating {@link FoodEntry} objects to be inserted into the DB.
     *
     * @param name     entry name
     * @param time     entry time (hour)
     * @param day      entry day
     * @param month    entry month
     * @param year     entry year
     * @param category entry category
     */
    @Ignore
    public FoodEntry(String name, long time, int day, int month, int year, int category) {
        mName = name;
        mTime = time;
        this.day = day;
        this.month = month;
        this.year = year;
        mCategory = category;
    }

    /**
     * Constructor for creating {@link FoodEntry} objects by the DB.
     * Should not be used manually.
     *
     * @param id       entry id
     * @param name     entry name
     * @param time     entry time (hour)
     * @param day      entry day
     * @param month    entry month
     * @param year     entry year
     * @param category entry category
     */
    public FoodEntry(int id, String name, long time, int day, int month, int year, int category) {
        mId = id;
        mName = name;
        mTime = time;
        this.day = day;
        this.month = month;
        this.year = year;
        mCategory = category;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public long getTime() {
        return mTime;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getCategory() {
        return mCategory;
    }

    @Override
    public String toString() {
        return "FoodEntry{" +
                "id=" + mId +
                ", name='" + mName + '\'' +
                ", time='" + mTime + '\'' +
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
                    && mTime==anFoodEntry.getTime()
                    && mCategory == anFoodEntry.getCategory();
        }
        return false;
    }
}
