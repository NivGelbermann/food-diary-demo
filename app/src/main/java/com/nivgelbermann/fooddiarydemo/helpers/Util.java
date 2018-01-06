package com.nivgelbermann.fooddiarydemo.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines every global constant used in this app.
 */

public class Util {

//    public static final int EPOCH = 2017;
//    public static final int MONTHS_A_YEAR = 12;
//    public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
//    public static final int CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH);
    public static final int MILLISECONDS = 1000;

    /**
     * Utility method for converting time in Epoch format to
     * a formatted String.
     *
     * @param time       long, representing time as seconds since Epoch
     * @param timeFormat String format for return value
     * @return String for time formatted
     */
    public static String formatTime(long time, String timeFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(new Date(time * MILLISECONDS));
    }
}
