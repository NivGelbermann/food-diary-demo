package com.nivgelbermann.fooddiarydemo.models;

import com.intrusoft.sectionedrecyclerview.Section;

import java.util.Calendar;
import java.util.List;

public class DateHeader implements Section<FoodItem> {
    private static final String TAG = "DateHeader";

    List<FoodItem> mChildItems;
    private int mDate;
    private String mDayOfWeek;
    private int mMonth;
    private int mYear;

    public DateHeader(List<FoodItem> childItems, int date, int month, int year) {
        mChildItems = childItems;
        mDate = date;
        mMonth = month;
        mYear = year;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        mDayOfWeek = String.format("%tA", calendar);
    }

    @Override
    public List<FoodItem> getChildItems() {
        return mChildItems;
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
        return "DateHeader{" +
                "date=" + mDate +
                ", dayOfWeek='" + mDayOfWeek + '\'' +
                ", month=" + mMonth +
                ", year=" + mYear +
                '}';
    }
}
