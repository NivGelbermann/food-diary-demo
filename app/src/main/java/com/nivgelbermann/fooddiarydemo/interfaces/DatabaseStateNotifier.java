package com.nivgelbermann.fooddiarydemo.interfaces;

/**
 * Created by Niv on 24-Dec-17.
 */

public interface DatabaseStateNotifier {
    void setDatabaseEmpty();
    void setDatabaseNotEmpty();
}
