package com.nivgelbermann.fooddiarydemo.data.database;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.nivgelbermann.fooddiarydemo.utilities.Util.MILLISECONDS;

/**
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
    private int mCategoryId;

    public FoodItem() {
        // Initialize an empty FoodItem, to be manually filled in
        mCategoryId = 1;
    }

    public FoodItem(String id, String name, long time, int day, int month, int year, int categoryId) {
        this.m_Id = id;
        mName = name;
        mTime = time;
        mDay = day;
        mMonth = month;
        mYear = year;
        mCategoryId = categoryId;
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

    public int getCategoryId() {
        return mCategoryId;
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
        // Calculate the time of the day
        long hour = mTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay);
        hour = hour - (calendar.getTimeInMillis() / MILLISECONDS);

        // Set item's time to: (new date) + (time of day)
        calendar.set(year, monthOfYear, dayOfMonth);
        mTime = calendar.getTimeInMillis() / MILLISECONDS + hour;

        // Set item's date to new day
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    /**
     * Utility method for converting time in Epoch format to
     * a formatted String.
     *
     * @param time       long, representing time as seconds since Epoch
     * @param timeFormat String format for return value
     * @return String for time formatted
     */
    public static String getFormattedTime(long time, String timeFormat) {
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
                ", category=" + mCategoryId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FoodItem item = (FoodItem) obj;
        return (this == obj
                || (m_Id.equals(item.getId())
                && mName.equals(item.getName())
                && mTime == item.getTime()
                && mDay == item.getDay()
                && mMonth == item.getMonth()
                && mYear == item.getYear()
                && mCategoryId == item.getCategoryId()));
    }


    /**
     * Verify all of FoodItem object's parameters are set and valid.
     *
     * @return true if valid, otherwise false.
     */
    public boolean isValid() {
        return !(mName == null || mName.trim().isEmpty()
                || mTime == 0L || mDay < 0 || mMonth < 0 || mYear < 0 || mCategoryId < 1);
    }
}
