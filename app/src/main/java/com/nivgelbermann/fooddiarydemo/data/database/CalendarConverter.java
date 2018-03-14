package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * {@link TypeConverter} between {@link Calendar} and String.
 */

public class CalendarConverter {
    private static final String TAG = "CalendarConverter";

    private static final String TIME_STAMP_FORMAT = "YYYY-MM-DD HH:MM:SS.SSS";

    @TypeConverter
    public static Calendar toCalendar(@NonNull String time) {
        Calendar calendar = null;
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
        try {
            calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            Log.d(TAG, "toCalendar: ParseException caught: " + e.getMessage());
            e.printStackTrace();
        }
        return calendar;
    }

    @TypeConverter
    public static String toString(@NonNull Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
        return sdf.format(calendar.getTime());
    }


    /* =========== Calendar to long convertion ===========
    @TypeConverter
    public static Calendar toCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp); // TODO See whether DateUtils.SECOND_IN_MILLIS adjustment required
        return calendar;
    }

    @TypeConverter
    public static long toLong(Calendar time) {
        return time.getTimeInMillis();
    }
    */

    /* =========== Calendar to String convertion TO SUPPORT TIMEZONES ===========
    private static final String TAG = "CalendarConverter";
    private static final String TIME_STAMP_FORMAT = "YYYY-MM-DDTHH:MM[+-]HH:MM";

     The following code is relevant (though not tested!) for implementing timezones
    @TypeConverter
    public static Calendar toCalendar(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            Timestamp timestamp = Timestamp.valueOf(time);
            calendar.setTime(timestamp);
        } catch (Exception exception) {
            Log.d(TAG, "toCalendar: error occurred: " + exception.getMessage());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_STAMP_FORMAT);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_STAMP_FORMAT);
        return simpleDateFormat.format(time.getTime());
    }
    */
}
