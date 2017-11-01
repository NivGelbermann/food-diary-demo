package com.nivgelbermann.fooddiarydemo.models;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * Created by Niv on 14-Sep-17.
 */

public class DateCard implements Serializable {
    private static final long serialVersionUID = -2385710830075534562L;

    private long mDate;
    private String mDayOfWeek;
    private int mMonth;
    private long mYear;

    public DateCard(long date, String dayOfWeek, int month, long year) {
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

    public String getMonthName() {
        switch (mMonth) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                throw new InvalidParameterException("Invalid month stored in DateCard object: " + toString());
        }
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
