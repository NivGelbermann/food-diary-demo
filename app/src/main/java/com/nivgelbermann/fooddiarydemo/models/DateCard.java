package com.nivgelbermann.fooddiarydemo.models;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Niv on 14-Sep-17.
 */

public class DateCard implements Serializable {
    private static final long serialVersionUID = -2385710830075534562L;

    private int mDate;
    private String mDayOfWeek;
    private int mMonth;
    private int mYear;

    public DateCard(int date, String dayOfWeek, int month, int year) {
        mDate = date;
        mDayOfWeek = dayOfWeek;
        mMonth = month;
        mYear = year;
    }

    public int getDate() {
        return mDate;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public String getMonthName() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, mMonth);
        return String.format("%tb", calendar); // Cheat sheet: https://dzone.com/articles/java-string-format-examples
    }

    @Override
    public String toString() {
        return "DateCard{" +
                "date=" + mDate +
                ", dayOfWeek='" + mDayOfWeek + '\'' +
                ", month=" + mMonth +
                ", year=" + mYear +
                '}';
    }
}
