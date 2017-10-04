package com.nivgelbermann.fooddiarydemo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.nivgelbermann.fooddiarydemo.Constants.MILLISECONDS;

/**
 * Created by Niv on 14-Sep-17.
 * <p>
 * Represents a row in the Foods table in the database.
 */

public class FoodItem implements Serializable {
    // 14-09-2017
    private static final long serialVersionUID = -931179032962101040L;

    private String m_Id;
    private String mName;
    private long mTime;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mCategory;

    public FoodItem() {
        // Initialize an empty FoodItem, to be manually filled in
    }

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

    public Calendar getCalendarDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay);
        return calendar;
    }

    public Calendar getCalendarTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTime * MILLISECONDS);
        return calendar;
    }

    public void setId(String id) {
        m_Id = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
    }

    public void setTime(long time) {
        mTime = time;
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
        return dateFormat.format(new Date(time * MILLISECONDS));
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

    /**
     * Verify all of FoodItem object's parameters are set and valid.
     * @return true if valid, otherwise false.
     */
    public boolean isValid() {
        if (mName == null || mName.trim().isEmpty()
                || mTime == 0L || mDay == 0 || mMonth == 0 || mYear == 0 || mCategory < 0) {
            return false;
        }
        return true;
    }
}
