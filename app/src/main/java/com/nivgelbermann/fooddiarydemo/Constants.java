package com.nivgelbermann.fooddiarydemo;

import java.util.Calendar;

/**
 * Created by Niv on 10-Sep-17.
 *
 * This class defines every global constant used in this app.
 */

public class Constants {

    // TODO Decide on an Epoch
    public static final int EPOCH = 2017;
    public static final int MONTHS_A_YEAR = 12;
    public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    public static final int CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH);
    public static final int MILLISECONDS = 1000;

}
