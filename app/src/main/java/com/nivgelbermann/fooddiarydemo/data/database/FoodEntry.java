package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;

import java.io.Serializable;

/**
 * Defines the schema of a table in {@link Room} for a single food item.
 * Also serves as a section-child for SectionedAdapter.
 */

@Entity(tableName = "food")
public class FoodEntry implements Serializable {
    private static final long serialVersionUID = 1759106230692747033L;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "time")
    private long mTime;

    @ColumnInfo(name = "day")
    private int mDay;

    @ColumnInfo(name = "month")
    private int mMonth;

    @ColumnInfo(name = "mYear")
    private int mYear;
//    @Embedded
//    private HistoryFragment.Month mMonth;

    @ColumnInfo(name = "category")
    private int mCategory;

    /**
     * Constructor for creating {@link FoodEntry} objects to be inserted into the DB.
     *
     * @param name     entry name
     * @param time     entry time (hour)
     * @param day      entry mDay
     * @param month    entry mMonth
     * @param year     entry mYear
     * @param category entry category
     */
    @Ignore
    public FoodEntry(String name, long time, int day, int month, int year, int category) {
//    public FoodEntry(String name, long time, int day, HistoryFragment.Month month, int category) {
        mName = name;
        mTime = time;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
        mCategory = category;
    }

    /**
     * Constructor for creating {@link FoodEntry} objects by the DB.
     * Should not be used manually.
     *
     * @param id       entry id
     * @param name     entry name
     * @param time     entry time (hour)
     * @param day      entry mDay
     * @param month    entry mMonth
     * @param year     entry mYear
     * @param category entry category
     */
    public FoodEntry(int id, String name, long time, int day, int month, int year, int category) {
//    public FoodEntry(int id, String name, long time, int day, HistoryFragment.Month month, int category) {
        mId = id;
        mName = name;
        mTime = time;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
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
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }
//    public HistoryFragment.Month getMonth() {
//        return mMonth;
//    }

    public int getYear() {
        return mYear;
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
