package com.nivgelbermann.fooddiarydemo;

import java.io.Serializable;

/**
 * Created by Niv on 14-Sep-17.
 */

public class DateCard implements Serializable {
    private static final long serialVersionUID = -2385710830075534562L;

    private long mDate;
    private String mDayOfWeek;
    private long mMonth;
    private long mYear;

    public DateCard(long date, String dayOfWeek, long month, long year) {
        mDate = date;
        mDayOfWeek = dayOfWeek;
        mMonth = month;
        mYear = year;
    }

    public long getDate() {
        return mDate;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public long getMonth() {
        return mMonth;
    }

    public long getYear() {
        return mYear;
    }

    @Override
    public String toString() {
        return "DateCard{" +
                ", date=" + mDate +
                ", dayOfWeek='" + mDayOfWeek + '\'' +
                ", month=" + mMonth +
                ", year=" + mYear +
                '}';
    }
}
