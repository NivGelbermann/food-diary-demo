package com.nivgelbermann.fooddiarydemo;

import java.io.Serializable;

/**
 * Created by Niv on 30-Sep-17.<p>
 * Any fragment implementing this interface will be able to receive data from its activity,
 * should the activity call the fragment's receiveData method.
 */

public interface PassActivityDataToFragment {
    void receiveData(Serializable data);
}