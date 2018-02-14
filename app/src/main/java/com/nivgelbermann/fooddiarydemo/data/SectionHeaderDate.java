package com.nivgelbermann.fooddiarydemo.data;

import com.intrusoft.sectionedrecyclerview.Section;

import java.util.Calendar;
import java.util.List;

public class SectionHeaderDate implements Section<SectionChildFood> {
    private static final String TAG = "SectionHeaderDate";

    List<SectionChildFood> mChildItems;
    private int mDate;
    private String mDayOfWeek;
    private int mMonth;
    private int mYear;

    public SectionHeaderDate(List<SectionChildFood> childItems, int date, int month, int year) {
        mChildItems = childItems;
        mDate = date;
        mMonth = month;
        mYear = year;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        mDayOfWeek = String.format("%tA", calendar);
    }

    @Override
    public List<SectionChildFood> getChildItems() {
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
        return "SectionHeaderDate{" +
                "date=" + mDate +
                ", dayOfWeek='" + mDayOfWeek + '\'' +
                ", month=" + mMonth +
                ", year=" + mYear +
                '}';
    }
}
