package com.nivgelbermann.fooddiarydemo;

import java.io.Serializable;

/**
 * Created by Niv on 14-Sep-17.
 * <p>
 * Represents a row in the Foods table in the database.
 */

public class FoodItem implements Serializable{
    // 14-09-2017
    private static final long serialVersionUID = -931179032962101040L;

//    private final long m_Id;
    private final String mName;
    private final long mHour;
//    private final int mDay;
//    private final int mMonth;
//    private final int mYear;
    private final int mCategory;

//    public FoodItem(long id, String name, int hour, int day, int month, int year) {
//    public FoodItem(long id, String name, long hour) {
    public FoodItem(String name, long hour) {
//        this.m_Id = id;
        mName = name;
        mHour = hour;
//        mDay = day;
//        mMonth = month;
//        mYear = year;
        mCategory = 0;
    }

//    public FoodItem(long m_Id, String name, int hour, int day, int month, int year, int category) {
//    public FoodItem(long m_Id, String name, long hour, int category) {
    public FoodItem(String name, long hour, int category) {
//        this.m_Id = m_Id;
        mName = name;
        mHour = hour;
//        mDay = day;
//        mMonth = month;
//        mYear = year;
        mCategory = category;
    }

//    public long getId() {
//        return m_Id;
//    }

    public String getName() {
        return mName;
    }

    public long getHour() {
        return mHour;
    }

//    public int getDay() {
//        return mDay;
//    }
//
//    public int getMonth() {
//        return mMonth;
//    }
//
//    public int getYear() {
//        return mYear;
//    }

    public int getCategory() {
        return mCategory;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + mName + '\'' +
                ", hour=" + mHour +
                ", category=" + mCategory +
                '}';
    }
}
