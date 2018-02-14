package com.nivgelbermann.fooddiarydemo.data.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

/**
 * {@link TypeConverter} between {@link Calendar} and String.
 */

public class CalendarConverter {

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

    /* =========== Calendar to String convertion ===========
    private static final String TAG = "CalendarConverter";
    private static final String FORMAT = "YYYY-MM-DDTHH:MM[+-]HH:MM";

     The following code is relevant (though not tested!) for implementing timezones
    @TypeConverter
    public static Calendar toCalendar(String time) {
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
    */
}
