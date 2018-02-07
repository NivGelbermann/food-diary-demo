package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * {@link TypeConverter} between {@link Calendar} and String.
 */

public class CalendarConverter {
    private static final String TAG = "CalendarConverter";

    private static final String FORMAT = "YYYY-MM-DDTHH:MM[+-]HH:MM";

    @TypeConverter
    public static Calendar toCalendar(String time) {
        // TODO Requires rigorous testing
        Calendar calendar = Calendar.getInstance();
        try {
            Timestamp timestamp = Timestamp.valueOf(time);
            calendar.setTime(timestamp);
        } catch (Exception exception) {
            Log.d(TAG, "toCalendar: error occurred: " + exception.getMessage());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
            try {
                calendar.setTime(simpleDateFormat.parse(time));
            } catch (ParseException parseException) {
                parseException.printStackTrace();
                Log.d(TAG, "toCalendar: error occurred: " + parseException.getMessage());
                Log.d(TAG, "toCalendar: returning null");
                calendar = null;
            }
        }
        return calendar;
    }

    @TypeConverter
    public static String toString(Calendar time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        return simpleDateFormat.format(time.getTime());
    }
}
