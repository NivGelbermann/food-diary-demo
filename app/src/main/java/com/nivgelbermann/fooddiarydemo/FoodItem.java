package com.nivgelbermann.fooddiarydemo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Niv on 14-Sep-17.
 * <p>
 * Represents a row in the Foods table in the database.
 */

public class FoodItem implements Serializable {
    // 14-09-2017
    private static final long serialVersionUID = -931179032962101040L;

    private final String m_Id;
    private final String mName;
    private final long mTime;
    private final int mDay;
    private final int mMonth;
    private final int mYear;
    private final int mCategory;

    public FoodItem(String id, String name, long time, int day, int month, int year) {
//    public FoodItem(long id, String name, long time) {
//    public FoodItem(String name, long time) {
        this.m_Id = id;
        mName = name;
        mTime = time;
        mDay = day;
        mMonth = month;
        mYear = year;
        mCategory = 0;
    }

    public FoodItem(String m_Id, String name, long time, int day, int month, int year, int category) {
//    public FoodItem(long m_Id, String name, long time, int category) {
//    public FoodItem(String name, long time, int category) {
        this.m_Id = m_Id;
        mName = name;
        mTime = time;
        mDay = day;
        mMonth = month;
        mYear = year;
        mCategory = category;
    }

    public String getId() {
        return m_Id;
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

    public int getYear() {
        return mYear;
    }

    public int getCategory() {
        return mCategory;
    }

    /**
     * Utility method for converting time in Epoch format to
     * a formatted String.
     *
     * @param time       long, representing time as seconds since Epoch
     * @param timeFormat String format for return value
     * @return String for time formatted
     */
    public String getFormattedTime(long time, String timeFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(new Date(time * Constants.MILLISECONDS));
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + m_Id +
                ", name='" + mName + '\'' +
                ", time=" + mTime +
                ", day=" + mDay +
                ", month=" + mMonth +
                ", year=" + mYear +
                ", category=" + mCategory +
                '}';
    }
}
